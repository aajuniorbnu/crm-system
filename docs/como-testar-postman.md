# Servidor CRM com PostgreSQL

Este projeto ja esta configurado para rodar com PostgreSQL e expor uma API REST em `http://localhost:8080`.

## 1. Subir o PostgreSQL

No terminal, na raiz do projeto:

```bash
docker compose up -d
```

O banco sera criado com estas configuracoes:

- Banco: `crm_system`
- Usuario: `postgres`
- Senha: `postgres`
- Porta: `5432`

## 2. Subir o servidor

Com o PostgreSQL rodando:

```bash
./mvnw spring-boot:run
```

Quando o servidor subir, a API ficara disponivel em:

```text
http://localhost:8080
```

## 3. Teste rapido no navegador

Voce pode validar se a aplicacao esta no ar abrindo:

```text
http://localhost:8080/actuator/health
```

Se estiver tudo certo, a resposta deve conter `"status":"UP"`.

## 4. Como testar no Postman

### Importar a colecao

1. Abra o Postman.
2. Clique em `Import`.
3. Selecione o arquivo `postman/crm-system.postman_collection.json`.

### Criar um cliente

Use a requisicao `Criar cliente` da colecao ou crie manualmente:

- Metodo: `POST`
- URL: `http://localhost:8080/api/clientes`
- Header: `Content-Type: application/json`

Body:

```json
{
  "nome": "Maria Oliveira",
  "email": "maria.oliveira@empresa.com",
  "telefone": "11999999999",
  "cpfCnpj": "12345678900",
  "endereco": "Rua das Flores, 100",
  "cidade": "Sao Paulo",
  "estado": "SP",
  "cep": "01001000",
  "tipo": "PESSOA_FISICA",
  "status": "ATIVO"
}
```

Resultado esperado:

- Status `200 OK`
- JSON com `id` preenchido

### Listar clientes

- Metodo: `GET`
- URL: `http://localhost:8080/api/clientes`

### Buscar cliente por ID

- Metodo: `GET`
- URL: `http://localhost:8080/api/clientes/1`

Troque o `1` pelo `id` retornado no cadastro.

### Atualizar cliente

- Metodo: `PUT`
- URL: `http://localhost:8080/api/clientes/1`
- Header: `Content-Type: application/json`

Body:

```json
{
  "nome": "Maria Oliveira Atualizada",
  "email": "maria.atualizada@empresa.com",
  "telefone": "11988888888",
  "cpfCnpj": "12345678900",
  "endereco": "Rua das Flores, 200",
  "cidade": "Sao Paulo",
  "estado": "SP",
  "cep": "01001000",
  "tipo": "PESSOA_FISICA",
  "status": "ATIVO"
}
```

### Deletar cliente

- Metodo: `DELETE`
- URL: `http://localhost:8080/api/clientes/1`

## 5. Outros endpoints para testar

- `GET /api/produtos`
- `POST /api/produtos`
- `GET /api/vendas`
- `POST /api/vendas`
- `GET /api/tickets`
- `POST /api/tickets`

## 6. Exemplo de produto no Postman

- Metodo: `POST`
- URL: `http://localhost:8080/api/produtos`
- Header: `Content-Type: application/json`

Body:

```json
{
  "nome": "Notebook Dell",
  "descricao": "Notebook i7 16GB RAM",
  "codigo": "NOTE-001",
  "categoria": "Informatica",
  "preco": 4599.9,
  "estoque": 15,
  "estoqueMinimo": 5,
  "unidadeMedida": "UN",
  "status": "ATIVO"
}
```

## Observacoes

- O projeto usa `spring.jpa.hibernate.ddl-auto=update`, entao as tabelas sao criadas automaticamente.
- Se a porta `5432` estiver ocupada, ajuste o `docker-compose.yml` e a variavel `SPRING_DATASOURCE_URL`.
- Se a porta `8080` estiver ocupada, altere `server.port` em `src/main/resources/application.properties`.
