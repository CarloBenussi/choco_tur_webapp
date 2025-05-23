FROM maven:latest

ENV CHOCOTUR_EMAIL_PASSWORD placeholder
ENV GOOGLE_CLIENT_ID placeholder
ENV SPRING_JWT_SECRET_KEY placeholder
ARG GOOGLE_CLOUD_PROJECT=placeholder

WORKDIR /app

# Copy your project directory (replace with your actual directory structure)
COPY . .

# Build the JAR using Maven
RUN mvn clean package

# Expose the port where your application listens (usually 8080)
EXPOSE 8080

# Define the entrypoint to run your application
ENTRYPOINT ["java", "-jar", "target/choco_tur-0.0.1-SNAPSHOT.jar"]