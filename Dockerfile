FROM maven:3.8.1-openjdk-8 AS build

WORKDIR /app
COPY pom.xml .
COPY src ./src

# Baixa as dependências incluindo Derby
RUN mvn dependency:copy-dependencies -DoutputDirectory=./lib

RUN mvn clean package -DskipTests

# Imagem final com GlassFish + Derby
FROM payara/micro:5.2022.4-jdk8

# Copia o WAR e dependências do Derby
COPY --from=build /app/target/desafiotec-1.0-SNAPSHOT.war /opt/payara/deployments/
COPY --from=build /app/lib/derby-*.jar /opt/payara/libs/

# Cria diretório para dados do Derby
RUN mkdir -p /opt/payara/derby_data

EXPOSE 8080

CMD ["--deploymentDir", "/opt/payara/deployments", "--noCluster", "--addLibs", "/opt/payara/libs/"]