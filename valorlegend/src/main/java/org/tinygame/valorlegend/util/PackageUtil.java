package org.tinygame.valorlegend.util;

import java.io.File;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Classpath package scanning helpers.
 */
public final class PackageUtil {
    private PackageUtil() {
    }

    public static Set<Class<?>> listSubClazz(String packageName, boolean recursive, Class<?> superClazz) {
        if (superClazz == null) {
            return Collections.emptySet();
        }

        return listClazz(packageName, recursive, superClazz::isAssignableFrom);
    }

    public static Set<Class<?>> listClazz(String packageName, boolean recursive, IClazzFilter filter) {
        if (packageName == null || packageName.trim().isEmpty()) {
            return Collections.emptySet();
        }

        String packagePath = packageName.replace('.', '/');
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        Set<Class<?>> result = new HashSet<>();

        try {
            Enumeration<URL> resources = classLoader.getResources(packagePath);
            while (resources.hasMoreElements()) {
                URL resource = resources.nextElement();
                String protocol = resource.getProtocol();

                if ("file".equalsIgnoreCase(protocol)) {
                    File rootDir = new File(URLDecoder.decode(resource.getFile(), StandardCharsets.UTF_8.name()));
                    result.addAll(listFromDirectory(rootDir, packageName, recursive, filter, classLoader));
                } else if ("jar".equalsIgnoreCase(protocol)) {
                    JarURLConnection conn = (JarURLConnection) resource.openConnection();
                    try (JarFile jarFile = conn.getJarFile()) {
                        result.addAll(listFromJar(jarFile, packageName, packagePath, recursive, filter, classLoader));
                    }
                }
            }
        } catch (Exception ex) {
            throw new RuntimeException("Failed to scan package: " + packageName, ex);
        }

        return result;
    }

    private static Set<Class<?>> listFromDirectory(File rootDir,
                                                   String packageName,
                                                   boolean recursive,
                                                   IClazzFilter filter,
                                                   ClassLoader classLoader) {
        if (rootDir == null || !rootDir.exists() || !rootDir.isDirectory()) {
            return Collections.emptySet();
        }

        Set<Class<?>> result = new HashSet<>();
        File[] files = rootDir.listFiles();
        if (files == null) {
            return result;
        }

        for (File file : files) {
            if (file == null) {
                continue;
            }

            if (file.isDirectory()) {
                if (!recursive) {
                    continue;
                }

                String subPackageName = packageName + "." + file.getName();
                result.addAll(listFromDirectory(file, subPackageName, true, filter, classLoader));
                continue;
            }

            if (!file.isFile() || !file.getName().endsWith(".class")) {
                continue;
            }

            String className = packageName + "." + file.getName().substring(0, file.getName().length() - 6);
            tryLoadClass(className, filter, classLoader, result);
        }

        return result;
    }

    private static Set<Class<?>> listFromJar(JarFile jarFile,
                                             String packageName,
                                             String packagePath,
                                             boolean recursive,
                                             IClazzFilter filter,
                                             ClassLoader classLoader) {
        Set<Class<?>> result = new HashSet<>();
        Enumeration<JarEntry> entries = jarFile.entries();

        while (entries.hasMoreElements()) {
            JarEntry entry = entries.nextElement();
            if (entry == null || entry.isDirectory()) {
                continue;
            }

            String entryName = entry.getName();
            if (!entryName.endsWith(".class") || !entryName.startsWith(packagePath + "/")) {
                continue;
            }

            if (!recursive) {
                String tail = entryName.substring(packagePath.length() + 1);
                if (tail.contains("/")) {
                    continue;
                }
            }

            String className = entryName.substring(0, entryName.length() - 6).replace('/', '.');
            if (!className.startsWith(packageName + ".")) {
                continue;
            }

            tryLoadClass(className, filter, classLoader, result);
        }

        return result;
    }

    private static void tryLoadClass(String className,
                                     IClazzFilter filter,
                                     ClassLoader classLoader,
                                     Set<Class<?>> result) {
        try {
            Class<?> clazzObj = Class.forName(className, false, classLoader);
            if (filter != null && !filter.accept(clazzObj)) {
                return;
            }

            result.add(clazzObj);
        } catch (Throwable ignored) {
            // Ignore classes that cannot be loaded in the current runtime.
        }
    }

    /**
     * Class predicate used by package scanning.
     */
    public interface IClazzFilter {
        boolean accept(Class<?> clazz);
    }
}
