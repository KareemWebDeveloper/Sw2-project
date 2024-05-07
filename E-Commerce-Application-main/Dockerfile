# Use a specific Java version
# FROM openjdk:8
FROM openjdk:17-alpine
#FROM openjdk:21-slim 
# Copy just the pom.xml to cache dependencies
# COPY ./target/pom.xml /tmp/
# RUN cd /tmp && \
#     mvn dependency:go-offline

# Copy the actual JAR file
COPY ./target/*.jar app.jar

# Set the entrypoint to run the application
ENTRYPOINT ["java","-jar","app.jar"]
