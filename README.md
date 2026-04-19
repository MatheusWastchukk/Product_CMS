# Product Catalog CMS

Sistema fullstack para gerenciamento de produtos, categorias e atributos dinamicos.

O projeto possui uma API REST em Spring Boot e uma interface em Vue 3 com TypeScript. A proposta central e permitir que cada categoria defina seus proprios atributos e que o cadastro de produtos seja montado dinamicamente a partir dessa configuracao.

## Funcionalidades

- Cadastro e listagem de categorias
- Cadastro de atributos por categoria
- Cadastro, listagem, filtro e edicao de produtos
- Atributos dinamicos por categoria
- Tela local de usuarios
- Login simulado no frontend
- Tratamento global de erros na API
- Testes unitarios na camada de service
- Dados iniciais de categorias e atributos ao subir o backend

## Stack

### Backend

- Java 21
- Spring Boot
- Spring Web
- Spring Data JPA
- Bean Validation
- PostgreSQL
- JUnit 5
- Mockito

### Frontend

- Vue 3
- TypeScript
- Vite
- Axios

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
frontend/
  src/
    components/
    services/
    types/
    views/
```

## Configuracao do Ambiente

O backend le as configuracoes de banco a partir de um arquivo `.env` dentro da pasta `backend`.

Crie o arquivo:

```text
backend/.env
```

Use o arquivo [backend/.env.example](backend/.env.example) como referencia:

```env
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/your_database
SPRING_DATASOURCE_USERNAME=your_user
SPRING_DATASOURCE_PASSWORD=your_password
SPRING_JPA_HIBERNATE_DDL_AUTO=update
SPRING_JPA_SHOW_SQL=false
```

O arquivo `.env` real nao deve ser versionado.

## Como Executar

### 1. Banco de Dados

Crie um banco PostgreSQL e configure o arquivo `backend/.env` com a URL, usuario e senha do seu ambiente local.

### 2. Backend

Entre na pasta do backend:

```bash
cd backend
```

Execute a API:

```bash
mvn spring-boot:run
```

Por padrao, a API fica disponivel em:

```text
http://localhost:8080
```

Ao iniciar, a aplicacao cadastra dados iniciais para categorias e atributos.

### 3. Frontend

Entre na pasta do frontend:

```bash
cd frontend
```

Instale as dependencias:

```bash
npm install
```

Execute a interface:

```bash
npm run dev
```

Por padrao, a interface fica disponivel em:

```text
http://localhost:5173
```

## Endpoints Principais

### Categorias

```http
GET /categories
POST /categories
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
```

## Exemplos de Requisicao

### Criar categoria

```json
{
  "name": "Eletronico"
}
```

### Adicionar atributos em uma categoria

```json
{
  "attributes": ["marca", "voltagem", "garantia"]
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
    { "name": "voltagem", "value": "220V" },
    { "name": "garantia", "value": "12 meses" }
  ]
}
```

## Testes

Execute os testes do backend com:

```bash
cd backend
mvn test
```

Os testes cobrem regras de negocio da camada de service, incluindo criacao de produto, categoria inexistente e atributos invalidos.

## Observacoes

- O frontend espera a API em `http://localhost:8080`.
- O CORS do backend permite chamadas vindas de portas locais, como `5173` e `5174`.
- A tela de usuarios e o login atual sao locais no frontend.
