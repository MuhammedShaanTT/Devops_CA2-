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

# Download Prometheus JMX Exporter Agent
ADD https://repo1.maven.org/maven2/io/prometheus/jmx/jmx_prometheus_javaagent/1.0.1/jmx_prometheus_javaagent-1.0.1.jar /usr/local/tomcat/jmx_prometheus_javaagent.jar
COPY prometheus/tomcat-jmx.yml /usr/local/tomcat/tomcat-jmx.yml

# Configure Tomcat to run with the JMX Exporter agent on port 8081
ENV CATALINA_OPTS="-javaagent:/usr/local/tomcat/jmx_prometheus_javaagent.jar=8081:/usr/local/tomcat/tomcat-jmx.yml"

RUN rm -rf /usr/local/tomcat/webapps/*
COPY --from=build /app/target/smartnotes-ai.war /usr/local/tomcat/webapps/ROOT.war

EXPOSE 8080
EXPOSE 8081
CMD ["catalina.sh", "run"]