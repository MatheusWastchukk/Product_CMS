# Product Catalog CMS

Sistema fullstack para gerenciamento de produtos, categorias, atributos dinâmicos e usuários.

O projeto possui uma API REST em Spring Boot, uma interface em Vue 3 com TypeScript, autenticação própria, integração assíncrona com RabbitMQ e um microsserviço de auditoria para consumir eventos de produtos.

## Funcionalidades

- Autenticação real com usuário, senha criptografada e token de sessão
- Cadastro, edição, listagem e exclusão de usuários
- Cadastro, listagem e exclusão de categorias
- Cadastro de múltiplos atributos por categoria
- Cadastro, edição, listagem, filtro e exclusão de produtos
- Formulário dinâmico de produto baseado nos atributos da categoria
- Publicação de eventos de produto no RabbitMQ
- Microsserviço de auditoria consumindo eventos assíncronos
- Tratamento global de erros na API
- Testes unitários na camada de service
- Docker Compose com PostgreSQL, RabbitMQ, backend, frontend e audit-service
- Pipeline de CI/CD com GitHub Actions

## Stack

### Backend

- Java 21
- Spring Boot
- Spring Web
- Spring Data JPA
- Bean Validation
- PostgreSQL
- RabbitMQ
- BCrypt para criptografia de senha
- JUnit 5
- Mockito

### Frontend

- Vue 3
- TypeScript
- Vite
- Axios
- Lucide Icons

### Infraestrutura

- Docker
- Docker Compose
- RabbitMQ Management
- GitHub Actions

## Estrutura

```text
backend/
  src/main/java/com/aztech/productcms/
    config/
    controller/
    dto/
    exception/
    model/
    repository/
    service/
audit-service/
  src/main/java/com/aztech/audit/
    config/
    dto/
    service/
frontend/
  src/
    components/
    services/
    types/
    views/
```

## Configuração Local

O backend lê as configurações de banco e RabbitMQ a partir de um arquivo `.env` dentro da pasta `backend`.

Crie o arquivo:

```text
backend/.env
```

Use o arquivo [backend/.env.example](backend/.env.example) como referência:

```env
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/your_database
SPRING_DATASOURCE_USERNAME=your_user
SPRING_DATASOURCE_PASSWORD=your_password
SPRING_JPA_HIBERNATE_DDL_AUTO=update
SPRING_JPA_SHOW_SQL=false
SPRING_JPA_OPEN_IN_VIEW=false
SPRING_RABBITMQ_HOST=localhost
SPRING_RABBITMQ_PORT=5672
SPRING_RABBITMQ_USERNAME=guest
SPRING_RABBITMQ_PASSWORD=guest
```

O arquivo `.env` real não deve ser versionado.

## Como Executar Com Docker

Na raiz do projeto, crie um arquivo `.env` usando [.env.example](.env.example) como referência:

```env
POSTGRES_DB=aztech_db
POSTGRES_USER=aztech_user
POSTGRES_PASSWORD=admin
```

Suba todos os serviços:

```bash
docker compose up --build
```

Se o container do PostgreSQL jÃ¡ tiver sido criado antes com outra senha, o volume preserva a credencial antiga. Nesse caso, ajuste o `.env` para a senha anterior ou recrie o banco com:

```bash
docker compose down -v
docker compose up --build
```

Serviços disponíveis:

```text
Frontend: http://localhost:5173
Backend: http://localhost:8080
RabbitMQ Management: http://localhost:15672
```

Credenciais padrão do RabbitMQ no Docker Compose:

```text
Usuário: guest
Senha: guest
```

O backend cria um usuário inicial da aplicação para acesso administrativo. Novos usuários são criados pela tela de usuários e recebem a senha padrão.

Credencial inicial da aplicação:

```text
Usuário: admin
Senha: senha
```

## Como Executar Manualmente

### 1. Banco de Dados

Crie um banco PostgreSQL e configure o arquivo `backend/.env` com a URL, usuário e senha do seu ambiente local.

### 2. RabbitMQ

Se não estiver usando Docker Compose, mantenha um RabbitMQ local disponível na porta `5672`.

### 3. Backend

Entre na pasta do backend:

```bash
cd backend
```

Execute a API:

Windows:

```powershell
.\mvnw.cmd spring-boot:run
```

Linux/macOS:

```bash
./mvnw spring-boot:run
```

Por padrão, a API fica disponível em:

```text
http://localhost:8080
```

### 4. Audit Service

O `audit-service` é um microsserviço separado que consome eventos de produtos publicados no RabbitMQ.

Entre na pasta do serviço:

```bash
cd audit-service
```

Execute com Maven:

```bash
mvn spring-boot:run
```

No Windows, caso não tenha Maven instalado, é possível usar o Maven Wrapper do backend a partir da pasta `audit-service`:

```powershell
..\backend\mvnw.cmd spring-boot:run
```

### 5. Frontend

Entre na pasta do frontend:

```bash
cd frontend
```

Instale as dependências:

```bash
npm install
```

Execute a interface:

```bash
npm run dev
```

Por padrão, a interface fica disponível em:

```text
http://localhost:5173
```

## Autenticação

O login é feito com usuário e senha. Após a autenticação, o backend retorna um token de sessão, e o frontend envia esse token no cabeçalho `Authorization` das próximas requisições.

Fluxo principal:

```text
POST /auth/login
GET /auth/me
PUT /auth/me
POST /auth/logout
```

As senhas são armazenadas com BCrypt. A tabela de usuários pertence ao backend, não ao `localStorage` do navegador.

## Endpoints Principais

### Autenticação

```http
POST /auth/login
GET /auth/me
PUT /auth/me
POST /auth/logout
```

### Usuários

```http
GET /users
POST /users
PUT /users/{id}
DELETE /users/{id}
```

### Categorias

```http
GET /categories
POST /categories
DELETE /categories/{id}
GET /categories/{id}/attributes
POST /categories/{id}/attributes
```

### Produtos

```http
GET /products
GET /products?categoryId=1
GET /products/{id}
POST /products
PUT /products/{id}
DELETE /products/{id}
```

## Eventos Assíncronos

Quando um produto é criado, editado ou excluído, o backend publica um evento no exchange `product.events`.

Eventos publicados:

```text
product.created
product.updated
product.deleted
```

O `audit-service` consome a fila `product.audit` e registra os eventos recebidos nos logs. Essa separação demonstra uma base simples de arquitetura orientada a eventos e microsserviços.

## Exemplos de Requisição

### Criar categoria

```json
{
  "name": "Eletrônico"
}
```

### Adicionar atributos em uma categoria

```json
{
  "attributes": [
    { "name": "marca", "type": "string" },
    { "name": "garantia", "type": "int" }
  ]
}
```

### Criar produto

```json
{
  "name": "Notebook X",
  "description": "Notebook para trabalho",
  "price": 4500.00,
  "categoryId": 1,
  "attributes": [
    { "name": "marca", "value": "Dell" },
    { "name": "garantia", "value": "12" }
  ]
}
```

## Testes

Execute os testes do backend com:

Windows:

```powershell
cd backend
.\mvnw.cmd test
```

Linux/macOS:

```bash
cd backend
./mvnw test
```

Os testes cobrem regras de negócio da camada de service, incluindo criação de produto, categoria inexistente e atributos inválidos.

## CI/CD

O projeto possui um workflow em [.github/workflows/ci.yml](.github/workflows/ci.yml) com três validações:

- testes do backend;
- build do frontend;
- build do `audit-service`.

Esse pipeline roda em pull requests, pushes na branch `main` e também manualmente via `workflow_dispatch`.

## Observações

- O frontend espera a API em `http://localhost:8080`.
- O CORS do backend permite chamadas vindas de portas locais, como `5173` e `5174`.
- O RabbitMQ é opcional para a API continuar respondendo localmente, mas é necessário para demonstrar a integração assíncrona completa.
- O projeto usa Docker Compose para facilitar a execução do ambiente completo.
