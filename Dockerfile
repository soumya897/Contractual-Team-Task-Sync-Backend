# Use official OpenJDK image
FROM eclipse-temurin:24-jdk

# Set working directory
WORKDIR /app

# Copy project files
COPY . .

# Build the application
RUN ./mvnw clean package -DskipTests

# Expose port
EXPOSE 8080

# Run the jar
CMD ["java", "-jar", "target/ctts-0.0.1-SNAPSHOT.jar"]
