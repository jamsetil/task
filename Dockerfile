# SubTask 1 default: integrations disabled (profile docker)
# SubTask 2: override with -e SPRING_PROFILES_ACTIVE=docker-net (see docker-compose.yml)
FROM eclipse-temurin:17-jdk AS builder

WORKDIR /app

COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .

RUN sed -i 's/\r$//' gradlew && chmod +x gradlew

COPY src src

RUN ./gradlew clean bootJar -x test --no-daemon \
    && cp $(ls build/libs/*.jar | grep -v plain | head -n 1) /app/application.jar

FROM eclipse-temurin:17-jre-jammy

WORKDIR /app

RUN groupadd -r app && useradd -r -g app app
COPY --from=builder /app/application.jar /app/app.jar
USER app

EXPOSE 8087

ENV SPRING_PROFILES_ACTIVE=docker
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
