FROM openjdk:17-alpine

WORKDIR /app

# Copy your project directory (replace with your actual directory structure)
COPY . .

# Copy the generated JAR
COPY target/*.jar app.jar

# Expose the port where your application listens (usually 8080)
EXPOSE 8080

# Define the entrypoint to run your application
ENTRYPOINT ["java", "-jar", "target/app.jar"]