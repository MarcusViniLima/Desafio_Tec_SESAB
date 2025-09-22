#!/bin/bash

echo "Iniciando Payara Server..."
asadmin start-domain --verbose &

echo "Aguardando 60 segundos para inicialização completa..."
sleep 60

echo "Configurando JNDI do PostgreSQL..."
asadmin --user=admin --passwordfile=/opt/payara/password.txt create-jdbc-connection-pool \
  --datasourceclassname org.postgresql.xa.PGXADataSource \
  --restype javax.sql.XADataSource \
  --property user=postgres:password=password:databaseName=desafiotec:serverName=postgres:portNumber=5432 \
  desafiotecPool

asadmin --user=admin --passwordfile=/opt/payara/password.txt create-jdbc-resource \
  --connectionpoolid desafiotecPool \
  jdbc/desafiotecDataSource

echo "Testando conexão..."
asadmin --user=admin --passwordfile=/opt/payara/password.txt ping-connection-pool desafiotecPool

echo "Configuração completa. Mantendo servidor ativo."
tail -f /dev/null