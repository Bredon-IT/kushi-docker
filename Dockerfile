# ------------ Stage 1: Build the JAR using Maven ------------
FROM maven:3.9-amazoncorretto-17 AS build
WORKDIR /app

# Copy pom.xml and source code
COPY pom.xml .
COPY src ./src

# Build the JAR (skip tests for faster build)
RUN mvn -q -DskipTests package


# ------------ Stage 2: Run the Spring Boot app ------------
FROM amazoncorretto:17-alpine
WORKDIR /app

# Copy built JAR from previous stage
COPY --from=build /app/target/*.jar app.jar

# Set default port (App Runner or Docker will inject PORT variable)
ENV PORT=8443
EXPOSE 8443

# Start the Spring Boot app
ENTRYPOINT ["java","-Dserver.port=${PORT}","-jar","/app/app.jar"]
