FROM maven:3.9.9-eclipse-temurin-8 AS build
WORKDIR /workspace

COPY pom.xml ./
COPY src ./src

RUN mvn -B -DskipTests clean package

FROM eclipse-temurin:8-jre-jammy
WORKDIR /app

COPY --from=build /workspace/target/herostory-1.1.0.jar /app/herostory.jar

ENV APP_MAIN_CLASS=org.tinygame.herostory.ServerMain
EXPOSE 12345

ENTRYPOINT ["sh", "-c", "java -cp /app/herostory.jar ${APP_MAIN_CLASS}"]
