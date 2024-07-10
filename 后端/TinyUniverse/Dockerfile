
FROM maven:3.8.1-jdk-8-slim as builder

# Copy local code to the container image.
WORKDIR /TinyUniverse
COPY pom.xml .
COPY ailu-common .
COPY ailu-server .
COPY ailu-manage .
COPY ailu-pojo .

# Build a release artifact.
RUN mvn package -DskipTests

# Run the web service on container startup.
CMD ["java","-jar","/ailu-server/target/ailu-server-0.0.1-SNAPSHOT.jar","--spring.profiles.active=prod"]
