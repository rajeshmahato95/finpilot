# FinPilot Backend

A modular Spring Boot backend for an India-focused personal finance assistant.

## Technology

- Java 21
- Spring Boot 3.5.x
- PostgreSQL 16
- Spring Data JPA
- Flyway
- JUnit 5, AssertJ, Mockito, Testcontainers

## Prerequisites

- JDK 21
- Maven 3.9+ (or use an IDE's Maven integration)
- Docker Desktop / Docker Engine

## Run locally

1. Start PostgreSQL:

   ```bash
   docker compose up -d postgres
   ```

2. Run the application:

   ```bash
   mvn spring-boot:run
   ```

3. Check health:

   ```bash
   curl http://localhost:8080/actuator/health
   ```

4. Run tests:

   ```bash
   mvn test
   ```

The local database password is intentionally for development only. Do not use it outside a local environment.

## Initial architecture

The first version is a modular monolith. Package boundaries represent business capabilities; avoid cross-module access to persistence internals.

```text
in.finpilot
├── FinPilotApplication
├── shared
│   └── config
└── financialaccount
    ├── api
    ├── application
    ├── domain
    └── infrastructure
```

## Development principles

- Never use PAN as an internal primary key.
- Do not connect to real financial institutions until consent, provider eligibility, and security requirements are validated.
- Keep financial calculations deterministic and separate from LLM responses.
- Use `BigDecimal` for monetary amounts.
- Every schema change must be a Flyway migration.
- Add tests for business rules and tenant/user data isolation as those modules are introduced.
