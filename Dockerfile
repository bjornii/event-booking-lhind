FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

COPY pom.xml .
COPY event-booking-core/pom.xml event-booking-core/pom.xml
COPY event-booking-api/pom.xml event-booking-api/pom.xml
RUN mvn -q -pl event-booking-api -am dependency:go-offline

COPY event-booking-core/src event-booking-core/src
COPY event-booking-api/src event-booking-api/src
RUN mvn -q clean package -pl event-booking-api -am -DskipTests

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/event-booking-api/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]