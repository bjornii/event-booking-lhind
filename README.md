# event-booking-lhind
Lufthansa Java Internship Final Assignement - Event Booking Platform

# Event Booking Platform

A Spring Boot event booking system with role-based access (`ADMIN`, `ORGANIZER`, `ATTENDEE`), JWT + Google OAuth2 authentication, seat reservation with waitlisting, payment processing, and reviews.

## Project structure

This is a multi-module Maven project:

```
event-booking-lhind/
├── event-booking-core/   # Entities, services, repositories, business logic
├── event-booking-api/    # Controllers, security config, application entry point
├── docker-compose.yml
├── .env
└── pom.xml                # Parent/aggregator
```

## Prerequisites

- Java 21
- Maven 3.9+
- MySQL 8.0 (local install or Docker)
- Docker Desktop (optional, only if you want to run MySQL in a container)

## 1. Clone and configure environment variables

Copy `.env.example` to `.env` (or edit `.env` directly if already present) in the project root:

```env
DB_URL: jdbc:mysql://mysql:3306/event_booking_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
DB_USERNAME=root
DB_PASSWORD=root
JWT_SECRET=your-jwt-secret-code-64
JWT_EXPIRATION_MS=3600000
GOOGLE_CLIENT_ID=your-google-client-id
GOOGLE_CLIENT_SECRET=your-google-client-secret
SPRING_PROFILES_ACTIVE=dev
```

> **Important:** every line must use `KEY=VALUE` format (no colons, no quotes). The app uses [`spring-dotenv`](https://github.com/paulschwarz/spring-dotenv) to load this file automatically, both for local runs and for Docker.

| Variable | Description |
|---|---|
| `DB_URL` | JDBC connection string for MySQL |
| `DB_USERNAME` / `DB_PASSWORD` | MySQL credentials |
| `JWT_SECRET` | Base64-encoded secret used to sign JWTs |
| `JWT_EXPIRATION_MS` | Token lifetime in milliseconds |
| `GOOGLE_CLIENT_ID` / `GOOGLE_CLIENT_SECRET` | OAuth2 credentials for "Sign in with Google" |
| `SPRING_PROFILES_ACTIVE` | Which profile to boot with — `dev`, `testing`, or `prod` |

## 2. Start MySQL

**Option A — Docker (recommended):**
```bash
docker-compose up -d mysql
```
This starts MySQL on `localhost:3306` with the `event_booking_db` database created automatically.

**Option B — native MySQL install:**
Make sure a local MySQL server is running on port 3306 and create the database manually:
```sql
CREATE DATABASE event_booking_db;
```

> If port `3306` is already in use (common if you already have MySQL installed natively), either skip Docker and use your existing MySQL install, or remap the Docker container to a different host port (e.g. `3307:3306`) and update `DB_URL` accordingly.

## 3. Run the application

**From your IDE (IntelliJ):**
1. Open the project as a Maven project (root `pom.xml`)
2. Edit your Run Configuration for `EventBookingApplication`
3. Set **Active profiles** to `dev` (there's a dedicated field for this — don't put it in Environment Variables or Program Arguments)
4. Run

**From the command line:**
```bash
mvn clean install
cd event-booking-api
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

**With Docker (full stack):**
```bash
docker-compose up -d
```

On first startup with the `dev` profile, Hibernate creates all tables automatically (`ddl-auto=update`) and seeds three test accounts (see below).

The API will be available at `http://localhost:8080`.

## 4. API documentation (Swagger)

Once running, interactive API docs are available at:
```
http://localhost:8080/swagger-ui.html
```
Use the **Authorize** button with a JWT (obtained from `/api/auth/login`) to test protected endpoints.

## Configuration profiles

The app supports three Spring profiles, each with its own `application-{profile}.properties` under `event-booking-api/src/main/resources/`:

| Profile | Purpose | Database schema behavior | Logging level |
|---|---|---|---|
| `dev` | Local development | `ddl-auto=update` — auto-creates/updates tables | `DEBUG` (app + Spring Security) |
| `testing` | Integration/staging environment | `ddl-auto=validate` — schema must already exist | `INFO` |
| `prod` | Production | `ddl-auto=validate` | `WARN` (root), `INFO` (app) |

Shared configuration (DB connection, JWT, OAuth2 client registration) lives in `application.properties` and is populated from environment variables via `.env`.

### Switching profiles

- **IntelliJ:** set the **Active profiles** field in your Run Configuration
- **Command line:** `mvn spring-boot:run -Dspring-boot.run.profiles=<profile>`
- **Docker:** set `SPRING_PROFILES_ACTIVE` in `.env`, or override per-run:
  ```bash
  SPRING_PROFILES_ACTIVE=prod docker-compose up
  ```

### Dev-only seed data

When running with `dev` (or `testing`) profile, `RoleDataSeeder` automatically creates one test account per role on startup, if they don't already exist:

| Email | Password        | Role |
|---|-----------------|---|
| `admin@test.com` | `Admin123$`     | `ADMIN` |
| `organizer@test.com` | `Organizer123$` | `ORGANIZER` |
| `attendee@test.com` | `Attendee123$`  | `ATTENDEE` |

This seeder is disabled entirely in `prod` (`@Profile({"dev", "testing"})`), so no test credentials ever ship to production.

## Running tests

```bash
mvn clean install
```
Tests run against an in-memory H2 database (`test` profile, `event-booking-api/src/test/resources/application-test.properties`) and never touch your real MySQL instance. This includes both isolated unit tests (Mockito) and full Spring context integration tests exercising the booking → payment → seat-availability flow end-to-end.

## Troubleshooting

- **`Malformed entry` error on startup** — check `.env` for `KEY: value` (colon) instead of `KEY=value` (equals). Every line must use `=`.
- **Tables not created** — confirm the active profile actually shows as `dev` in the startup log (`The following 1 profile is active: "dev"`), not a malformed value like `--spring.profiles.active=dev`.
- **Port `3306` already in use** — you likely have MySQL running natively already; either use that instance directly (skip the Docker `mysql` service) or remap Docker to a different host port.
- **DBeaver shows no tables** — double-check DBeaver's connection is pointed at the same host/port/database as your `.env`, and do a full disconnect/reconnect rather than just refreshing the tree.ev