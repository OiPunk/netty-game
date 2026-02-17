FROM maven:3.9.9-eclipse-temurin-8 AS build
WORKDIR /workspace

COPY pom.xml ./
COPY src ./src

RUN mvn -B -DskipTests clean package

FROM eclipse-temurin:8-jre-jammy
WORKDIR /app

COPY --from=build /workspace/target/valorlegend-1.1.0.jar /app/valorlegend.jar

ENV APP_MAIN_CLASS=org.tinygame.herostory.ServerMain
EXPOSE 12345

ENTRYPOINT ["sh", "-c", "java -cp /app/valorlegend.jar ${APP_MAIN_CLASS}"]
