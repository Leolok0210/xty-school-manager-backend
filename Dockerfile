FROM eclipse-temurin:8-jre

WORKDIR /app

COPY target/xty-school-manager.jar /app/app.jar

ENV SPRING_PROFILES_ACTIVE=dev

EXPOSE 8080

HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8080/ || exit 1

ENTRYPOINT ["java", "-jar", "/app/app.jar"]