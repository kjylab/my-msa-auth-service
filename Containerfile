FROM eclipse-temurin:21.0.9_10-jdk-jammy AS build
WORKDIR /app

COPY gradlew .
COPY gradle gradle
COPY build.gradle.kts .
COPY settings.gradle.kts .
COPY gradle.properties .

COPY auth/build.gradle.kts auth/
COPY auth-service/build.gradle.kts auth-service/

RUN ./gradlew dependencies --no-daemon -Pkotlin.incremental=false

COPY . .

RUN ./gradlew :auth-service:bootJar -x test --no-daemon -Pkotlin.incremental=false

FROM eclipse-temurin:21.0.9_10-jre-jammy
WORKDIR /app

RUN useradd -ms /bin/bash springuser
USER springuser

COPY --from=build /app/auth-service/build/libs/auth-service.jar app.jar

ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=staging", "-XX:+UseContainerSupport", "-XX:MaxRAMPercentage=75.0", "app.jar"]

EXPOSE 8080 9090
