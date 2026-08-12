# EmployeePojo

A small Java/Maven demo app for Employee CRUD, built on Hibernate + MySQL, with two independent web layers on top of the same server:

- **Raw servlet API** (`/api`) — Hibernate-backed CRUD (create/read/update/delete employees), plus static HTML pages under `src/main/webapp/`.
- **Spring MVC API** (`/api/{id}`) — a Spring MVC + `springdoc-openapi` layer (`EmployeeController`), also Hibernate/MySQL-backed, with interactive Swagger docs. Note: the exact path `/api` (no id) is currently claimed by the raw servlet layer above, not Spring — see [docs/flow.md](docs/flow.md) for the routing details.

Deeper project docs live in [`docs/`](docs/): [flow.md](docs/flow.md) (request flow, file map), [decision.md](docs/decision.md) (judgment-call log), [memory.md](docs/memory.md) (build/run status), and [jetty-hotreload-dispatcherservlet-issue.md](docs/jetty-hotreload-dispatcherservlet-issue.md) (a currently-open bug — see "Known issue" below).

There's also a standalone console entry point (`employee.App`) that exercises the Hibernate layer directly, without a server.

## Requirements

- Java 17+
- Maven 3.9+
- A local MySQL server with an `employee` database

## Setup

1. Create the database:
   ```sql
   CREATE DATABASE employee;
   ```
2. Copy `.env.example` to `.env` and fill in your MySQL credentials:
   ```
   DB_URL=jdbc:mysql://localhost:3306/employee
   DB_USERNAME=root
   DB_PASSWORD=your-password-here
   ```
   `.env` is read at runtime by `HibernateUtil` and must sit in the project root (the working directory the app is run from). It's gitignored — never commit it.

## Build

```
mvn clean install -DskipTests
```
(`-DskipTests` because the bundled `EmployeeTest` expects a `employee.emp` table that isn't part of this schema.)

## Run

```
mvn jetty:run
```

App is then live at `http://localhost:9000/employee/`:

- `http://localhost:9000/employee/` — static HTML pages (add/view/list employees)
- `http://localhost:9000/employee/api` — raw servlet CRUD endpoint (GET/POST/PUT/DELETE)
- `http://localhost:9000/employee/api/{id}` — Spring MVC CRUD by id (GET/PUT/DELETE)
- `http://localhost:9000/employee/swagger-ui/index.html` — interactive API docs for the Spring MVC layer (must be the full path — bare `/swagger-ui` 404s)
- `http://localhost:9000/employee/v3/api-docs` — raw OpenAPI JSON

See [docs/flow.md](docs/flow.md) for the full request-routing details.

### Known issue

Jetty's hot-reload (`mvn jetty:run`'s file watcher) currently kills Spring's `DispatcherServlet` on auto-restart — `EmployeeController` and the Swagger endpoints 404 afterward until the process is manually restarted. See [docs/jetty-hotreload-dispatcherservlet-issue.md](docs/jetty-hotreload-dispatcherservlet-issue.md).

### Console mode (no server)

```
mvn -q dependency:build-classpath -Dmdep.outputFile=cp.txt
java -cp "target/classes;$(cat cp.txt)" employee.App
```

## Author

Manish Singh
