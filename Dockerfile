FROM eclipse-temurin:17-jdk AS builder

WORKDIR /app

COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .
COPY src src

RUN sed -i 's/\r$//' gradlew
RUN chmod +x gradlew

RUN ./gradlew clean bootJar --no-daemon

FROM eclipse-temurin:17-jre-jammy

WORKDIR /app

COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 8087

ENTRYPOINT ["java", "-jar", "app.jar", "--spring.profiles.active=docker"]