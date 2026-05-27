# ============================================
# SmartNotes AI - Multi-Stage Docker Build
# ============================================

# Stage 1: Build the application using Maven
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:resolve
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Deploy to Tomcat
FROM tomcat:9-jdk17
RUN rm -rf /usr/local/tomcat/webapps/*
COPY --from=build /app/target/smartnotes-ai.war /usr/local/tomcat/webapps/ROOT.war
EXPOSE 8080
CMD ["catalina.sh", "run"]