# Task Manager API — In-Depth Project Notes

**Author:** P. Samuel Joe  
**Purpose:** Beginner-friendly learning notes and Java fresher interview preparation  
**Repository:** https://github.com/samueljoep-byte/task-manager-api  
**Live API documentation:** https://task-manager-api-4b95.onrender.com/swagger-ui/index.html

> These notes are based on the repository's current source files and configuration at the time of writing. Where a code example is simplified for teaching, it is labelled as an illustration. Always check the current source if the code changes.

## 1. Understand the project before memorising it

This is a Java backend REST API for managing tasks. A client sends HTTP requests; Spring MVC routes each request to a controller; service classes apply application logic; repositories access PostgreSQL through Spring Data JPA/Hibernate; and the API returns an HTTP status and JSON.

The current repository README describes:
- User registration and login
- JWT authentication and role-based access
- Task CRUD and status management
- A user-to-task relationship
- Flyway database migrations
- JUnit/Mockito service tests
- Docker packaging
- OpenAPI/Swagger documentation

### What problem does it solve?

Instead of keeping tasks only in a frontend page or a local list, the backend stores them in a database and exposes endpoints so a client can retrieve and modify them. Authentication identifies the user, and the service queries task records belonging to that authenticated user.

### A simple mental model

Imagine a receptionist (controller) taking a request, a coordinator (service) deciding what should happen, a filing clerk (repository) retrieving or saving records, and a filing cabinet (database) storing them. Each part has a distinct responsibility.

## 2. Current stack and why each item is used

| Technology | Role in this repository |
|---|---|
| Java 21 | Language and runtime target declared in Maven |
| Spring Boot 4.1.1 | Application bootstrapping and auto-configuration |
| Spring MVC | HTTP endpoints and request/response handling |
| Spring Security | Request authentication and authorization |
| JWT (JJWT 0.12.6) | Creating and validating bearer tokens |
| Spring Data JPA / Hibernate | Entity persistence and repository abstraction |
| PostgreSQL | Current datasource driver/configuration |
| Flyway | Versioned database schema migrations |
| Bean Validation | Validating incoming task fields |
| Springdoc OpenAPI | Interactive Swagger API documentation |
| JUnit 5 / Mockito | Automated tests |
| Maven | Dependency management and build |
| Docker | Repeatable packaging and runtime |
| Git/GitHub | Source control and collaboration |

**Database note:** older development conversations involved Oracle. The current checked-in `pom.xml` and `application.properties` use PostgreSQL. When discussing the current repository, say PostgreSQL unless you are describing a previous local version.

## 3. Source-code map: where to find each responsibility

The current Java package is `com.example.taskmanager`.

| Location | Responsibility |
|---|---|
| `TaskmanagerApplication.java` | Spring Boot application entry point |
| `controller/TaskController.java` | Task HTTP endpoints |
| `controller/AuthController.java` | Registration and login endpoints |
| `service/TaskService.java` | Task business/application operations and current-user scoping |
| `service/AuthService.java` | Registration workflow |
| `service/CustomUserDetailsService.java` | Loads user information for Spring Security |
| `repository/TaskRepository.java` | Task database queries |
| `repository/UserRepository.java` | User database queries |
| `entity/Task.java` | JPA task entity |
| `entity/User.java` | JPA user entity |
| `dto/TaskRequest.java` | Incoming task payload model |
| `dto/TaskResponse.java` | Outgoing task payload model |
| `dto/RegisterRequest.java`, `LoginRequest.java` | Auth request payloads |
| `dto/AuthResponse.java` | Registration response |
| `mapper/TaskMapper.java` | Converts between task DTOs and entity |
| `exception/TaskNotFoundException.java` | Represents a missing task |
| `exception/GlobalExceptionHandler.java` | Centralised API exception responses |
| `security/JwtService.java` | JWT creation/validation logic |
| `security/JwtAuthenticationFilter.java` | Reads bearer tokens for incoming requests |
| `config/SecurityConfig.java` | Security filter chain and public/protected routes |
| `config/PasswordConfig.java` | Password encoder configuration |
| `config/OpenApiConfig.java` | OpenAPI/Swagger configuration |
| `src/main/resources/application.properties` | Environment-based application settings |
| `src/main/resources/db/migration/` | Flyway SQL migration scripts |

**How to use this map:** open the class named in the table in Eclipse and read from the top down. Start with the controller, follow the method call into the service, then inspect the repository method. This is the fastest way to explain a real request without trying to memorise the entire project.

## 4. Architecture and request flow

### The layers

1. **Client:** Postman, Swagger UI, or a frontend sends HTTP.
2. **Security filter chain:** checks public/protected access; the JWT filter attempts to authenticate a bearer token.
3. **Controller:** maps the HTTP method and path to a Java method, binds JSON/path variables, and chooses a response.
4. **Service:** applies task/user rules and orchestrates the operation.
5. **Mapper:** converts between request/response DTOs and the JPA entity.
6. **Repository:** performs persistence queries.
7. **PostgreSQL:** stores user and task rows.
8. **Response:** Java result becomes an HTTP response, normally JSON.

### Example: create a task

For a successful `POST /tasks` request:

1. The client sends a JSON body and a bearer token.
2. The security filter chain requires authentication for the protected route.
3. `TaskController.createTask` receives the request body as a `TaskRequest`; `@Valid` requests Bean Validation.
4. The controller calls `TaskService.createTask`.
5. The service reads the authenticated username from `SecurityContextHolder`.
6. It looks up that user using `UserRepository.findByUsername`.
7. `TaskMapper` creates a task entity from the request.
8. The service attaches the current user to the task.
9. `TaskRepository.save` persists the entity.
10. The mapper creates a `TaskResponse`.
11. The controller returns HTTP `201 Created` with the response body.

The crucial ownership step is attaching the authenticated user in the service. The server should not blindly trust a client-supplied user ID to decide who owns a new task.

### Example: read a task by ID

For `GET /tasks/12`:

1. Authentication establishes the current principal.
2. Controller receives `12` as a `@PathVariable Long id`.
3. Service finds the authenticated user's database record.
4. Service calls `findByIdAndUser(id, user)`, not simply `findById(id)`.
5. If the task belongs to that user, the service returns it.
6. If not found for that user, it throws `TaskNotFoundException`.
7. The controller maps the task into `TaskResponse` and returns it.

This is an important security design: using both task ID and owner prevents a user from reading another user's task just by guessing its ID.

## 5. Endpoints in the current controller

The current `TaskController` has a class-level mapping of `/tasks`.

| Method | Path | Purpose | Success response in current code |
|---|---|---|---|
| POST | `/tasks` | Create a task | `201 Created` and `TaskResponse` |
| GET | `/tasks` | List the authenticated user's tasks | `200 OK` with a list |
| GET | `/tasks/{id}` | Get one task belonging to the current user | `200 OK` |
| GET | `/tasks/status/{status}` | List current user's tasks by status | `200 OK` with a list |
| PUT | `/tasks/{id}` | Update a task belonging to the current user | `200 OK` |
| DELETE | `/tasks/{id}` | Delete a task belonging to the current user | `200 OK` with a text message |

Authentication routes are mapped under `/api/auth`:

| Method | Path | Purpose |
|---|---|---|
| POST | `/api/auth/register` | Register a user |
| POST | `/api/auth/login` | Verify credentials and return a JWT string |

The current security configuration permits registration, login, Swagger UI, and OpenAPI docs without authentication. Other requests require authentication.

### Example task request

The current controller accepts a `TaskRequest` body. A typical payload is:

```json
{
  "title": "Study Spring Security",
  "description": "Understand JWT filter flow",
  "status": "PENDING"
}
```

Confirm the exact required fields against `dto/TaskRequest.java`. In the entity, title and description are non-blank, and status is represented by the `TaskStatus` enum.

### HTTP methods and status codes

- **GET** reads a resource and should not modify it.
- **POST** creates a resource or submits an operation.
- **PUT** updates/replaces the resource identified by the URL; PUT is intended to be idempotent.
- **DELETE** removes a resource.
- **200 OK** means the request succeeded.
- **201 Created** means a resource was created.
- **400 Bad Request** commonly means the submitted data is invalid.
- **401 Unauthorized** means the request lacks valid authentication.
- **403 Forbidden** means the authenticated principal lacks permission.
- **404 Not Found** means the resource was not found.
- **500 Internal Server Error** indicates an unexpected server-side failure.

Do not claim the API returns a status code solely because it is common REST practice; confirm the controller and exception handler.

## 6. DTOs, entities and mapping

### What is a DTO?

A Data Transfer Object (DTO) defines the shape of data moving across the API boundary. It is not necessarily the same thing as a database entity.

- `TaskRequest`: data the client sends.
- `TaskResponse`: data the server returns.
- `Task`: JPA entity stored by the persistence layer.

### Why separate DTOs and entities?

1. **Security:** response payloads can omit internal fields such as password hashes or relationships.
2. **API stability:** database changes do not have to change the public JSON shape.
3. **Validation:** request constraints can be specific to user input.
4. **Clarity:** request and response data are explicit.

### Mapper responsibility

The `TaskMapper` converts between the DTO and entity. The service asks the mapper to convert, then applies business-specific data such as the authenticated owner before saving. The mapper should not decide which user is allowed to own a task; that is application/security logic.

## 7. Task entity walkthrough

The current `Task.java` contains these important fields:

- `id`: primary key, generated by the database identity strategy.
- `title`: required non-blank task title.
- `description`: required non-blank description.
- `status`: `TaskStatus`, stored as a string through `@Enumerated(EnumType.STRING)`.
- `createdAt`: set when the entity is first persisted.
- `updatedAt`: set when the entity is created and refreshed before an update.
- `user`: many tasks can belong to one user; mapped to `user_id`, which is non-nullable.

### JPA lifecycle callbacks

`@PrePersist` calls `onCreate()` before a new entity is persisted. In this entity, it assigns current time to both timestamps.

`@PreUpdate` calls `onUpdate()` before an existing entity update and refreshes `updatedAt`.

These callbacks centralise timestamp handling so every code path does not have to set those values manually. The timestamps use `LocalDateTime`, which does not itself carry a timezone offset. For systems spanning time zones, teams often consider storing instants in UTC and being explicit about timezone handling.

### Why use `EnumType.STRING`?

If enum values are stored as ordinals (0, 1, 2), reordering enum constants can change their meaning. Storing strings such as `PENDING` is easier to read and avoids ordinal-reordering risk. Renaming a persisted enum constant still requires a planned data migration.

## 8. Task status enum

The repository defines a `TaskStatus` enum. The project history uses statuses such as:

- `PENDING`: work has not started.
- `IN_PROGRESS`: work is underway.
- `COMPLETED`: work is finished.

The endpoint `GET /tasks/status/{status}` binds the path segment to the enum. Use the enum's exact spelling in requests; for example, `/tasks/status/PENDING`. Invalid enum text generally fails request conversion and should be handled as a client error.

## 9. Database schema and indexes

The current Flyway migrations are:

### V1 — `V1__create_users_table.sql`

Creates `users` with:
- `id`: identity primary key
- `username`: required and unique
- `password`: required stored credential field (the application should store a password hash, never the plain password)
- `role`: required role value

### V2 — `V2__create_task_table.sql`

Creates `task` with:
- `id`: identity primary key
- `title`, `description`, `status`
- `created_at`, `updated_at`
- `user_id`: required foreign key referencing `users(id)`

The foreign key ensures the referenced user exists. The non-null rule means a task cannot be persisted without an owner.

### V3 — `V3__create_task_indexes.sql`

Creates indexes on:
- `task(user_id)`
- `task(status, user_id)`

An index is an additional data structure that can speed up lookups/filtering at the cost of disk space and extra work during inserts/updates. The user index supports owner-based task retrieval. The composite index can support queries filtering by status and user; index usefulness depends on actual query patterns and database query plans.

### Flyway concepts

Flyway uses versioned files named in a pattern such as `V4__add_priority_column.sql`. It stores migration history so it can determine which scripts have already run.

Recommended change workflow:
1. Add a new migration; do not rewrite an already-applied shared migration.
2. Apply it to a development database.
3. Update the entity mapping if needed.
4. Keep Hibernate schema validation enabled.
5. Run tests and verify the affected endpoint.
6. Deploy using a controlled migration process.

The current application properties include:
- `spring.flyway.enabled=true`
- `spring.flyway.baseline-on-migrate=true`
- `spring.flyway.baseline-version=0`
- `spring.jpa.hibernate.ddl-auto=validate`
- `spring.jpa.open-in-view=false`

`ddl-auto=validate` checks entity/schema compatibility; it does not create tables for you. Flyway is the schema-change mechanism. `open-in-view=false` disables the Open EntityManager in View pattern, helping make persistence access boundaries more explicit.

## 10. Authentication and JWT — step by step

### Authentication and authorization are different

- **Authentication:** proves who the caller is.
- **Authorization:** decides whether that caller is permitted to perform an operation.

### Registration

The controller receives a `RegisterRequest` and calls `AuthService.register`. A secure registration workflow should validate input, encode the password using a password encoder, assign an allowed role, and persist the user. Inspect `AuthService.java` and `PasswordConfig.java` to explain the precise implementation.

### Login

The current `AuthController.login`:
1. Reads username and password from `LoginRequest`.
2. Creates a `UsernamePasswordAuthenticationToken` containing those credentials.
3. Calls Spring Security's `AuthenticationManager.authenticate(...)`.
4. If authentication succeeds, gets the authenticated principal.
5. Calls `JwtService.generateToken(...) `.
6. Returns the token string.

If credentials are wrong, authentication should fail and the client should not receive a valid token.

### Authenticated API request

A client sends:

```http
Authorization: Bearer <your-token>
```

The `JwtAuthenticationFilter` checks the request token. If it is valid, the security context can hold the authenticated principal so service code can call:

```java
SecurityContextHolder.getContext()
    .getAuthentication()
    .getName();
```

This is how `TaskService` obtains the current username before retrieving that user's tasks.

### Stateless sessions

The current `SecurityConfig` uses `SessionCreationPolicy.STATELESS`. The server does not rely on an HTTP session to remember authentication between requests; the client sends the bearer token with each protected request.

### Public and protected routes

The configuration permits:
- `/api/auth/register`
- `/api/auth/login`
- `/swagger-ui/**`
- `/v3/api-docs/**`

It requires authentication for other requests. `@EnableMethodSecurity` enables method-level annotations such as `@PreAuthorize`.

**Code-review observation:** comments in `TaskController` say some methods are “ADMIN only,” but the actual annotations currently use `hasAnyRole('USER','ADMIN')` on task methods. In an interview, describe what the annotations actually do, not the comments. If the intended rule is ADMIN-only for a particular action, update the annotation, test it, and update the notes. Also, role-based access and per-user task ownership are separate checks: a role can permit entering an endpoint, while the service's user-scoped repository query limits which task records can be accessed.

### JWT safety

- Use a strong random signing secret stored outside source control.
- Never log tokens or commit secrets.
- A signed JWT is generally integrity-protected but not encrypted by default.
- Expiration should be checked; token revocation needs a separate strategy if required.
- Store only necessary claims in a token.
- Use HTTPS in deployed environments.

Current property names are `JWT_SECRET` and `JWT_EXPIRATION_MS`, mapped into `app.jwt.secret` and `app.jwt.expiration-ms`. The default expiry is `86400000` milliseconds (24 hours) if the environment variable is not supplied.

## 11. Validation and exception handling

The current `Task` entity uses `@NotBlank` for title and description. The task controller uses `@Valid @RequestBody TaskRequest`, which asks Spring's validation integration to validate the request model before proceeding.

A typical validation cycle:
1. JSON is deserialized into the request DTO.
2. Bean Validation checks constraints.
3. If a constraint fails, the controller method is not executed normally.
4. The exception handler converts the failure into a client-facing response.

The project has a `GlobalExceptionHandler`, `ValidationErrorResponse`, `ErrorResponse`, `InvalidStatusResponse`, and `TaskNotFoundException`. Read the handler to learn the exact error payload and status mapping.

A good API error should contain a useful message and appropriate HTTP status without leaking SQL details, stack traces, passwords, signing secrets or internal infrastructure.

## 12. Testing with JUnit and Mockito

A **unit test** checks a small unit, such as service logic, in isolation. Mockito can replace the repository or user repository with a mock, allowing the test to simulate data and verify behavior without connecting to PostgreSQL.

A service test generally follows **Arrange – Act – Assert**:
1. **Arrange:** set up mock behavior and test inputs.
2. **Act:** call the service method.
3. **Assert:** check the result or exception.
4. Optionally verify a dependency interaction when it is part of the behavior being tested.

High-value cases for this project:
- Valid task creation attaches the authenticated user and saves the task.
- Listing returns only tasks owned by the authenticated user.
- Getting an owned task succeeds.
- A task not owned by the current user is not returned.
- Status filtering uses both status and user.
- Updating an owned task changes allowed fields.
- Updating/deleting a missing or non-owned task raises the not-found path.
- Invalid title/description is rejected.
- Invalid login does not produce a token.
- Protected routes reject requests without valid authentication.
- Role rules match the intended policy.

Run tests on Windows from the project root:

```powershell
.\mvnw.cmd test
```

If Maven Wrapper is not present or not usable:

```powershell
mvn test
```

Do not say a test passes merely because it was written. Run it and report the actual test result.

## 13. Maven, build and dependency management

The `pom.xml` is Maven's project descriptor. It declares:
- Coordinates: group ID, artifact ID and version
- Java version
- Dependencies for web, JPA, security, validation, Flyway, PostgreSQL, OpenAPI and testing
- Build plugins such as the Spring Boot Maven plugin

Useful commands:

```powershell
# Compile and run tests
.\mvnw.cmd test

# Package the application
.\mvnw.cmd clean package

# Run the packaged jar (after a successful package)
java -jar target\taskmanager-0.0.1-SNAPSHOT.jar
```

The current Dockerfile builds with Maven and a Java 21 build image, then copies the JAR into a Java 21 JRE runtime image. This is a multi-stage build: the final runtime image need not contain the full build toolchain.

## 14. Configuration and deployment

The current `application.properties` reads settings from environment variables:

| Environment variable | Used for |
|---|---|
| `DB_URL` | PostgreSQL JDBC URL |
| `DB_USERNAME` | Database username |
| `DB_PASSWORD` | Database password |
| `JWT_SECRET` | JWT signing secret |
| `JWT_EXPIRATION_MS` | Token lifetime in milliseconds |
| `PORT` | Server port; defaults to 8083 |

Do not commit real values. Keep local credentials in local environment configuration and production secrets in the hosting provider's secret/environment settings.

The Docker Compose file maps port `8083:8083` and forwards the database/JWT environment variables. Docker containers must be able to reach the configured database host; a localhost address inside a container usually refers to the container itself, not the developer's host machine.

### Live API docs

The repository README currently links Swagger UI at:

https://task-manager-api-4b95.onrender.com/swagger-ui/index.html

Use Swagger UI to inspect the actual API schema and send requests. For protected endpoints, first register/login, copy the returned token, and authorize Swagger with the bearer token if the UI's security scheme is configured.

### Deployment checklist

- Build and test before deployment.
- Configure environment variables and secrets in the host.
- Ensure the database is reachable from the deployed service.
- Check Flyway migration logs and schema compatibility.
- Confirm the application binds to the hosting platform's expected port.
- Inspect startup logs if the service fails to start.
- Test a public endpoint, login, and an authenticated task endpoint.
- Never expose production credentials in screenshots or demonstrations.

## 15. Common failures and a debugging order

### App fails during startup

Check the first meaningful exception in the logs, not just the final “application failed” line. Determine whether the failure begins in datasource creation, Flyway, JPA entity manager, repository creation, security dependency injection, or controller startup.

### Database connection failure

Check the JDBC URL, driver, database status, credentials, network/firewall and whether the configured database exists. The current checked-in configuration is PostgreSQL-based.

### Flyway fails

Read the exact migration name and SQL error. Verify syntax, schema permissions, migration history, and whether a script was edited after being applied. Do not delete migration history as a first fix.

### Entity manager or repository bean fails

Inspect the earliest root cause below the bean-creation exception. Check datasource connectivity, Flyway completion, Hibernate validation errors, entity mapping, repository query property names, and dependency/version compatibility. A later bean failure can be a consequence of an earlier persistence failure.

### 401 versus 403

- **401:** token absent, invalid, expired, or authentication not established.
- **403:** authentication exists but the principal is not allowed by authorization policy.

### Endpoint returns 404

Verify method and path, class-level mapping, method-level mapping, application base path, and whether the resource exists for the currently authenticated user.

## 16. Interview answers — explain, do not memorise blindly

### “Explain the architecture.”

“My application follows a layered Spring Boot structure. The controller handles HTTP requests, the service coordinates business logic, the mapper converts DTOs and entities, and the repository accesses PostgreSQL through Spring Data JPA. Spring Security checks protected requests, and the service scopes task queries to the authenticated user.”

### “How does your JWT authentication work?”

“The login endpoint passes the submitted credentials to Spring Security's AuthenticationManager. After successful authentication, the application creates a JWT. For protected requests, the client sends it as a Bearer token. A JWT filter validates it and establishes the authenticated principal, which the application uses to identify the current user.”

### “How do you prevent users accessing each other's tasks?”

“The service gets the username from the authenticated security context, loads that user, and uses repository methods that query by both task ID and user, or list tasks by user. That ownership check is separate from simply requiring a logged-in user.”

### “Why use Flyway?”

“Flyway keeps database schema changes in ordered versioned SQL scripts. It records applied migrations, so developers can understand which schema version an environment has. Hibernate is configured to validate mappings rather than automatically change the schema.”

### “Why use DTOs?”

“DTOs define the API request and response shape separately from the persistence entity. This helps validation and keeps internal fields and relationships from being exposed accidentally.”

### “What would you improve?”

A thoughtful answer is to mention changes you have evaluated, not pretend they are already done:
- Align controller authorization annotations and comments.
- Add tests for cross-user access and every security rule.
- Use consistent DTO mapping instead of repeating mapping code in controllers.
- Ensure global exception responses are consistent.
- Add integration tests against a test database.
- Add pagination if task lists grow.
- Add CI to build and test every change.
- Keep secrets out of source and use HTTPS.
- Consider a consistent UTC timestamp strategy.

## 17. Your 2-minute project presentation

“My project is a Task Manager REST API built using Java 21 and Spring Boot. It exposes endpoints to create, retrieve, update, delete and filter tasks by status. The API also includes user registration and login with JWT-based authentication.

I used Spring MVC for request handling, service classes for application logic, Spring Data JPA for persistence, and PostgreSQL for storage. The task entity is linked to a user, and the service retrieves tasks using the authenticated user so that task access is scoped by ownership. DTOs separate API payloads from database entities, and Flyway manages versioned schema migrations.

I also worked with Bean Validation, centralised exception handling, Swagger/OpenAPI documentation, and JUnit/Mockito tests. Docker provides a repeatable way to package the application. Building the project taught me how an HTTP request moves through security, controller, service, repository and database layers, and how to debug issues across those layers.”

Only claim features you can explain and demonstrate in the current code.

## 18. Practical revision checklist

- [ ] Open `TaskController.java` and explain each mapping.
- [ ] Trace `POST /tasks` into `TaskService.createTask`.
- [ ] Explain where the current username comes from.
- [ ] Explain why task repository methods include the user.
- [ ] Compare `TaskRequest`, `TaskResponse` and `Task`.
- [ ] Explain `@Entity`, `@Id`, `@ManyToOne`, `@JoinColumn` and `@Enumerated`.
- [ ] Explain `@PrePersist` and `@PreUpdate`.
- [ ] Explain each Flyway migration and the two indexes.
- [ ] Explain `ddl-auto=validate`.
- [ ] Trace registration and login.
- [ ] Explain JWT filter, security context and stateless sessions.
- [ ] Review the actual `@PreAuthorize` expressions and confirm the intended policy.
- [ ] Run the test suite and explain at least one test.
- [ ] Explain environment variables and Docker port mapping.
- [ ] Demonstrate Swagger UI without revealing secrets.
- [ ] Be honest about what is implemented, deployed, and still planned.

## 19. Source files to revisit

- [TaskController.java](src/main/java/com/example/taskmanager/controller/TaskController.java)
- [AuthController.java](src/main/java/com/example/taskmanager/controller/AuthController.java)
- [TaskService.java](src/main/java/com/example/taskmanager/service/TaskService.java)
- [Task entity](src/main/java/com/example/taskmanager/entity/Task.java)
- [SecurityConfig.java](src/main/java/com/example/taskmanager/config/SecurityConfig.java)
- [Application properties](src/main/resources/application.properties)
- [V1 users migration](src/main/resources/db/migration/V1__create_users_table.sql)
- [V2 task migration](src/main/resources/db/migration/V2__create_task_table.sql)
- [V3 index migration](src/main/resources/db/migration/V3__create_task_indexes.sql)
- [pom.xml](pom.xml)
- [Dockerfile](Dockerfile)

---
**Learning advice:** read one section, open the corresponding class, explain it aloud in your own words, and then answer one follow-up question. Understanding the request flow is more valuable than memorising annotation definitions.
