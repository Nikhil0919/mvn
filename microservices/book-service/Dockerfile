# Runtime image using the prebuilt JAR from target/
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
# Copy the already-built jar produced by Maven on the host
COPY target/*.jar /app/app.jar
EXPOSE 8081
ENTRYPOINT ["java","-jar","/app/app.jar"]
