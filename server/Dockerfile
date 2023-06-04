FROM maven:3.8.4-openjdk-17-slim AS build

WORKDIR /app

COPY ../ .

RUN mvn clean package

FROM openjdk:17

WORKDIR /app

COPY --from=build /app/server/target/app-jar-with-dependencies.jar .

CMD ["java", "-jar", "app-jar-with-dependencies.jar", "docker"]
