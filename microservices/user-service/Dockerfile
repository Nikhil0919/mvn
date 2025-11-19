# Runtime image using the prebuilt JAR from target/
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
COPY target/*.jar /app/app.jar
EXPOSE 8082
ENTRYPOINT ["java","-jar","/app/app.jar"]
