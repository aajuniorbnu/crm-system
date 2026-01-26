# CRM System - Copilot Instructions

## Project Overview
Spring Boot 3.5 CRM (Customer Relationship Management) system with PostgreSQL backend. Dual-layer API architecture: server-side templates (Thymeleaf) for UI + REST APIs for programmatic access.

## Architecture & Key Components

### Domain Model (Entity Layer)
**Core entities in `src/main/java/com/empresa/crm_system/`:**
- `Cliente` - Customer records with contact info, type (PF/PJ), status (ATIVO/INATIVO), linked to Vendas and Tickets
- `Venda` - Sales transactions with Cliente reference, items list (ItemVenda), status tracking, payment method
- `Produto` - Product catalog with stock levels and pricing
- `Ticket` - Support tickets linked to Cliente with priority levels
- `ItemVenda` - Join entity for sales line items (many-to-many through Venda)

**Pattern:** All entities use `@Entity` + `@Table` annotations, `IDENTITY` strategy for ID generation, `LocalDateTime` for timestamps, `@Enumerated(EnumType.STRING)` for status fields.

### Service Layer Architecture
`service/` contains business logic services:
- Services use `@Service` annotation with `@Autowired` repositories
- Pattern: `List<T> listarTodos()`, `Optional<T> buscarPorId(Long id)`, `T salvar(T)`, `void deletar(Long id)`
- Custom queries by status: `buscarPorStatus(StatusEnum status)` → delegates to repository custom methods

### Dual API Layer
**MVC Controllers** (`controller/`):
- `@Controller` + `@RequestMapping` classes for server-side rendering
- Return template names (e.g., `"clientes"`) → Thymeleaf templates in `templates/`
- Pass data via `model.addAttribute()`

**REST Controllers** (`controller/api/`):
- `@RestController` + `@RequestMapping("/api/...")` for JSON APIs
- CRUD pattern: `GET /{id}`, `POST`, `PUT /{id}`, `DELETE /{id}`
- Use `ResponseEntity<T>` for proper HTTP status codes

### Data Flow
Client Data Flow: `ClienteApiController` → `ClienteService` → `ClienteRepository` → PostgreSQL

## Database Configuration
- **Driver:** PostgreSQL via Supabase remote instance
- **URL:** `jdbc:postgresql://db.0ec90b57d6e95fcbda19832f.supabase.co:5432/postgres`
- **Auth:** `SUPABASE_DB_PASSWORD` environment variable (fallback: `postgres`)
- **DDL:** `spring.jpa.hibernate.ddl-auto=update` - auto-creates/updates schema
- **SQL Logging:** Enabled (`show-sql=true`, `format_sql=true`) for debugging

## Development Workflows

### Build & Run
```bash
./mvnw clean package              # Build with Maven
java -jar target/crm-system-*.jar # Run packaged JAR
# or direct: ./mvnw spring-boot:run
```

### Testing
- Unit tests in `src/test/java/com/empresa/crm_system/CrmSystemApplicationTests.java`
- Run: `./mvnw test`

### Development Mode
- `spring-boot-devtools` enabled - auto-reload on file changes
- Server: `http://localhost:8080`
- DevTools restart triggered on Java file saves

## Project-Specific Patterns

### Enum Usage
Status and choice fields use `@Enumerated(EnumType.STRING)`:
- `StatusCliente`, `StatusVenda`, `StatusTicket`, `StatusProduto`
- `FormaPagamento`, `TipoCliente`, `PrioridadeTicket`
- Always use STRING type to keep database values human-readable during queries

### Repository Convention
Custom repository methods follow naming: `findBy<FieldName>(Type value)`, e.g.:
- `findByStatus(StatusCliente status)` → custom method in repository interface

### Timestamp Handling
- Use `LocalDateTime` (JPA native type for PostgreSQL `timestamp`)
- Set `dataCadastro` on first save in Service layer: `if (cliente.getId() == null) cliente.setDataCadastro(LocalDateTime.now())`

### Cascading & Relationships
- `@OneToMany(mappedBy = "...")` - reverse side of relationships (no cascade by default)
- `@OneToMany(cascade = CascadeType.ALL)` - only used for owned entities (ItemVenda in Venda)
- `@ManyToOne @JoinColumn(name = "...")` - always on the owning side

## External Dependencies
- **Spring Boot 3.5.10-SNAPSHOT** - Latest version with Java 17+ support
- **Spring Data JPA** - ORM/repository layer
- **Spring Web** - REST controllers + MVC
- **Thymeleaf** - Server-side template engine
- **PostgreSQL Driver** - Database connectivity
- **DevTools** - Hot reload for development

## Common Tasks

### Adding a New Entity
1. Create entity class in root `crm_system/` with JPA annotations
2. Create `Repository extends JpaRepository<Entity, Long>` in `repository/`
3. Create `Service` with CRUD methods in `service/`
4. Create `@Controller` in `controller/` for MVC view + `@RestController` in `controller/api/` for REST
5. Add Thymeleaf template in `templates/`

### Adding a Custom Repository Query
In `Repository` interface, add method like:
```java
List<Entity> findByFieldName(Type value);  // JPA derives implementation
List<Entity> findByStatus(StatusEnum status);
```

### Updating Entity Relationships
Always check cascading rules - use `CascadeType.ALL` only for owned collections (ItemVenda), never for shared references (Cliente in Venda).
