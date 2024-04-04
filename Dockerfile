FROM openjdk:17-alpine

WORKDIR /app

# Copy your project directory (replace with your actual directory structure)
COPY . .

# Build the JAR using Maven
RUN ./mvnw clean package

# Copy the generated JAR
COPY target/*.jar app.jar

# Expose the port where your application listens (usually 8080)
EXPOSE 8080

# Define the entrypoint to run your application
ENTRYPOINT ["java", "-jar", "app.jar"]