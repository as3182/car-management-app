# Use an official Maven image to build the application
FROM maven:3.9.5-eclipse-temurin-17 AS builder

# Set the working directory inside the container
WORKDIR /app

# Copy only the pom.xml to download dependencies
COPY pom.xml .

# Download dependencies without building the project
RUN mvn dependency:go-offline -B

# Copy the rest of the application code
COPY . .

# Build the project
RUN mvn clean package -DskipTests

# Use an official Java runtime for the application
FROM eclipse-temurin:17-jdk

# Set the working directory inside the container
WORKDIR /app

# Copy the built JAR from the previous stage
COPY --from=builder /app/target/*.jar app.jar

# Expose the application's port
EXPOSE 8080

# Run the application
CMD ["java", "-jar", "app.jar"]
