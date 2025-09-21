#!/bin/bash

# Aguardar o PostgreSQL ficar disponível
echo "Aguardando PostgreSQL iniciar..."
while ! nc -z postgres 5432; do
  sleep 1
done

echo "PostgreSQL está disponível, iniciando configuração do GlassFish..."

# Baixar driver PostgreSQL
wget -O /tmp/postgresql.jar https://jdbc.postgresql.org/download/postgresql-42.2.27.jar
cp /tmp/postgresql.jar /glassfish5/glassfish/lib/

# Iniciar domínio GlassFish
asadmin start-domain

# Configurar JDBC Connection Pool com JNDI
asadmin create-jdbc-connection-pool \
  --datasourceclassname org.postgresql.xa.PGXADataSource \
  --restype javax.sql.XADataSource \
  --property user=postgres:password=password:databaseName=desafiotec:serverName=postgres:portNumber=5432 \
  desafiotecPool

# Configurar JDBC Resource com JNDI
asadmin create-jdbc-resource \
  --connectionpoolid desafiotecPool \
  jdbc/desafiotecDataSource

# Verificar configuração
echo "Testando connection pool..."
asadmin ping-connection-pool desafiotecPool

# Listar recursos configurados para verificação
echo "Recursos JNDI configurados:"
asadmin list-jdbc-resources

# Parar e reiniciar para garantir que tudo está carregado
asadmin stop-domain

echo "Configuração completa! Iniciando GlassFish..."
exec asadmin start-domain -v