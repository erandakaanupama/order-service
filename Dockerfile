# Stage 1: extract layers
FROM eclipse-temurin:17-jre-alpine AS builder
WORKDIR /builder
ARG JAR_FILE=build/libs/*[^plain].jar
COPY ${JAR_FILE} application.jar
RUN java -Djarmode=layertools -jar application.jar extract --destination extracted

# Stage 2: minimal runtime
FROM eclipse-temurin:17-jre-alpine
RUN addgroup -S spring && adduser -S spring -G spring
USER spring
WORKDIR /app
COPY --from=builder /builder/extracted/dependencies/          ./
COPY --from=builder /builder/extracted/spring-boot-loader/    ./
COPY --from=builder /builder/extracted/snapshot-dependencies/ ./
COPY --from=builder /builder/extracted/application/           ./
EXPOSE 8080
ENV DB_HOST="" DB_PORT="3306" DB_NAME="" DB_USERNAME="" DB_PASSWORD=""
ENV SPRING_CLOUD_CONFIG_URI="http://localhost:8090"
ENV SPRING_PROFILES_ACTIVE="dev"
ENTRYPOINT ["java", "org.springframework.boot.loader.launch.JarLauncher"]
