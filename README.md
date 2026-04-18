# Product Catalog CMS

Mini CMS de produtos criado como projeto de estudo fullstack para entrevista, usando Spring Boot no backend e Vue 3 com TypeScript no frontend.

## Objetivo

O sistema permite cadastrar e listar produtos com atributos dinamicos definidos pela categoria. Por exemplo:

- Eletronico: marca, voltagem, garantia
- Roupa: tamanho, cor, material
- Livro: autor, editora, paginas

Esse fluxo demonstra API REST, modelagem relacional, regras de negocio, tratamento de erros, testes unitarios e um formulario Vue que muda conforme a categoria selecionada.

## Stack

- Java 21
- Spring Boot 3
- Spring Web
- Spring Data JPA
- Bean Validation
- PostgreSQL
- JUnit 5 e Mockito
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

## Banco de dados

O projeto esta configurado para usar PostgreSQL:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/aztech_db
spring.datasource.username=aztech_user
spring.datasource.password=admin
spring.jpa.hibernate.ddl-auto=update
```

Ao subir o backend, a classe `DataSeeder` cria categorias e atributos iniciais.

## Como rodar

### Backend

Entre na pasta do backend:

```bash
cd backend
mvn spring-boot:run
```

A API sobe em:

```text
http://localhost:8080
```

### Frontend

Entre na pasta do frontend:

```bash
cd frontend
npm install
npm run dev
```

A interface sobe em:

```text
http://localhost:5173
```

## Endpoints

### Categorias

```http
GET /categories
POST /categories
GET /categories/{id}/attributes
POST /categories/{id}/attributes
```

Exemplo para criar categoria:

```json
{
  "name": "Eletronico"
}
```

Exemplo para adicionar atributos:

```json
{
  "attributes": ["marca", "voltagem", "garantia"]
}
```

### Produtos

```http
GET /products
GET /products?categoryId=1
GET /products/{id}
POST /products
```

Exemplo para criar produto:

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

## Regras de negocio

- Produto precisa ter uma categoria valida.
- Nome do produto e obrigatorio.
- Preco nao pode ser negativo.
- Atributos enviados no produto precisam existir na categoria selecionada.
- Categoria inexistente retorna erro 404.

## Testes

Os testes unitarios cobrem a camada de service:

- cria produto com categoria e atributos validos
- rejeita produto quando a categoria nao existe
- rejeita atributo que nao pertence a categoria

Execute com:

```bash
cd backend
mvn test
```

## Pontos para explicar na entrevista

- DTOs separam contrato da API das entidades JPA.
- Services concentram regras de negocio e deixam controllers mais simples.
- Repositories encapsulam acesso ao banco com Spring Data JPA.
- O frontend usa `watch` para buscar atributos quando a categoria muda.
- O formulario dinamico monta o payload final com campos fixos e atributos preenchidos.
- Os testes validam comportamento de negocio sem depender de banco real.
