FROM maven:3.8.4-openjdk-11 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

FROM payara/server-full:5.2022.5-jdk11

# Copia o arquivo WAR
COPY --from=build /app/target/desafiotec-1.0-SNAPSHOT.war /opt/payara/deployments/

EXPOSE 8080 4848

CMD ["asadmin", "start-domain", "--verbose"]