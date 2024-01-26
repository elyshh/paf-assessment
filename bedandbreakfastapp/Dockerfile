# Starting with this Linux server
FROM maven:3-eclipse-temurin-21 AS builder

## Build the application
# Create a directory call /app
# go into the directory cd /app
WORKDIR /app

# everything after this is in /app
COPY mvnw .
COPY mvnw.cmd .
COPY pom.xml .
COPY .mvn .mvn
COPY src src

# Build the application
RUN mvn package -Dmaven.test.skip=true

FROM openjdk:21-jdk-bullseye

WORKDIR /app 

COPY --from=builder /app/target/assessment-0.0.1-SNAPSHOT.jar bedandbreakfastapp.jar

## Run the application
# Define environment variable 
ENV PORT=8080 
ENV SPRING_DATA_MONGODB_URI=mongodb://localhost:27017/bedandbreakfast
ENV SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/bedandbreakfast

# Expose the port
EXPOSE ${PORT}

# Run the program
ENTRYPOINT SERVER_PORT=${PORT} java -jar bedandbreakfastapp.jar