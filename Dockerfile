# Specify java runtime base image
FROM amazoncorretto:21-alpine

# Use a build argument for version
ARG app_version=1.0.0

# Set up working directory in the container
RUN mkdir -p /opt/laa-fee-scheme-api/
WORKDIR /opt/laa-fee-scheme-api/

# Copy the built JAR file to the container
COPY scheme-service/build/libs/scheme-service-${app_version}.jar laa-fee-scheme-api.jar

# Create a group and non-root user
RUN addgroup -S appgroup && adduser -u 1001 -S appuser -G appgroup

# Set the default user
USER 1001

# Set environment variables
ENV TZ=Europe/London
ENV JAVA_TOOL_OPTIONS="-XX:InitialRAMPercentage=50.0 -XX:MaxRAMPercentage=80.0"

# Expose the port that the application will run on
EXPOSE 8080

# Run the JAR file
CMD ["java", "-jar", "laa-fee-scheme-api.jar"]
