# Jewelry Shop Billing System

A 3-tier billing application for jewelry retail: **React** frontend, **Spring Boot** backend, **MySQL** database.

## Architecture

```
React (5173)  →  Spring Boot REST API (8080)  →  MySQL (jewelry_billing)
```

- **Daily rates** are append-only (history preserved for old invoices)
- **Invoice items** lock `rate_applied`, `making_applied`, and totals at checkout
- **Billing math** uses `BigDecimal` (Java) — never raw floats for currency
- **RBAC**: `ADMIN` / `MANAGER` update rates & products; `STAFF` creates invoices

## Daily rate strategy (recommended)

Use a **hybrid approach**:

1. **Primary: manual entry by manager each morning** — most Indian jewelers follow MCX/board rates with a shop-specific markup. Manual entry keeps control and matches local pricing.
2. **Optional: API auto-fetch** — enable in `application.yml` when you connect a metal price provider:

```yaml
app:
  rates:
    auto-fetch-enabled: true
    api-url: https://your-provider.example/rates
```

The scheduled job inserts new `daily_rates` rows; managers can still override manually before billing opens.

## Prerequisites

- Java 21+
- Maven 3.9+ (or open `backend` in IntelliJ / Eclipse)
- Node.js 18+
- MySQL 8+

## Database setup

Create the database (Spring Boot can also auto-create it):

```sql
CREATE DATABASE IF NOT EXISTS jewelry_billing
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;
```

Update credentials in `backend/src/main/resources/application.yml`:

```yaml
spring:
  datasource:
    username: root
    password: your_password
```

Tables are created automatically via JPA (`ddl-auto: update`).

## Run backend

```bash
cd backend
mvn spring-boot:run
```

API base: `http://localhost:8080/api/v1`

### Seed users

| Username | Password  | Role  |
|----------|-----------|-------|
| admin    | admin123  | ADMIN |
| staff    | staff123  | STAFF |

## Run frontend

```bash
cd frontend
npm install
npm run dev
```

Open `http://localhost:5173`

## Key API endpoints

| Method | Endpoint | Access | Description |
|--------|----------|--------|-------------|
| POST | `/auth/login` | Public | JWT login |
| GET | `/rates` | Auth | Latest rate per metal/purity |
| POST | `/rates` | Admin/Manager | Insert new daily rate |
| GET | `/products` | Auth | Product catalog |
| POST | `/invoices` | Staff+ | Generate invoice with locked prices |
| GET | `/invoices/{id}` | Staff+ | Invoice detail / receipt |

## Billing formula

```
Item Price = (weight × rate) + (weight × making charge) + GST
```

Wastage (if configured on product) increases effective weight before calculation.

Past invoices **never** recalculate when today's rate changes.

## Project structure

```
jewelary/
├── backend/          Spring Boot API
├── frontend/         React + Vite UI
└── database/         Reference SQL schema
```

## Production notes

- Change `app.jwt.secret` and database passwords
- Set `spring.jpa.hibernate.ddl-auto=validate` and use migrations (Flyway/Liquibase)
- Deploy backend + MySQL in Docker or cloud for multi-branch sync
- Use HTTPS and restrict CORS origins in `SecurityConfig`
