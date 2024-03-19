# Stage 1: Build the application
FROM gradle:jdk21 as build

WORKDIR /home/gradle/src
COPY --chown=gradle:gradle . .
RUN gradle build --no-daemon

# Stage 2: Run the application
FROM openjdk:21-jdk-slim as run

EXPOSE 8888
WORKDIR /app

RUN mkdir /app/libs
COPY --from=build /home/gradle/src/build/libs/*.jar /app/libs/
RUN set -eux; \
    shortestFile=$(ls /app/libs/*.jar | awk '{print length, $0}' | sort -n | head -1 | cut -d " " -f2-); \
    cp "$shortestFile" /app/app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]