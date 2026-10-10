FinPilot_AGENTS
FinPilot — Project Context and Engineering Guide
This file is the durable project brief for Codex and contributors. Read it before making changes.
Keep it current when an architecture decision, milestone, or implementation status changes.
The repository is public: never add real personal financial data, secrets, tokens, credentials, or production configuration.
1. Project Identity
   Name: FinPilot
   Repository: https://github.com/rajeshmahato95/finpilot
   Product: An India-focused personal finance assistant.
   Implementation approach: Year-long, open-source Java/Spring Boot project, developed incrementally with meaningful features and engineering documentation.
   Primary product ambition: Help a user understand their financial position across connected or imported financial data, ask questions in natural language, and eventually execute tightly controlled, explicitly authorized workflows.
   Initial release principle: Establish trustworthy financial data and deterministic calculations before adding generative AI or financial actions.
2. Product Problem and Target Users
   People often have financial information distributed across bank accounts, credit cards, loans, deposits, investments, insurance and other providers. FinPilot aims to consolidate available information into a useful financial picture and make it understandable through reports and a conversational assistant.
   The product must be transparent about:
   Which institutions and data types are supported.
   Where each figure came from.
   When the data was last refreshed.
   Whether a value is reported by a provider, imported by the user, or estimated.
   Which accounts, dates, and records were included in a calculation.
   Missing, stale, pending, reversed, or incomplete information.
   FinPilot must never imply that it can retrieve every account merely from a PAN number.
3. User-Facing Use Cases
   First useful release
   Import a bank statement (for example, CSV) or load clearly labelled synthetic development data.
   Normalize transactions into a consistent internal model.
   Identify likely income, expenditure, transfers, refunds, fees, and loan-related payments.
   Calculate a monthly cash-flow report for a chosen date range.
   Show data coverage, source, freshness, and limitations.
   List financial accounts for the authenticated user.
   Financial intelligence
   Income versus expenditure over the last three months.
   Cumulative EMI payments during a selected month.
   Loan outstanding principal, where supported by reliable data.
   Remaining loan tenure, distinguishing a provider-reported value from an estimate.
   Consolidated cash flow, assets, liabilities, and net worth.
   Later: investments, deposits, insurance, pension and other supported assets.
   Conversational assistant
   Users may ask questions such as:
   “Show my income versus expenses for the last three months.”
   “How much EMI did I pay last month?”
   “How much principal remains on my car loan?”
   “What is my home loan tenure?”
   “Why did my expenses increase this month?”
   The assistant should answer only from authorized, available data and deterministic tools, cite source/freshness/coverage, and ask clarifying questions when the request is ambiguous.
   Financial actions — later phase only
   A future version may support a user-requested transfer (example: transfer ₹30,000 from one bank account to another), but only after a compliant payment-provider integration, explicit user confirmation, strong authentication/authorization, risk controls, idempotency, limits, and reconciliation are established. This is explicitly out of the initial release.
4. Financial Data Access and Account Aggregator Constraints
   PAN alone is not authorization to access a person's financial accounts.
   Account Aggregator (AA) access is consent-based and depends on participating institutions, supported data types, provider arrangements, eligibility, and user authorization.
   AA data sharing is distinct from payment initiation or transfer execution.
   Provider coverage, APIs, pricing, onboarding, compliance obligations, and sandbox access must be researched and verified before promising an integration.
   Use a provider-independent connector abstraction, such as FinancialDataConnector, so the domain does not depend on one vendor.
   CSV/manual statement import and synthetic fixtures provide a useful fallback while real-provider access is being evaluated.
   Do not invent account coverage or assume all institutions expose all desired data.
5. Target Architecture
   Start with a modular monolith. Do not split into microservices without evidence that operational or scaling needs justify the cost.
   Web / Mobile Client
   |
   v
   REST API + Authentication / Authorization
   |
   v
   +----------------------------------------------------+
   |                  FinPilot Backend                  |
   |                                                    |
   | Identity & Access                                  |
   | Financial Connections & Consent                    |
   | Transaction Ingestion & Normalization              |
   | Financial Ledger                                   |
   | Loan Intelligence                                  |
   | Investment / Insurance / Net Worth (later)          |
   | Deterministic Analytics Engine                     |
   | AI Assistant (bounded tool calling)                |
   | Action Workflows (later, guarded)                   |
   | Audit, Observability & Data Lifecycle              |
   +----------------------------------------------------+
   |                    |
   v                    v
   PostgreSQL / Flyway     Provider Connectors
   |         |
   v         v
   Statement   AA / other eligible
   import      providers (after validation)
   ​
   Target technology stack
   Java 21
   Spring Boot 3.x (keep dependencies compatible and upgrade deliberately)
   Spring Web / REST
   Spring Security with an established OAuth2/OIDC identity provider where feasible
   PostgreSQL
   Spring Data JPA
   Flyway schema migrations
   Bean Validation
   Spring AI for bounded LLM/tool integration in a later milestone
   JUnit 5, AssertJ, Mockito
   Testcontainers for database integration tests
   Docker Compose for local dependencies
   GitHub Actions for build/test checks
   OpenAPI documentation (add when API surface grows)
   Observability/structured logs and metrics as appropriate
   Do not add dependencies just because they may be useful later. Add them when a concrete feature requires them.
6. Suggested Module Boundaries
   Keep modules separate by business capability and avoid leaking persistence details across modules.
   identity-access: user identity, authentication integration, authorization and ownership.
   financial-connections: connected providers, connection lifecycle, consent records and revocation.
   transaction-ingestion: statement parsing, connector ingestion, normalization, validation and deduplication.
   financial-ledger: financial accounts and normalized transaction records.
   loan-intelligence: loan accounts, repayments, reported outstanding and clearly labelled estimates.
   investment-insurance: investments, deposits, insurance and pension models (later).
   analytics-engine: deterministic cash-flow, EMI, liability, asset and net-worth calculations.
   ai-assistant: intent interpretation, approved tool selection, response grounding and evaluation (later).
   action-workflows: explicit-confirmation, idempotent and auditable actions (later).
   observability: health, metrics, structured logs, audit and operational diagnostics.
   shared: only genuinely cross-cutting primitives; do not turn it into a dumping ground.
   The current project is small and does not need all of these packages immediately. Introduce boundaries incrementally.
7. Domain Model — Target
   Likely core entities and responsibilities:
   UserProfile: internal user identity and profile metadata. Use an internal UUID; do not use PAN as a primary key.
   FinancialConnection: connection to an import/provider, its status, provider reference and sync metadata.
   ConsentRecord: consent purpose, scope, status, timestamps, expiry/revocation and provider references as applicable.
   FinancialAccount: account identity, owner, type, display label, currency, source, status and timestamps.
   FinancialTransaction: normalized transaction with account, amount, currency, direction/type, booking/value dates, status, merchant/description, external reference and provenance.
   LoanAccount: lender/product reference, linked financial account if appropriate, reported principal outstanding, rate, term, dates and data freshness.
   LoanRepayment: due/paid amounts, due/paid dates, status, source and references.
   IngestionJob: source, progress/status, counts, errors, timestamps and idempotency/correlation information.
   DataSourceRecord: source/provider/import batch, provenance, data coverage and last refresh.
   AuditEvent: security-sensitive actions and changes, with minimal necessary metadata.
   Later: investment holdings, deposits, insurance policies, pension assets and action-workflow records.
   Domain modelling should be based on requirements, not a rush to create every entity at once. Avoid exposing JPA entities directly from controllers; use request/response DTOs.
8. Financial Correctness Rules
   These rules are non-negotiable:
   Use BigDecimal for monetary amounts. Never use double/float for financial calculations.
   Store explicit ISO currency codes and define rounding rules at domain boundaries.
   Define date ranges and timezone semantics explicitly; do not silently mix local dates and UTC timestamps.
   Exclude transfers between a user's own accounts from income and expenses when correctly identified.
   Treat credit-card purchases and credit-card bill payments carefully to avoid double-counting expenses.
   Loan disbursements are not salary/income.
   Refunds, reversals, chargebacks and pending transactions must be distinguishable.
   Distinguish a loan's outstanding principal from the sum of remaining instalments, which may include future interest and charges.
   Do not state an estimated loan tenure or balance as a provider-reported fact.
   Identify missing or stale source data and communicate uncertainty.
   Deduplicate imported/provider transactions using a robust, source-aware idempotency strategy; do not rely on descriptions alone.
   Every aggregate must define which transaction statuses and transaction categories it includes.
9. GenAI and Agentic Design
   AI is not the source of truth for financial arithmetic.
   Target interaction model
   User question
   |
   v
   LLM interprets intent / selects an approved tool
   |
   v
   Authorization and user-scoped tool boundary
   |
   v
   Deterministic Java domain/analytics service
   |
   v
   Structured result + source + freshness + coverage
   |
   v
   LLM explains the result without changing its meaning
   ​
   Initial candidate tools:
   getCashFlowSummary
   getEmiPayments
   getLoanOutstanding
   getLoanTenure
   getDataCoverage
   Rules:
   Use Spring AI tool calling only when the core data and deterministic services are ready.
   Do not let an LLM generate and execute arbitrary SQL.
   Tool implementations must derive user identity from the authenticated security context, not from untrusted user-supplied IDs.
   Prefer read-only, narrowly scoped tools at first.
   Validate tool inputs and outputs.
   Never fabricate missing numbers or pretend a tool succeeded.
   Return evidence/metadata sufficient to explain the answer.
   Create evaluation cases for correctness, ambiguity, stale data, missing data, and prompt-injection attempts.
   Agentic workflows must be bounded, observable, reversible where possible, and require human confirmation for consequential actions.
10. Security, Privacy and Compliance-by-Design
    Authenticate users before exposing private financial resources.
    Enforce resource ownership at the service/data-access boundary, not just in the UI.
    Never trust caller-provided userId for authorization.
    Use established identity providers/protocols where feasible; avoid inventing a password/token security system.
    Keep local development credentials out of committed production configuration. Use environment variables or a proper secrets manager in deployed environments.
    Do not log account numbers, tokens, statements, sensitive identifiers or full transaction payloads unnecessarily.
    Minimize sensitive data retention; plan deletion/export and consent revocation.
    Encrypt data in transit and at rest in deployed environments and protect backups.
    Maintain an audit trail for consent, data access, imports and consequential actions without logging unnecessary financial details.
    Validate file uploads and protect statement parsing against malformed/oversized inputs.
    Apply rate limits, request validation and safe error handling as the API matures.
    Never use real financial data in tests, examples, screenshots or public fixtures.
    The repository is public; do not commit secrets or personal information.
    Any live payment/transfer feature requires separate partner, authorization, compliance and security validation. It is not part of the first release.
11. API Design Principles
    Use versioned endpoints, initially /api/v1/....
    Use DTOs for requests/responses and Bean Validation for input constraints.
    Keep controllers thin; business rules belong in application/domain services.
    Return consistent error responses with safe messages and correlation identifiers where appropriate.
    Scope reads and writes to the authenticated principal.
    Add pagination for potentially large transaction/account collections.
    Design ingestion endpoints to be idempotent and report partial failures clearly.
    Do not expose temporary /_dev endpoints in production; remove them or isolate them behind a development-only profile before real user data is handled.
12. Testing and Quality Bar
    Unit-test domain rules and financial calculations.
    Test validation, boundary dates, refunds, reversals, internal transfers, duplicates and missing/stale data.
    Test user isolation: one user must never read or modify another user's records.
    Use integration tests for persistence, migrations and repository behavior; use Testcontainers when appropriate.
    Test authentication/authorization failures as well as success paths.
    Do not weaken Hibernate schema validation to hide migration/model mismatches.
    Every schema change must be a new Flyway migration once a migration has been applied/shared. Never casually edit an applied migration in a shared environment.
    Run mvn test after each meaningful change and report the real output.
    Review git diff for correctness, accidental secrets, unrelated files and generated artifacts before committing.
    Keep the project buildable after each milestone.
13. GitHub and Open-Source Practices
    Keep commits small, meaningful and focused.
    Use clear commit messages, e.g. feat: add user-scoped financial account APIs.
    Add architecture decision records (ADRs) for important choices.
    Track work in issues and milestones.
    Use GitHub Actions to run tests on pushes and pull requests.
    Add synthetic fixtures and reproducible setup instructions.
    Document limitations honestly, especially provider coverage and financial estimates.
    Never claim an external integration is supported until it is implemented and verified.
    Before pushing, inspect git status, git diff and staged changes.
    Do not force-push or overwrite remote history without first understanding the divergence.
14. Roadmap and Milestones
    The schedule is indicative, not a promise; adjust based on feasibility and learning.
    Stage 0 — Requirements and feasibility (Weeks 1–2)
    Define requirements and data coverage matrix.
    Research AA eligibility, supported institutions/data types, provider terms, pricing and sandbox availability.
    Validate identity/authentication options.
    Document payment rails/partner constraints for the later phase.
    Decide first CSV statement format and transaction-normalization rules.
    Stage 1 — Secure foundation and importable data (Weeks 3–6)
    Spring Boot, PostgreSQL, Flyway, CI and baseline docs.
    Authentication/authorization and user ownership.
    Financial account management.
    CSV/manual statement import.
    Normalized transactions, deduplication, provenance and ingestion status.
    Synthetic sample data and robust validation.
    Stage 2 — Financial data connectors (Weeks 7–12)
    Provider-independent FinancialDataConnector interface.
    Mock/synthetic connector and statement import fallback.
    Real AA/provider sandbox only if access and eligibility are confirmed.
    Consent lifecycle, sync status, retries, error handling, data freshness and revocation.
    Stage 3 — Deterministic financial intelligence (Months 4–5)
    Three-month income/expense summaries.
    EMI totals by period.
    Loan outstanding and tenure with reported-versus-estimated distinction.
    Account transfers, refunds, reversals and card settlement rules.
    Testable calculations with explicit data coverage.
    Stage 4 — GenAI finance assistant (Months 6–7)
    Spring AI integration.
    Bounded, user-scoped, read-only tool calling.
    Grounded responses, provenance/freshness, ambiguity handling and evaluation tests.
    No arbitrary SQL execution and no autonomous money movement.
    Stage 5 — Broader financial picture (Months 8–10)
    Investments, deposits, insurance, pension and net-worth view, based on verified data availability.
    Asset/liability valuation dates, source coverage and stale-data handling.
    Stage 6 — Guarded action workflows (Months 11–12+)
    Mock transfer/action workflow first.
    Research partner and compliance requirements.
    Add explicit confirmation, strong authentication, limits, idempotency keys, audit, status reconciliation and safe failure handling before any live financial action.
    Do not implement live transfer execution unless provider and compliance feasibility is established.
15. Release Targets
    0.1 — Financial data foundation: secure user boundary, account model, statement import, normalized transactions, source/freshness and migration discipline.
    0.2 — Deterministic financial intelligence: tested cash-flow, EMI and loan calculations.
    0.3 — AI finance assistant: grounded question answering through approved deterministic tools.
    Later releases: broader assets, provider integrations and explicitly authorized action workflows.
16. Current State (as of 2026-10-11)
    Verified in the user's local development session
    The project directory is finpilot-backend on macOS.
    Java 21 is installed (21.0.12.1 was shown in terminal output).
    Docker Desktop exists at /Applications/Docker.app.
    docker compose up -d postgres was reported as completed.
    Maven was initially not found; it was then installed/configured sufficiently for mvn test to run.
    The initial mvn test found a schema mismatch: migration declared currency CHAR(3) but Hibernate expected VARCHAR(3).
    The local migration was changed to VARCHAR(3) and the local database was reset during troubleshooting.
    The user reported that the Spring Boot application is now UP.
    The user reports that code has been pushed to the public GitHub repository: https://github.com/rajeshmahato95/finpilot.
    The existing Git remote initially pointed to https://github.com; it was corrected during the workflow, but verify with git remote -v if uncertain.
    Starter implementation known to exist
    The starter project was generated with:
    pom.xml for Java 21 / Spring Boot 3.5.x, Web, Validation, Actuator, Data JPA, Flyway, PostgreSQL, Spring Boot Test, Testcontainers dependencies.
    FinPilotApplication.
    FinancialAccount entity with UUID ID and userId, display name, account type, currency, data source and created timestamp.
    AccountType enum.
    FinancialAccountRepository with findAllByUserId.
    FinancialAccountController with temporary development endpoints under /_dev.
    application.yaml with PostgreSQL, Flyway, ddl-auto: validate, health endpoint settings.
    V1__create_financial_account.sql migration.
    Basic context-load and entity tests.
    compose.yaml for local PostgreSQL.
    .gitignore and a basic README.
    Known gaps — do not assume these are done
    Authentication and Spring Security have not yet been implemented.
    User identity is not yet backed by a proper UserProfile model.
    /_dev endpoints are not safe for production because they accept a caller-supplied user ID. Do not use them with real data or expose them publicly; replace/remove them or confine them to a development profile before real data handling.
    The current entity/controller expose persistence model details directly; add DTOs and a service layer.
    Statement ingestion, transaction entity, deduplication and analytics are not yet implemented.
    No real AA/bank connector has been implemented or verified.
    No AI assistant or payment/transfer feature has been implemented.
    GitHub Actions/CI, ADRs, broader API docs and production security hardening may still be outstanding.
    The app being UP is not proof that all tests pass; run mvn clean test and report actual results before claiming green CI.
    Do not assume the remote branch, remote URL or pushed commit state without checking Git.
17. Immediate Next Actions
    Work in this order:
    Inspect repository, git status, git remote -v, current branch, source files and existing migrations.
    Run mvn clean test and record the actual outcome.
    Review the temporary development API and decide how to isolate/remove it.
    Add a small architecture decision record for identity/authentication approach.
    Implement secure user identity and authorization. Prefer a managed OIDC provider if feasible; do not implement custom password/token cryptography casually.
    Introduce a service layer and request/response DTOs for financial accounts.
    Add user-scoped tests proving cross-user access is impossible.
    Add GitHub Actions to build/test on pushes and pull requests.
    Only then build transaction ingestion.
18. Instructions for Codex on Every Task
    Before editing:
    Read this file and inspect the relevant source/tests.
    Summarize current behavior and the specific change needed.
    Identify security/data-integrity risks and any migration implications.
    Propose a small plan; ask before broad or destructive changes.
    During implementation:
    Prefer small, understandable changes.
    Follow existing conventions unless they are unsafe or inconsistent.
    Do not add speculative frameworks.
    Do not edit an applied/shared Flyway migration; add a new migration instead.
    Do not run destructive database commands or reset volumes without explaining data loss and receiving explicit approval.
    Do not use real user financial data.
    Do not trust unverified provider capabilities or external assumptions.
    After implementation:
    Run relevant tests and mvn clean test where practical.
    Report exactly which tests ran and their result.
    Inspect git diff and summarize changed files.
    List remaining risks, limitations and manual steps.
    Never commit or push unless the user explicitly requests it.