# Projeto de Estudo para Entrevista — AZ Tech

## Objetivo
Construir um projeto pequeno, mas com cara profissional, alinhado com a vaga de **Desenvolvedor(a) Fullstack Júnior** da AZ Tech, para demonstrar:

- conhecimento prático em **Java + Spring Boot**
- construção e consumo de **APIs REST**
- uso de **Vue.js + TypeScript** no front-end
- noções de **testes unitários com JUnit e Mockito**
- organização de projeto em camadas
- capacidade de aprender rápido e usar **IA como acelerador de desenvolvimento**

A ideia é que este projeto funcione como um **case de entrevista**. Mesmo sendo pequeno, ele deve permitir que você explique arquitetura, decisões técnicas, fluxo de dados, testes e aprendizado com Vue.

---

## Nome sugerido do projeto

**Product Catalog CMS**

ou

**Mini CMS de Produtos**

---

## Ideia central
Criar um sistema simples de cadastro e listagem de produtos, com **atributos dinâmicos por categoria**.

### Exemplo
- Categoria **Eletrônico** → atributos: marca, voltagem, garantia
- Categoria **Roupa** → atributos: tamanho, cor, material
- Categoria **Livro** → atributos: autor, editora, número de páginas

Esse formato é excelente porque permite mostrar:
- API REST
- modelagem de dados
- renderização dinâmica no front-end
- componentização no Vue
- validação
- separação de responsabilidades

Além disso, conecta muito bem com sua experiência anterior em CMS e categorização dinâmica.

---

# 1. Escopo do projeto

## 1.1 Funcionalidades obrigatórias

### Backend
- cadastrar categorias
- listar categorias
- cadastrar produtos
- listar produtos
- buscar produto por id
- filtrar produtos por categoria
- salvar atributos dinâmicos do produto

### Frontend
- tela de listagem de produtos
- tela de cadastro de produto
- seleção de categoria
- formulário dinâmico baseado na categoria
- exibição dos atributos dinâmicos cadastrados

### Testes
- pelo menos **2 testes unitários** no backend
- foco na camada de service

---

## 1.2 Funcionalidades bônus

Se der tempo, adicionar:

- edição de produto
- exclusão de produto
- paginação
- tratamento visual de erro
- loading state no front-end
- validação de formulário
- Docker
- documentação com README bem feito

---

# 2. Stack sugerida

## Backend
- Java 21
- Spring Boot
- Spring Web
- Spring Data JPA
- Validation
- H2 ou PostgreSQL
- JUnit 5
- Mockito

## Frontend
- Vue 3
- TypeScript
- Vite
- Axios
- Vue Router (opcional)

## Ferramentas
- Postman ou Insomnia
- IntelliJ
- VS Code
- Git/GitHub
- ChatGPT / outra IA para apoio no desenvolvimento

---

# 3. Arquitetura sugerida

## 3.1 Backend — estrutura de pastas

```text
src/main/java/com/seuprojeto/
  controller/
  service/
  repository/
  dto/
  model/
  exception/
  config/
```

## 3.2 Frontend — estrutura de pastas

```text
src/
  components/
  views/
  services/
  types/
  router/   (opcional)
  App.vue
  main.ts
```

---

# 4. Modelagem do backend

## 4.1 Entidades sugeridas

### Category
Campos:
- id
- name

### CategoryAttribute
Representa quais atributos existem para determinada categoria.

Campos:
- id
- name
- categoryId

Exemplo:
- categoria: Eletrônico
- atributos possíveis: marca, voltagem, garantia

### Product
Campos:
- id
- name
- description
- price
- categoryId

### ProductAttributeValue
Representa os valores preenchidos pelo usuário para cada produto.

Campos:
- id
- productId
- attributeName
- attributeValue

---

## 4.2 Forma simples de pensar

Você terá:

- uma tabela de categorias
- uma tabela de atributos permitidos por categoria
- uma tabela de produtos
- uma tabela com os valores dos atributos de cada produto

Isso é suficiente para demonstrar comportamento dinâmico sem complicar demais o projeto.

---

# 5. Endpoints sugeridos

## Categorias

### `GET /categories`
Lista categorias.

### `POST /categories`
Cria categoria.

Exemplo de body:
```json
{
  "name": "Eletrônico"
}
```

---

## Atributos por categoria

### `GET /categories/{id}/attributes`
Retorna os atributos configurados para uma categoria.

### `POST /categories/{id}/attributes`
Adiciona atributos para uma categoria.

Exemplo de body:
```json
{
  "attributes": ["marca", "voltagem", "garantia"]
}
```

---

## Produtos

### `GET /products`
Lista produtos.

### `GET /products/{id}`
Busca um produto por id.

### `GET /products?categoryId=1`
Filtra por categoria.

### `POST /products`
Cria produto com atributos dinâmicos.

Exemplo de body:
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

---

# 6. DTOs sugeridos

## CategoryRequestDTO
- name

## CategoryResponseDTO
- id
- name

## CategoryAttributeRequestDTO
- attributes: string[]

## ProductAttributeDTO
- name
- value

## ProductRequestDTO
- name
- description
- price
- categoryId
- attributes: ProductAttributeDTO[]

## ProductResponseDTO
- id
- name
- description
- price
- categoryName
- attributes: ProductAttributeDTO[]

---

# 7. Regras de negócio que você pode explicar na entrevista

Essas regras deixam o projeto com mais cara de sistema real:

- o produto deve sempre ter uma categoria válida
- atributos enviados no cadastro devem pertencer à categoria selecionada
- não permitir nome de produto vazio
- não permitir preço negativo
- categoria inexistente deve retornar erro 404

---

# 8. Tratamento de erros

Crie um tratamento global simples com `@RestControllerAdvice`.

Casos tratados:
- recurso não encontrado
- erro de validação
- regra de negócio inválida
- erro genérico

Exemplo de retorno:
```json
{
  "message": "Categoria não encontrada",
  "status": 404,
  "timestamp": "2026-04-18T10:00:00"
}
```

Isso é um diferencial porque mostra preocupação com qualidade de API.

---

# 9. Backend — passo a passo de implementação

## Etapa 1 — subir projeto Spring Boot
Gerar projeto com:
- Spring Web
- Spring Data JPA
- Validation
- H2 ou PostgreSQL
- Lombok (opcional)

## Etapa 2 — criar entidades
Criar:
- Category
- CategoryAttribute
- Product
- ProductAttributeValue

## Etapa 3 — criar repositories
Criar interfaces com `JpaRepository`.

## Etapa 4 — criar DTOs
Separar requests e responses.

## Etapa 5 — criar camada service
Implementar regras de negócio.

## Etapa 6 — criar controllers
Expor endpoints REST.

## Etapa 7 — criar tratamento global de exceções
Adicionar `@RestControllerAdvice`.

## Etapa 8 — popular banco
Adicionar dados iniciais com categorias e atributos.

Exemplo:
- Eletrônico → marca, voltagem, garantia
- Roupa → tamanho, cor, material
- Livro → autor, editora, páginas

## Etapa 9 — testar no Postman
Validar:
- criação de categoria
- listagem
- criação de produto
- filtros
- erros

## Etapa 10 — criar testes unitários
Cobrir pelo menos a camada de service.

---

# 10. Testes sugeridos com JUnit + Mockito

## Teste 1
**Deve criar produto com categoria válida**

Validar:
- categoria encontrada
- produto salvo com sucesso
- atributos salvos corretamente

## Teste 2
**Deve lançar exceção quando categoria não existir**

Validar:
- service lança exceção apropriada

## Teste 3 (bônus)
**Deve rejeitar atributo inválido para categoria**

---

# 11. Frontend em Vue — objetivo
O front-end deve provar duas coisas:

1. que você entende o fluxo básico do Vue
2. que você consegue consumir e renderizar dados dinâmicos

---

# 12. Conceitos de Vue que você vai praticar no projeto

## 12.1 Componentes
Quebrar a interface em partes reutilizáveis.

Exemplo:
- `ProductForm.vue`
- `ProductList.vue`
- `DynamicAttributesForm.vue`
- `BaseInput.vue` (opcional)

## 12.2 Props
Passagem de dados do pai para o filho.

Exemplo:
- categoria selecionada
- lista de atributos

## 12.3 Emits
Comunicação do filho para o pai.

Exemplo:
- componente de atributo avisa mudança de valor

## 12.4 Estado reativo
Praticar:
- `ref`
- `reactive`

Use:
- `ref` para valores simples
- `reactive` para objetos de formulário

## 12.5 Computed
Exemplo:
- exibir nome da categoria selecionada
- habilitar botão somente quando formulário estiver válido

## 12.6 Watch
Exemplo:
- ao mudar a categoria, buscar automaticamente os atributos da API

## 12.7 Diretivas
Usar:
- `v-model`
- `v-if`
- `v-for`

## 12.8 Lifecycle
Usar `onMounted()` para carregar categorias ou produtos iniciais.

---

# 13. Frontend — telas sugeridas

## 13.1 Tela 1 — Lista de produtos
Exibir:
- nome
- descrição
- preço
- categoria
- atributos dinâmicos

Bônus:
- filtro por categoria

## 13.2 Tela 2 — Cadastro de produto
Campos fixos:
- nome
- descrição
- preço
- categoria

Campos dinâmicos:
- renderizados de acordo com a categoria escolhida

### Exemplo
Se usuário selecionar **Eletrônico**, a interface carrega inputs para:
- marca
- voltagem
- garantia

Se trocar para **Roupa**, os campos mudam para:
- tamanho
- cor
- material

Esse é o principal diferencial do projeto.

---

# 14. Frontend — passo a passo de implementação

## Etapa 1 — criar projeto Vue com Vite
Configurar:
- Vue 3
- TypeScript
- Axios

## Etapa 2 — organizar pastas
Criar:
- `components`
- `views`
- `services`
- `types`

## Etapa 3 — criar service de API
Criar arquivo para centralizar chamadas HTTP.

Exemplo:
- buscar categorias
- buscar atributos da categoria
- listar produtos
- criar produto

## Etapa 4 — criar tela de listagem
Consumir endpoint `GET /products`.

## Etapa 5 — criar formulário de cadastro
Usar `reactive` para estado do formulário.

## Etapa 6 — implementar carregamento dinâmico de atributos
Ao mudar categoria:
- buscar `GET /categories/{id}/attributes`
- renderizar inputs dinamicamente com `v-for`

## Etapa 7 — montar payload final
No submit, enviar dados fixos + atributos preenchidos.

## Etapa 8 — tratar loading e erro
Mostrar:
- carregando...
- erro ao salvar
- sucesso no cadastro

---

# 15. Exemplo de fluxo do usuário

1. usuário entra na tela
2. categorias são carregadas
3. usuário preenche nome, descrição e preço
4. usuário seleciona categoria
5. sistema busca atributos da categoria
6. Vue renderiza inputs dinâmicos
7. usuário preenche atributos
8. sistema envia payload para API
9. backend valida dados
10. produto é salvo e listado

---

# 16. Como usar IA junto no projeto

Esse ponto é importante porque também conversa com o diferencial da vaga.

A proposta não é deixar a IA fazer tudo. A proposta é mostrar que você sabe usá-la de forma produtiva para:

- acelerar prototipação
- revisar arquitetura
- gerar testes base
- validar decisões técnicas
- melhorar documentação

---

# 17. Fluxo recomendado de uso de IA

## 17.1 Para arquitetura
Peça ajuda para:
- revisar modelagem
- validar estrutura de pacotes
- sugerir DTOs

### Prompt exemplo
```text
Estou construindo um mini CMS de produtos com Java Spring Boot. 
Quero uma modelagem simples com Category, Product, CategoryAttribute e ProductAttributeValue.
Me ajude a validar se essa estrutura faz sentido e como organizar controller, service, repository e dto.
```

## 17.2 Para backend
Peça ajuda para:
- gerar classe base de controller/service
- revisar validações
- criar testes unitários

### Prompt exemplo
```text
Estou implementando um ProductService em Spring Boot.
Quero validar se a regra de negócio está correta: o produto deve ter categoria válida e só pode receber atributos permitidos para sua categoria.
Me ajude a estruturar esse service e sugerir testes com JUnit e Mockito.
```

## 17.3 Para Vue
Peça ajuda para:
- entender Composition API
- montar componente com `ref`, `reactive`, `watch`, `computed`
- revisar consumo de API

### Prompt exemplo
```text
Estou criando um formulário dinâmico em Vue 3 com TypeScript.
Quando o usuário seleciona uma categoria, preciso buscar os atributos da API e renderizar inputs dinamicamente.
Me ajude com uma estrutura limpa usando Composition API, ref, reactive e watch.
```

## 17.4 Para README
Peça ajuda para:
- escrever documentação profissional
- resumir arquitetura
- descrever decisões técnicas

### Prompt exemplo
```text
Me ajude a escrever um README profissional para um projeto fullstack com Spring Boot e Vue.js.
Quero explicar objetivo, stack, arquitetura, como rodar localmente e principais funcionalidades.
```

---

# 18. O que você deve entender, e não apenas copiar

Se usar IA, garanta que você sabe explicar:

- por que usou DTO
- por que separou service e repository
- por que usou `watch` no Vue
- por que os atributos são dinâmicos
- como o payload é montado
- o que os testes estão cobrindo

Na entrevista, isso vale mais do que a quantidade de código.

---

# 19. Entregáveis mínimos

Até o final do fim de semana, tente chegar em:

## Obrigatório
- backend funcionando
- front consumindo API
- formulário dinâmico por categoria
- listagem de produtos
- 2 testes unitários
- README

## Bônus
- projeto no GitHub
- prints ou GIF
- validações visuais
- edição e exclusão

---

# 20. Checklist final

## Backend
- [ ] projeto Spring Boot criado
- [ ] entidades criadas
- [ ] repositories criados
- [ ] DTOs criados
- [ ] services implementados
- [ ] controllers implementados
- [ ] exceptions tratadas
- [ ] dados iniciais cadastrados
- [ ] endpoints testados
- [ ] testes unitários criados

## Frontend
- [ ] projeto Vue criado
- [ ] integração com API funcionando
- [ ] listagem de produtos pronta
- [ ] formulário pronto
- [ ] categoria carrega atributos dinâmicos
- [ ] submit funcionando
- [ ] loading e erro tratados

## Documentação
- [ ] README com stack e instruções
- [ ] descrição do objetivo
- [ ] explicação da arquitetura
- [ ] prints do projeto

---

# 21. Como vender esse projeto na entrevista

Você pode falar algo assim:

> Para me preparar melhor para a vaga, desenvolvi um projeto fullstack alinhado com a stack da empresa, usando Spring Boot no backend e Vue com TypeScript no frontend.
> 
> A proposta foi construir um mini CMS de produtos com atributos dinâmicos por categoria, o que me permitiu praticar consumo de APIs, reatividade no Vue, organização em camadas no backend e testes unitários com JUnit e Mockito.
> 
> Também usei IA como apoio para acelerar estruturação, revisar arquitetura e documentar melhor o projeto, mas sempre validando as decisões para garantir que eu entendesse o que estava implementando.

Essa resposta fica muito forte porque mostra:
- iniciativa
- alinhamento com a empresa
- estudo ativo
- uso inteligente de IA

---

# 22. Como apresentar seu ponto sobre Vue

> Eu ainda não tive a oportunidade de usar Vue profissionalmente nas empresas em que trabalhei, porque minha experiência acabou sendo mais com Angular e React. 
> 
> Mesmo assim, já tive contato com Vue em projetos acadêmicos e, para me preparar melhor para essa oportunidade, desenvolvi um projeto prático com Vue 3 e TypeScript, o que me ajudou a consolidar conceitos como componentização, reatividade, props, emits e consumo de API.

---

# 23. Como conectar isso com IA e Machine Learning na entrevista

> Além do uso de IA no meu dia a dia de desenvolvimento, também estou iniciando meu TCC com foco em visão computacional aplicada ao futebol. 
> 
> Esse contexto me aproxima ainda mais de conceitos de Machine Learning, já que a ideia envolve trabalhar com análise de imagens e possíveis modelos para detecção de eventos. 
> 
> Então vejo esse interesse em IA tanto como um diferencial técnico quanto como uma área que quero continuar aprofundando.

---

# 24. Ordem recomendada para execução no fim de semana

## Dia 1 — Backend
1. subir projeto Spring Boot
2. criar entidades
3. criar repositories
4. criar DTOs
5. criar services
6. criar controllers
7. testar endpoints

## Dia 2 — Frontend
1. subir projeto Vue
2. criar tela de listagem
3. criar formulário
4. integrar categorias
5. renderizar atributos dinâmicos
6. salvar produto

## Dia 3 — Fechamento
1. criar testes unitários
2. ajustar UX básica
3. fazer README
4. subir no GitHub
5. preparar discurso para entrevista

---

# 25. Resultado esperado

Ao final, você terá um projeto pequeno, mas com excelente valor para entrevista, porque conseguirá demonstrar:

- backend com Spring Boot
- APIs REST
- testes unitários
- frontend com Vue + TypeScript
- renderização dinâmica
- organização de código
- aprendizado rápido
- uso estratégico de IA

Esse projeto não precisa ser grande. Ele precisa ser **explicável, funcional e alinhado com a vaga**.
