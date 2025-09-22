FROM maven:3-openjdk-11 AS builder

# Define o diretório de trabalho
WORKDIR /app

# Copia o pom.xml e baixa as dependências
COPY pom.xml .
RUN mvn dependency:go-offline

# Copia o código fonte
COPY src ./src

# Compila a aplicação e cria o arquivo WAR
RUN mvn clean package -DskipTests

# Stage 2: Deploy to GlassFish
# Use a imagem oficial do GlassFish da Eclipse
FROM eclipse-temurin:11-jdk-jammy

# Baixa e instala o GlassFish
ARG GLASSFISH_VERSION=6.2.5
ARG GLASSFISH_ZIP=glassfish-${GLASSFISH_VERSION}.zip
ARG GLASSFISH_URL=https://download.eclipse.org/ee4j/glassfish/glassfish-6.2.5.zip

RUN apt-get update && apt-get install -y wget unzip && \
    wget -q ${GLASSFISH_URL} -O /tmp/${GLASSFISH_ZIP} && \
    unzip -q /tmp/${GLASSFISH_ZIP} -d /opt && \
    rm /tmp/${GLASSFISH_ZIP}

ENV GLASSFISH_HOME=/opt/glassfish6
ENV PATH=$PATH:$GLASSFISH_HOME/bin

# Copia o WAR da aplicação
COPY --from=builder /app/target/desafiotec-1.0-SNAPSHOT.war ${GLASSFISH_HOME}/glassfish/domains/domain1/autodeploy/

# Copia o driver do Derby
COPY --from=builder /root/.m2/repository/org/apache/derby/derby/10.14.2.0/derby-10.14.2.0.jar ${GLASSFISH_HOME}/glassfish/domains/domain1/lib/

# Cria um script para configurar a fonte de dados do Derby
RUN echo '#!/bin/sh' > /tmp/setup_derby.sh && \
    echo 'echo "Configuring Derby JDBC resource..."' >> /tmp/setup_derby.sh && \
    echo "${GLASSFISH_HOME}/bin/asadmin create-jdbc-connection-pool \
        --datasourceclassname org.apache.derby.jdbc.ClientDataSource \
        --restype javax.sql.DataSource \
        --property databaseName=/opt/payara/derby_data/desafiodb:createDatabase=true DerbyPool" >> /tmp/setup_derby.sh && \
    echo "${GLASSFISH_HOME}/bin/asadmin create-jdbc-resource \
        --connectionpoolid DerbyPool jdbc/meuDerbyDataSource" >> /tmp/setup_derby.sh && \
    echo 'echo "Derby JDBC resource configured."' >> /tmp/setup_derby.sh && \
    chmod +x /tmp/setup_derby.sh

EXPOSE 8080 4848

# Inicia o GlassFish e o script de configuração
CMD ["/bin/bash", "-c", "${GLASSFISH_HOME}/bin/asadmin start-domain && /tmp/setup_derby.sh && tail -f /dev/null"]