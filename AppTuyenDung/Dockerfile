# Stage 1: Build WAR
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline -q
COPY src ./src
RUN mvn clean package -DskipTests -q

# Stage 2: Deploy on Tomcat 9 (tương thích javax.servlet-api 4.x)
FROM tomcat:9.0-jdk17
RUN rm -rf /usr/local/tomcat/webapps/ROOT

COPY --from=build /app/target/AppTuyenDung.war /usr/local/tomcat/webapps/AppTuyenDung.war

EXPOSE 8080
CMD ["catalina.sh", "run"]
