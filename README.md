# CRM System

A Spring Boot CRM application for managing customers, products, sales, and support tickets. The project includes a Thymeleaf web interface for day-to-day use and REST endpoints for CRUD operations.

## Overview

This project is organized around four main business areas:

- `Clientes`: customer registration and relationship tracking
- `Produtos`: product catalog and stock monitoring
- `Vendas`: sales management with payment method and order status
- `Tickets`: customer support workflow with priority and status tracking

The dashboard also exposes summary metrics such as total customers, sales in the current month, total products, open tickets, recent sales, and low-stock products.

## Real Estate Module

The repository now also includes a real estate experience built on top of the same Spring Boot backend:

- Angular frontend in `frontend/`
- Responsive catalog for `venda` and `aluguel`
- Property categories such as apartment, house, penthouse, land, commercial, and rural
- Square-meter value calculator
- REST API for property registration and listing
- Admin panel to create, edit, and delete properties

## Tech Stack

- Java 17
- Spring Boot 3
- Spring Web
- Spring Data JPA
- Thymeleaf
- PostgreSQL 16
- Docker Compose
- Maven Wrapper
- Angular 21
- TypeScript
- SCSS

## Project Structure

```text
src/main/java/com/empresa/crm_system
├── controller        # MVC controllers for HTML pages
├── controller/api    # REST API controllers
├── enums             # Domain enums
├── repository        # Spring Data repositories
├── service           # Business logic
└── *.java            # JPA entities and main application

src/main/resources
├── static/css        # Styles
├── templates         # Thymeleaf templates
└── application.properties

frontend
├── src/app/features/home   # Responsive real estate catalog
├── src/app/features/admin  # Property administration panel
├── src/app/core            # Models and API service
└── proxy.conf.json         # Angular dev proxy for Spring API
```

## Main Features

- Dashboard with operational indicators
- Customer management
- Product management and low-stock tracking
- Sales management with final value calculation
- Ticket management with support lifecycle states
- REST API for customers, products, sales, and tickets
- Automatic schema update via Hibernate (`spring.jpa.hibernate.ddl-auto=update`)
- Real estate REST API with filters and indicators
- Angular real estate frontend with responsive layout
- Property registration screen integrated with Spring Boot
- Square-meter calculator for sales and rentals

## Domain Model

### Cliente

Represents a customer and includes fields such as:

- `nome`, `email`, `telefone`
- `cpfCnpj`
- address fields (`endereco`, `cidade`, `estado`, `cep`)
- `tipo`: `PESSOA_FISICA` or `PESSOA_JURIDICA`
- `status`: `ATIVO`, `INATIVO`, `BLOQUEADO`, `PROSPECTO`
- `dataCadastro`, `ultimaCompra`

### Produto

Represents an item in the catalog and includes:

- `nome`, `descricao`, `codigo`, `categoria`
- `preco`
- `estoque`, `estoqueMinimo`
- `unidadeMedida`
- `status`: `ATIVO`, `INATIVO`, `ESGOTADO`

### Venda

Represents a sale and includes:

- associated `cliente`
- `dataVenda`
- `valorTotal`, `desconto`, `valorFinal`
- `status`: `PENDENTE`, `APROVADA`, `CANCELADA`, `ENTREGUE`
- `formaPagamento`: `DINHEIRO`, `CARTAO_CREDITO`, `CARTAO_DEBITO`, `PIX`, `BOLETO`
- sale items through `ItemVenda`

### Ticket

Represents a support request and includes:

- associated `cliente`
- `assunto`, `descricao`
- `prioridade`: `BAIXA`, `MEDIA`, `ALTA`, `URGENTE`
- `status`: `ABERTO`, `EM_ANDAMENTO`, `AGUARDANDO_CLIENTE`, `RESOLVIDO`, `FECHADO`
- `dataAbertura`, `dataFechamento`

### Imovel

Represents a real estate listing and includes:

- `codigo`, `titulo`, `descricao`
- `categoria`: `APARTAMENTO`, `CASA`, `COBERTURA`, `TERRENO`, `COMERCIAL`, `RURAL`
- `finalidade`: `VENDA` or `ALUGUEL`
- `status`: `DISPONIVEL`, `RESERVADO`, `VENDIDO`, `ALUGADO`, `INATIVO`
- `cidade`, `bairro`, `endereco`, `estado`, `cep`
- `areaM2`, `quartos`, `banheiros`, `vagas`
- `valor`, `condominio`, `iptu`
- `destaque`, `imagemUrl`, `dataCadastro`

## Running Locally

### Prerequisites

- Java 17 installed
- Docker and Docker Compose available
- Node.js 20 or 22 recommended for the Angular frontend

### 1. Start PostgreSQL

```bash
docker compose up -d
```

This starts a PostgreSQL 16 container with:

- database: `crm_system`
- username: `postgres`
- password: `postgres`
- port: `5432`

### 2. Run the application

```bash
./mvnw spring-boot:run
```

The app starts on:

```text
http://localhost:8080
```

### 3. Run tests

```bash
./mvnw test
```

### 4. Run the Angular real estate frontend

```bash
cd frontend
npm install --ignore-scripts
npm start
```

The Angular app starts on:

```text
http://localhost:4200
```

The dev server proxies `/api` requests to Spring Boot on port `8080`.

## Configuration

The application reads these environment variables, with local defaults already defined in `application.properties`:

| Variable | Default value |
| --- | --- |
| `SPRING_DATASOURCE_URL` | `jdbc:postgresql://localhost:5432/crm_system` |
| `SPRING_DATASOURCE_USERNAME` | `postgres` |
| `SPRING_DATASOURCE_PASSWORD` | `postgres` |

Default server port:

```properties
server.port=8080
```

## Web Routes

The Thymeleaf UI is exposed through these routes:

- `/` or `/dashboard`
- `/clientes`
- `/produtos`
- `/vendas`
- `/tickets`
- `/relatorios`

## REST API

CRUD endpoints are available for all main resources:

### Clientes

- `GET /api/clientes`
- `GET /api/clientes/{id}`
- `POST /api/clientes`
- `PUT /api/clientes/{id}`
- `DELETE /api/clientes/{id}`

### Produtos

- `GET /api/produtos`
- `GET /api/produtos/{id}`
- `POST /api/produtos`
- `PUT /api/produtos/{id}`
- `DELETE /api/produtos/{id}`

### Vendas

- `GET /api/vendas`
- `GET /api/vendas/{id}`
- `POST /api/vendas`
- `PUT /api/vendas/{id}`
- `DELETE /api/vendas/{id}`

### Tickets

- `GET /api/tickets`
- `GET /api/tickets/{id}`
- `POST /api/tickets`
- `PUT /api/tickets/{id}`
- `DELETE /api/tickets/{id}`

### Imoveis

- `GET /api/imoveis`
- `GET /api/imoveis?finalidade=VENDA`
- `GET /api/imoveis?categoria=CASA`
- `GET /api/imoveis?status=DISPONIVEL`
- `GET /api/imoveis?cidade=Sao Paulo`
- `GET /api/imoveis?destaque=true`
- `GET /api/imoveis/indicadores`
- `GET /api/imoveis/{id}`
- `POST /api/imoveis`
- `PUT /api/imoveis/{id}`
- `DELETE /api/imoveis/{id}`

## Business Rules Already Implemented

- New customers receive `dataCadastro` automatically and default to `ATIVO` when status is not informed
- New products receive `dataCadastro` automatically and default to `ATIVO`
- Products default `estoqueMinimo` to `10`
- Products with stock `0` are marked as `ESGOTADO`
- New sales receive `dataVenda` automatically and default to `PENDENTE`
- Sale `valorFinal` is calculated from `valorTotal - desconto`
- Saving a sale updates the customer's `ultimaCompra`
- New tickets receive `dataAbertura` automatically and default to `ABERTO` with `MEDIA` priority
- Tickets marked as `RESOLVIDO` or `FECHADO` receive `dataFechamento`

## Seed Data

On startup, the application now preloads:

- a smartphone stock base in `produtos` with Apple and Samsung models
- a customer base in `clientes` with individual and company records

The load is idempotent:

- products are inserted only when their `codigo` does not already exist
- customers are inserted only when their `email` does not already exist
- properties are inserted only when their `codigo` does not already exist

The reference prices for the seeded smartphones were based on market prices checked on `2026-03-25`.

## Local SQL Databases

The repository also includes standalone PostgreSQL scripts for two local databases:

- `database/local/00-create-databases.sql`
- `database/local/01-estoque-smartphones.sql`
- `database/local/02-clientes.sql`

Suggested local execution with `psql`:

```bash
psql -U postgres -d postgres -f database/local/00-create-databases.sql
psql -U postgres -d postgres -f database/local/01-estoque-smartphones.sql
psql -U postgres -d postgres -f database/local/02-clientes.sql
```

The `estoque_smartphones` database stores Apple and Samsung smartphone inventory with market-reference prices, and the `clientes_crm` database stores a dedicated customer base.

## Notes

- The database schema is created and updated automatically on startup
- The frontend is server-rendered with Thymeleaf templates under `src/main/resources/templates`
- The Angular app lives in `frontend/` and is intended for development on `localhost:4200`
- The current local environment in this workspace uses `Node 25.3.0`; Angular CLI warns about that version, so prefer Node 20 or 22 when running the frontend outside this session

## Useful Commands

```bash
# start database
docker compose up -d

# stop database
docker compose down

# run application
./mvnw spring-boot:run

# run tests
./mvnw test
```
