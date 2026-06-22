# CI에서 ./gradlew :auth-service:bootJar 를 먼저 실행 후 Docker 빌드
# Docker는 레이어 추출 + 런타임 패키징만 담당

# ===== Stage 1: layered jar extraction =====
FROM eclipse-temurin:21-jre-alpine AS layers
WORKDIR /app
COPY auth-service/build/libs/*.jar app.jar
RUN java -Djarmode=layertools -jar app.jar extract

# ===== Stage 2: runtime =====
FROM eclipse-temurin:21-jre-alpine
RUN apk upgrade --no-cache
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring
WORKDIR /app

COPY --from=layers /app/dependencies/ ./
COPY --from=layers /app/spring-boot-loader/ ./
COPY --from=layers /app/snapshot-dependencies/ ./
COPY --from=layers /app/application/ ./

EXPOSE 8080 9090
ENV JAVA_OPTS="-XX:+UseG1GC -XX:MaxRAMPercentage=75.0"
ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS org.springframework.boot.loader.launch.JarLauncher $@", "--"]
