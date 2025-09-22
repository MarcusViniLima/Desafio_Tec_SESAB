<h1>📋 Sistema de Cadastro de Pessoas e Endereços</h1>
<p>Sistema web desenvolvido em <strong>Java EE</strong> para gerenciamento de pessoas e seus endereços, com interface moderna usando PrimeFaces.</p>

<h2>🚀 Tecnologias Utilizadas</h2>
<p align="left">
  <img src="https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=docker&logoColor=white" alt="Docker"/>
  <img src="https://img.shields.io/badge/GlassFish-FF6600?style=for-the-badge&logo=java&logoColor=white" alt="GlassFish"/>
  <img src="https://img.shields.io/badge/PrimeFaces-39A9DB?style=for-the-badge&logo=java&logoColor=white" alt="PrimeFaces"/>
  <img src="https://img.shields.io/badge/JSF-2E7D32?style=for-the-badge&logo=java&logoColor=white" alt="JSF"/>
  <img src="https://img.shields.io/badge/Hibernate-59666C?style=for-the-badge&logo=hibernate&logoColor=yellow" alt="Hibernate"/>
  <img src="https://img.shields.io/badge/Maven-C71A36?style=for-the-badge&logo=apache-maven&logoColor=white" alt="Maven"/>
  <img src="https://img.shields.io/badge/Java%20EE-007396?style=for-the-badge&logo=java&logoColor=white" alt="JavaEE"/>
  <img src="https://img.shields.io/badge/MVC%20Pattern-FF5722?style=for-the-badge&logo=architecture&logoColor=white" alt="MVC"/>
  <img src="https://img.shields.io/badge/PostgreSQL-316192?style=for-the-badge&logo=postgresql&logoColor=white" alt="PostgreSQL"/>
  <img src="https://img.shields.io/badge/SQLite-003B57?style=for-the-badge&logo=sqlite&logoColor=white" alt="SQLite"/>
</p>


<h2>⭐ Funcionalidades</h2>

<h3>👥 Gestão de Pessoas</h3>
<ul>
  <li>✅ Cadastro, edição e exclusão de pessoas</li>
  <li>✅ Campos: Nome, CPF, Email, Data de Criação, Perfil (ADM/USER)</li>
  <li>✅ Validação de dados e prevenção de duplicatas</li>
  <li>✅ Filtros avançados por nome, CPF e data</li>
</ul>

<h3>🏠 Gestão de Endereços</h3>
<ul>
  <li>✅ Sistema de endereços compartilhados (Many-to-Many)</li>
  <li>✅ Busca por CEP ou logradouro</li>
  <li>✅ Associação múltipla de endereços às pessoas</li>
  <li>✅ Validação de endereços duplicados</li>
</ul>

<h3>🔍 Recursos Avançados</h3>
<ul>
  <li>✅ Interface responsiva com PrimeFaces</li>
  <li>✅ Diálogos modais para operações</li>
  <li>✅ Confirmações de exclusão</li>
  <li>✅ Paginação e ordenação de dados</li>
  <li>✅ Mensagens de feedback para o usuário</li>
</ul>

<h2>🏗️ Estrutura do Projeto</h2>

<pre>
desafiotec/
├── src/
│   ├── main/
│   │   ├── java/com/sesab/desafiotec/
│   │   │   ├── controllers/          # Controladores JSF
│   │   │   ├── models/               # Entidades JPA
│   │   │   └── controllers/util/     # Utilidades
│   │   └── webapp/
│   │       ├── resources/            # CSS, JS, imagens
│   │       ├── WEB-INF/              # Configurações
│   │       └── pessoa/               # Páginas JSF
├── target/                           # Build do Maven
├── docker-compose.yml                # Orquestração de containers
├── Dockerfile                        # Imagem Docker
├── startup.sh                        # Script de inicialização
└── pom.xml                           # Dependências Maven
</pre>

<h2>🐳 Execução com Docker</h2>

<h3>Pré-requisitos</h3>
<ul>
  <li>Docker instalado</li>
  <li>Docker Compose instalado</li>
</ul>

<pre>
# Clone o repositório
git clone &lt;url-do-repositorio&gt;
cd desafiotec

# Execute com Docker Compose
docker-compose up --build

# Ou para executar em segundo plano
docker-compose up --build -d

# Parar os containers
docker-compose down

# Parar e remover volumes
docker-compose down -v
</pre>

<h2>⚙️ Configuração Manual do Banco de Dados</h2>

<pre><code class="language-bash">
# Acessar o container
docker exec -it desafiotec-glassfish-1 /bin/bash

# Configurar connection pool
asadmin create-jdbc-connection-pool \
  --datasourceclassname org.postgresql.xa.PGXADataSource \
  --restype javax.sql.XADataSource \
  --property user=postgres:password=password:databaseName=desafiotec:serverName=postgres:portNumber=5432 \
  desafiotecPool

# Criar resource JNDI
asadmin create-jdbc-resource \
  --connectionpoolid desafiotecPool \
  jdbc/desafiotecDataSource

# Testar conexão
asadmin ping-connection-pool desafiotecPool
</code></pre>

<h3>📋 Acessos após subir os containers:</h3>
<table>
  <tr><th>Serviço</th><th>URL</th><th>Credenciais</th></tr>
  <tr><td>Aplicação</td><td><a href="http://localhost:8080">http://localhost:8080</a></td><td>-</td></tr>
  <tr><td>Admin Console</td><td><a href="http://localhost:4848">http://localhost:4848</a></td><td>Usuário: admin / Senha: adminpass</td></tr>
  <tr><td>PostgreSQL</td><td>localhost:5432</td><td>Usuário: postgres / Senha: password</td></tr>
</table>

<h2>🎯 Entidades do Sistema</h2>

<h3>👤 Entidade Pessoa</h3>
<pre>
@Entity
public class Pessoa {
    @Id 
    @GeneratedValue
    private Long id;
    
    private String nome;
    private String cpf;
    private String email;
    private Date dataCriacao;
    private String perfil; // ADM ou USER
    
    @ManyToMany
    private List&lt;Endereco&gt; enderecos;
}
</pre>

<h3>🏠 Entidade Endereco</h3>
<pre>
@Entity
public class Endereco {
    @Id 
    @GeneratedValue
    private Long id;
    
    private String logradouro;
    private String cep;
    
    @ManyToMany(mappedBy = "enderecos")
    private List&lt;Pessoa&gt; pessoas;
}
</pre>

<h3>📊 Relacionamentos</h3>
<ul>
  <li>Pessoa ↔ Endereco: Relacionamento Many-to-Many</li>
  <li>Um endereço pode pertencer a múltiplas pessoas</li>
  <li>Uma pessoa pode ter múltiplos endereços</li>
  <li>Tabela de junção automática gerenciada pelo JPA</li>
</ul>

<h2>🤝 Contribuição</h2>
<ol>
  <li>Fork o projeto</li>
  <li>Crie uma branch para sua feature (<code>git checkout -b feature/AmazingFeature</code>)</li>
  <li>Commit suas mudanças (<code>git commit -m 'Add some AmazingFeature'</code>)</li>
  <li>Push para a branch (<code>git push origin feature/AmazingFeature</code>)</li>
  <li>Abra um Pull Request</li>
</ol>

<h2>📝 Licença</h2>
<p>Este projeto está sob licença MIT. Veja o arquivo LICENSE para mais detalhes.</p>

<h2>🆘 Suporte</h2>
<p>Em caso de problemas:</p>
<ul>
  <li>Verifique os logs do Payara: <code>docker logs desafiotec-glassfish-1</code></li>
  <li>Confirme se o banco está rodando: <code>docker ps</code></li>
  <li>Valide as configurações JNDI no Admin Console</li>
  <li>Verifique a conexão com o banco: <code>docker exec -it desafiotec-postgres-1 psql -U postgres -d desafiotec</code></li>
</ul>

<p><strong>Desenvolvido por <a href="https://www.linkedin.com/in/marcuslameu/" target="_blank">Marcus Lima</a> - Sistema de Cadastro de Pessoas e Endereços 🚀</strong></p>

