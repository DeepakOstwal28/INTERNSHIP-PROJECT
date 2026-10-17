# StreamFlow — Enterprise Media Streaming Platform with API Gateway & RBAC

StreamFlow is a production-grade media streaming backend and responsive web application engineered with **Java 17/21**, **Spring Boot 3.3**, **Spring Security 6**, and **Relational SQL**. 

It provides an integrated API Gateway architecture featuring **stateless JWT authentication**, **sliding-window rate limiting**, **dynamic Role-Based Access Control (RBAC)** with runtime administrative delegation, and **time-decaying permission passes** for content access.

---

## Table of Contents
1. [Project Overview & Resume Highlights](#1-project-overview--resume-highlights)
2. [Technologies, Libraries & Tools Used](#2-technologies-libraries--tools-used)
3. [Repository Directory & Code File Map](#3-repository-directory--code-file-map)
4. [Relational Database Schema (SQL)](#4-relational-database-schema-sql)
5. [Step-by-Step Request Lifecycle & Code Execution](#5-step-by-step-request-lifecycle--code-execution)
6. [API Endpoints Reference](#6-api-endpoints-reference)
7. [How to Run & Verify the Project](#7-how-to-run--verify-the-project)
8. [Comprehensive Interview Master Guide](#8-comprehensive-interview-master-guide)
   - [60-Second Elevator Pitch](#60-second-elevator-pitch)
   - [Top 12 Technical Interview Questions & Answers](#top-12-technical-interview-questions--answers)
   - [Key Architecture Trade-offs & Talking Points](#key-architecture-trade-offs--talking-points)

---

## 1. Project Overview & Resume Highlights

### Resume Description & Bullet Points (Ready for CV/Portfolio)

**Project Title**: StreamFlow — Media Streaming Platform with Centralized Gateway & Dynamic RBAC  
**Role**: Backend Engineer / Full-Stack Java Developer  
**Technologies**: Java 21, Spring Boot 3.3, Spring Security 6, JJWT, Spring Data JPA, SQL (H2/PostgreSQL), Vanilla JS, CSS3, Maven  

- **Architected a Centralized Security Gateway with Spring Boot 3 & Spring Security 6**: Developed a stateless security pipeline intercepting all inbound HTTP traffic, validating HMAC-SHA256 JWT tokens, and enforcing fine-grained CORS and BCrypt credential encryption across protected REST APIs.
- **Engineered a Thread-Safe Sliding-Window Rate Limiting Filter**: Implemented IP-based request throttling (`ConcurrentHashMap` sliding window) to prevent DoS and brute-force attacks, attaching standard headers (`X-RateLimit-Limit`, `X-RateLimit-Remaining`) and returning HTTP 429 upon quota breaches (30 req/min for auth, 120 req/min for catalog).
- **Designed Dynamic Role-Based Access Control (RBAC)**: Created an administrative authorization engine allowing real-time role promotion and demotion (`ROLE_ADMIN`, `ROLE_USER`) through REST endpoints without requiring application restarts or database migrations.
- **Implemented Time-Bounded Access Grants with Relational SQL**: Designed a normalized schema supporting both permanent catalog subscriptions and temporary trial passes (e.g., 2, 6, 24 hours) with automatic SQL timestamp validation (`expires_at`), complete with a user request and admin approval workflow.
- **Constructed a Modern, Minimalist Web Dashboard**: Built a lightweight frontend with pure HTML5, modern CSS3 (clean white background, minimal subtle shading, Inter typography), and vanilla JavaScript featuring catalog browsing, media streaming modal, request submission, and live admin analytics.

---

## 2. Technologies, Libraries & Tools Used

| Layer / Domain | Technology / Tool | Version | Purpose & Rationale |
| :--- | :--- | :--- | :--- |
| **Language** | Java | 17 / 21 | Core programming language offering strong typing, virtual threads, and modern concurrency features. |
| **Framework** | Spring Boot | 3.3.4 | Provides dependency injection, auto-configuration, embedded Tomcat servlet container, and production-ready metrics. |
| **Security** | Spring Security | 6.3.3 | Provides the servlet filter chain architecture, authentication management, and `@PreAuthorize` method security. |
| **Authentication** | JJWT (Java JWT) | 0.12.6 | Industry-standard library used for generating, signing, and parsing HMAC-SHA256 stateless JSON Web Tokens. |
| **Data Access** | Spring Data JPA / Hibernate | 6.5.3 | Object-Relational Mapping (ORM) and repository abstraction providing type-safe SQL queries and entity relationships. |
| **Database** | Relational SQL (H2 / PostgreSQL) | H2 2.2 / ANSI SQL | Normalized relational store for users, roles, permissions, time-bound grants, and payment audit logs. Zero-config H2 for local testing; ANSI-SQL compatible with PostgreSQL. |
| **Password Security** | BCrypt (Spring Crypto) | 10 rounds | Adaptive key derivation function for secure one-way password hashing with per-user salt. |
| **Frontend UI** | HTML5, CSS3, Vanilla JS (ES6+) | Modern Web Standards | Zero-dependency, lightweight, fast-loading client interface adhering to modern minimalist design (white surface, subtle borders). |
| **Typography** | Inter (Google Fonts) | Variable | Professional, clean typographic hierarchy designed specifically for web applications. |
| **Build & Tooling** | Apache Maven | 3.9+ | Standard dependency management and project build tool. |
| **Automation** | PowerShell Scripts | 5.1 / 7+ | End-to-end integration test runners (`test_flow.ps1`) verifying all 13 core functional assertions against live endpoints. |

---

## 3. Repository Directory & Code File Map

Here is the exact structure of the project and where every line of code resides:

```
testing-project/
├── pom.xml                                      # Maven project configuration & dependencies
├── START_PROJECT.bat                            # Windows one-click launcher
├── test_flow.ps1                                # Automated end-to-end integration test script (13 assertions)
├── test_api.ps1                                 # Quick PowerShell API smoke test script
├── ARCHITECTURE_AND_SCHEMA.md                   # Architectural design & SQL entity documentation
├── README.md                                    # Master project documentation & interview guide
└── src/
    └── main/
        ├── java/com/mediastream/
        │   ├── MediaStreamApplication.java      # Spring Boot application entry point (@SpringBootApplication)
        │   │
        │   ├── config/                          # Security, Gateway Filters & System Seeding
        │   │   ├── SecurityConfig.java          # Spring Security 6 filter chain configuration, CORS, session policy
        │   │   ├── JwtTokenProvider.java        # JWT creation, secret key signing, claims extraction, validation
        │   │   ├── JwtAuthenticationFilter.java # OncePerRequestFilter parsing Authorization: Bearer <token>
        │   │   ├── RateLimitingFilter.java      # Gateway sliding-window request throttling filter
        │   │   └── DataInitializer.java         # CommandLineRunner seeding roles, permissions, admin & catalog
        │   │
        │   ├── controller/                      # REST API Web Layer
        │   │   ├── AuthController.java          # Endpoints: /api/auth/register, /login, /me
        │   │   ├── MediaController.java         # Endpoints: /api/media, /api/media/{id}/stream
        │   │   ├── PermissionController.java    # Endpoints: /api/permissions, /purchase, /request-access
        │   │   └── AdminController.java         # Endpoints: /api/admin/roles, /users, /requests, /stats
        │   │
        │   ├── service/                         # Core Business Logic Layer
        │   │   ├── AuthService.java             # User registration, authentication, JWT issuing
        │   │   ├── CustomUserDetailsService.java# Resolves users by username or email for Spring Security
        │   │   ├── MediaService.java            # Catalog filtering & streaming permission validation
        │   │   ├── PermissionService.java       # Package purchasing, access requests & expiration evaluation
        │   │   └── AdminService.java            # Dynamic role assignment, user promotion, request approval
        │   │
        │   ├── repository/                      # Data Access Layer (Spring Data JPA)
        │   │   ├── UserRepository.java          # User entity persistence (findByUsername, findByEmail)
        │   │   ├── RoleRepository.java          # Role entity persistence (findByName)
        │   │   ├── PermissionRepository.java    # Permission entity persistence (findByName)
        │   │   ├── UserPermissionGrantRepository.java # Active/expired access grants querying
        │   │   ├── MediaContentRepository.java  # Catalog items querying
        │   │   ├── AccessRequestRepository.java # Temporary access request approval tracking
        │   │   └── PaymentTransactionRepository.java # Purchase audit log tracking
        │   │
        │   ├── entity/                          # JPA Database Entities (JPA / Hibernate)
        │   │   ├── User.java                    # users table mapping
        │   │   ├── Role.java                    # roles table mapping
        │   │   ├── Permission.java              # permissions table mapping
        │   │   ├── UserPermissionGrant.java     # user_permission_grants table mapping (expires_at logic)
        │   │   ├── MediaContent.java            # media_content table mapping
        │   │   ├── AccessRequest.java           # access_requests table mapping
        │   │   └── PaymentTransaction.java      # payment_transactions table mapping
        │   │
        │   ├── dto/                             # Data Transfer Objects
        │   │   ├── AuthRequest.java             # Login payload (username/email + password)
        │   │   ├── AuthResponse.java            # Authentication token response with roles & permissions
        │   │   ├── RegisterRequest.java         # Registration payload
        │   │   ├── UserDto.java                 # Safe user profile representation (excluding password)
        │   │   ├── AssignRoleDto.java           # Role assignment payload for RBAC updates
        │   │   ├── MediaDto.java                # Catalog card presentation model
        │   │   ├── CreateAccessRequestDto.java  # Temporary pass request submission model
        │   │   ├── AccessRequestDto.java        # Request status presentation model
        │   │   ├── PurchasePermissionDto.java   # Subscription purchase model
        │   │   ├── AdminStatsDto.java           # Platform metrics and analytical counters
        │   │   └── ApiResponse.java             # Standardized generic JSON response envelope
        │   │
        │   └── exception/                       # Error Handling
        │       ├── ApiException.java            # Custom runtime exception with HTTP status codes
        │       └── GlobalExceptionHandler.java  # @RestControllerAdvice returning structured error responses
        │
        └── resources/
            ├── application.properties           # Spring Boot configuration (H2, JPA, Server port, JWT secrets)
            ├── schema.sql                       # Standalone ANSI SQL DDL script for database provisioning
            └── static/                          # Minimalist Web Frontend
                ├── index.html                   # Semantic HTML single-page dashboard & admin panel
                ├── css/styles.css               # Clean white design system with subtle contrast
                └── js/app.js                    # Client application logic, API client, modal controllers
```

---

## 4. Relational Database Schema (SQL)

The database schema is fully normalized (3NF) to guarantee transactional integrity, auditability, and fast indexed lookups.

### Entity Relationship Diagram (ERD)

```
┌──────────────────┐         ┌───────────────────────┐         ┌──────────────────┐
│      users       │ 1     * │      user_roles       │ *     1 │      roles       │
├──────────────────┤─────────┼───────────────────────┼─────────┼──────────────────┤
│ id (PK)          │         │ user_id (FK)          │         │ id (PK)          │
│ username (UQ)    │         │ role_id (FK)          │         │ name (UQ)        │
│ email (UQ)       │         └───────────────────────┘         └──────────────────┘
│ password (BCrypt)│
│ enabled          │         ┌───────────────────────┐         ┌──────────────────┐
│ created_at       │ 1     * │user_permission_grants │ *     1 │   permissions    │
└──────────────────┘─────────┼───────────────────────┼─────────┼──────────────────┤
         │                   │ id (PK)               │         │ id (PK)          │
         │                   │ user_id (FK)          │         │ name (UQ)        │
         │                   │ permission_id (FK)    │         │ description      │
         │                   │ granted_at            │         │ price            │
         │                   │ expires_at (Nullable) │         └──────────────────┘
         │                   │ grant_type            │                   │
         │                   └───────────────────────┘                   │
         │                                                               │
         │ 1                                                           * │ 1
         ├──────────────────────┬────────────────────────────────────────┤
         │                      │
         ▼ *                    ▼ *
┌──────────────────┐   ┌──────────────────────┐   ┌───────────────────────┐
│ access_requests  │   │ payment_transactions │   │     media_content     │
├──────────────────┤   ├──────────────────────┤   ├───────────────────────┤
│ id (PK)          │   │ id (PK)              │   │ id (PK)               │
│ user_id (FK)     │   │ user_id (FK)         │   │ title                 │
│ permission_id(FK)│   │ permission_id (FK)   │   │ description           │
│ requested_hours  │   │ amount               │   │ category              │
│ reason           │   │ payment_method       │   │ video_url             │
│ status           │   │ status               │   │ thumbnail_url         │
│ admin_notes      │   │ transaction_ref (UQ) │   │ required_permission   │
│ created_at       │   │ created_at           │   │ duration_minutes      │
│ reviewed_at      │   └──────────────────────┘   │ release_year          │
└──────────────────┘                              └───────────────────────┘
```

### Complete SQL DDL (`schema.sql`)

```sql
-- 1. ROLES TABLE (Role-Based Access Control)
CREATE TABLE IF NOT EXISTS roles (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE
);

-- 2. PERMISSIONS TABLE (Streaming modules & subscriptions)
CREATE TABLE IF NOT EXISTS permissions (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name VARCHAR(60) NOT NULL UNIQUE,
    description VARCHAR(255),
    price DOUBLE PRECISION NOT NULL
);

-- 3. USERS TABLE (Core authentication & profile)
CREATE TABLE IF NOT EXISTS users (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 4. USER_ROLES JOIN TABLE (Many-to-Many User <-> Role)
CREATE TABLE IF NOT EXISTS user_roles (
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    CONSTRAINT fk_user_roles_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT fk_user_roles_role FOREIGN KEY (role_id) REFERENCES roles (id) ON DELETE CASCADE
);

-- 5. USER_PERMISSION_GRANTS (Temporary or permanent access with expiration)
CREATE TABLE IF NOT EXISTS user_permission_grants (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    user_id BIGINT NOT NULL,
    permission_id BIGINT NOT NULL,
    granted_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    expires_at TIMESTAMP NULL, -- NULL = permanent, timestamp = temporary time-bound access
    grant_type VARCHAR(50) NOT NULL, -- 'PURCHASE', 'ADMIN_TEMP_GRANT', 'ADMIN_PERMANENT_GRANT'
    CONSTRAINT fk_user_grants_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT fk_user_grants_permission FOREIGN KEY (permission_id) REFERENCES permissions (id) ON DELETE CASCADE
);

-- 6. MEDIA_CONTENT TABLE (Catalog items with access restrictions)
CREATE TABLE IF NOT EXISTS media_content (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    title VARCHAR(150) NOT NULL,
    description VARCHAR(1000),
    category VARCHAR(30) NOT NULL, -- 'MOVIE', 'ANIME', 'SERIES', 'MUSIC'
    video_url VARCHAR(500) NOT NULL,
    thumbnail_url VARCHAR(500),
    required_permission VARCHAR(60) NOT NULL,
    duration_minutes INTEGER,
    release_year INTEGER
);

-- 7. ACCESS_REQUESTS TABLE (User requests admin for temporary access)
CREATE TABLE IF NOT EXISTS access_requests (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    user_id BIGINT NOT NULL,
    permission_id BIGINT NOT NULL,
    requested_hours INTEGER NOT NULL,
    reason VARCHAR(500),
    status VARCHAR(30) NOT NULL DEFAULT 'PENDING', -- 'PENDING', 'APPROVED', 'REJECTED'
    admin_notes VARCHAR(500),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    reviewed_at TIMESTAMP NULL,
    CONSTRAINT fk_access_req_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT fk_access_req_perm FOREIGN KEY (permission_id) REFERENCES permissions (id) ON DELETE CASCADE
);

-- 8. PAYMENT_TRANSACTIONS TABLE (Purchase logs & revenue tracking)
CREATE TABLE IF NOT EXISTS payment_transactions (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    user_id BIGINT NOT NULL,
    permission_id BIGINT NOT NULL,
    amount DOUBLE PRECISION NOT NULL,
    payment_method VARCHAR(50) NOT NULL,
    status VARCHAR(30) NOT NULL,
    transaction_ref VARCHAR(100) NOT NULL UNIQUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_payment_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT fk_payment_perm FOREIGN KEY (permission_id) REFERENCES permissions (id) ON DELETE CASCADE
);
```

---

## 5. Step-by-Step Request Lifecycle & Code Execution

### Step 1: Inbound Request & Gateway Rate Limiting
- Every HTTP request first hits `RateLimitingFilter.java` (registered before security filters).
- The filter resolves the client IP (`X-Forwarded-For` or `request.getRemoteAddr()`).
- It accesses a concurrent sliding window (`ConcurrentHashMap<String, Deque<Long>>`) tracking timestamps.
- It purges timestamps older than 60 seconds (`now - 60000`).
- If request count exceeds the route quota:
  - Auth routes (`/api/auth/**`): **30 requests/minute**.
  - Catalog routes (`/api/media/**`): **120 requests/minute**.
  - General routes: **60 requests/minute**.
- If quota is exceeded, it sets HTTP `429 Too Many Requests` and terminates the request immediately.
- If allowed, it appends response headers:
  - `X-RateLimit-Limit`: Maximum requests per window.
  - `X-RateLimit-Remaining`: Remaining allowance.
  - `X-RateLimit-Reset`: Milliseconds until window resets.

### Step 2: Authentication & JWT Token Verification
- Next, the request reaches `JwtAuthenticationFilter.java`.
- If the endpoint is public (`/api/auth/**`, `/index.html`, `/css/**`, `/js/**`), the filter delegates down the chain without requiring tokens.
- For protected endpoints, it extracts `Authorization: Bearer <token>`.
- `JwtTokenProvider.java` validates the HMAC-SHA256 signature against the configured secret key.
- It parses the token claims, extracts the `sub` (username) and `roles` array.
- It constructs a `UsernamePasswordAuthenticationToken` and installs it into Spring's `SecurityContextHolder`.

### Step 3: Authorization & RBAC Enforcement
- `SecurityConfig.java` enforces URL pattern authorization:
  - `.requestMatchers("/api/admin/**").hasRole("ADMIN")`
  - `.requestMatchers("/api/media/**", "/api/permissions/**").authenticated()`
  - `.anyRequest().permitAll()`
- If a user with only `ROLE_USER` attempts to access `/api/admin/users`, Spring Security intercepts the call and emits HTTP `403 Forbidden`.

### Step 4: Dynamic Admin Role Management (RBAC Assignment)
- An Administrator calls `PUT /api/admin/users/{userId}/roles` with payload `{"roles": ["ROLE_ADMIN", "ROLE_USER"]}`.
- `AdminController.java` routes the call to `AdminService.java`.
- `AdminService` verifies the user exists in `UserRepository`.
- It looks up the requested role names from `RoleRepository`, normalizing any casing or prefixes.
- It updates the user's role set in the database and persists the entity.
- The next time the user makes an authenticated request or re-logs in, their updated privileges are immediately evaluated.

### Step 5: Media Streaming Permission Verification
- When a user requests `GET /api/media/3/stream` (e.g. Anime catalog):
- `MediaController.java` calls `MediaService.getStreamUrl(mediaId, currentUser)`.
- If `currentUser` has `ROLE_ADMIN`, stream access is immediately granted.
- Otherwise, `MediaService` queries `UserPermissionGrantRepository`:
  - It checks whether the user has a grant for `STREAM_ANIME` or `VIP_ACCESS`.
  - It validates the timestamp: `expires_at IS NULL OR expires_at > LocalDateTime.now()`.
- If a grant is found and valid: HTTP `200 OK` is returned with the streaming video URL.
- If no active grant exists or the temporary grant expired: HTTP `403 Forbidden` is returned with `ApiError: You do not have permission to stream this content`.

### Step 6: Temporary Access Request & Admin Approval Flow
1. **User Request**: User calls `POST /api/permissions/request-access` specifying `permissionId: 3`, `requestedHours: 2`, and a `reason`.
2. An `AccessRequest` entity is stored in the database with status `PENDING`.
3. **Admin Review**: Admin opens the Admin Console, reviews pending requests, and calls `POST /api/admin/requests/{id}/approve`.
4. `AdminService` updates the request status to `APPROVED`.
5. It creates a `UserPermissionGrant` with:
   - `grantedAt`: `LocalDateTime.now()`
   - `expiresAt`: `LocalDateTime.now().plusHours(requestedHours)`
   - `grantType`: `"ADMIN_TEMP_GRANT"`
6. The user can immediately stream the media. Once the 2 hours elapse, subsequent streaming checks automatically deny access.

---

## 6. API Endpoints Reference

### Authentication (`/api/auth`)
| Method | Endpoint | Access | Description |
| :--- | :--- | :--- | :--- |
| `POST` | `/api/auth/register` | Public | Registers a new account and returns a signed JWT token. |
| `POST` | `/api/auth/login` | Public | Authenticates user by username/email and password; returns JWT token. |
| `GET` | `/api/auth/me` | Authenticated | Fetches current user profile, assigned roles, and active permissions. |

### Media Catalog (`/api/media`)
| Method | Endpoint | Access | Description |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/media` | Authenticated | Lists all catalog items annotated with accessibility flags. |
| `GET` | `/api/media/{id}/stream` | Authenticated | Returns media streaming URL (enforces active permissions). |
| `POST` | `/api/media` | Admin Only | Creates a new media item in the catalog. |

### Permissions & Subscriptions (`/api/permissions`)
| Method | Endpoint | Access | Description |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/permissions` | Authenticated | Lists available streaming subscription tiers and prices. |
| `POST` | `/api/permissions/purchase` | Authenticated | Simulates subscription purchase and grants a 30-day pass. |
| `POST` | `/api/permissions/request-access` | Authenticated | Submits a request for a temporary time-bounded access pass. |
| `GET` | `/api/permissions/my-requests` | Authenticated | Retrieves current user's submitted access requests and review statuses. |

### Administration & RBAC (`/api/admin`)
| Method | Endpoint | Access | Description |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/admin/roles` | Admin Only | Lists all available system security roles (`ROLE_ADMIN`, `ROLE_USER`). |
| `GET` | `/api/admin/users` | Admin Only | Lists all users, active grants, and assigned roles. |
| `PUT` | `/api/admin/users/{id}/roles` | Admin Only | Dynamically updates and reassigns user roles (RBAC). |
| `POST` | `/api/admin/users/{id}/permissions/{permId}/grant` | Admin Only | Manually provisions a streaming permission to a user. |
| `POST` | `/api/admin/users/{id}/permissions/{permId}/revoke` | Admin Only | Revokes a streaming permission from a user. |
| `GET` | `/api/admin/requests` | Admin Only | Lists all user access requests (pending, approved, rejected). |
| `POST` | `/api/admin/requests/{id}/approve` | Admin Only | Approves a request and provisions temporary time-bounded access. |
| `POST` | `/api/admin/requests/{id}/reject` | Admin Only | Rejects a request with reviewer notes. |
| `GET` | `/api/admin/stats` | Admin Only | Returns platform counters: total users, revenue, active passes. |

---

## 7. How to Run & Verify the Project

### Prerequisites
- **Java Development Kit (JDK)**: Java 17 or Java 21 installed.
- **Apache Maven**: 3.8+ (or use wrapper).

### 1. Launching the Platform

**Option A (Windows batch script)**:
Double-click or run:
```cmd
START_PROJECT.bat
```

**Option B (Maven direct)**:
```bash
mvn spring-boot:run
```

The application starts on **http://localhost:8080**.

### 2. Accessing the User Interfaces
- **Web Platform**: Open `http://localhost:8080` in any web browser.
- **H2 SQL Database Console**: Open `http://localhost:8080/h2-console`
  - JDBC URL: `jdbc:h2:mem:mediastreamdb`
  - User Name: `sa`
  - Password: *(leave blank)*

### 3. Default Seed Accounts
| Account | Email / Username | Password | Role | Default Access |
| :--- | :--- | :--- | :--- | :--- |
| **Administrator** | `admin@example.com` (`admin`) | `admin123` | `ROLE_ADMIN`, `ROLE_USER` | Universal Admin Access (`VIP_ACCESS`) |
| **Standard User** | `user@example.com` (`user`) | `user123` | `ROLE_USER` | `STREAM_MOVIES` (Movies Pass) |

### 4. Running the Automated Integration Test Suite
To verify all 13 core functional assertions (rate limiting, authentication, 403 authorization guards, request approvals, and RBAC promotion/demotion):
```powershell
powershell -ExecutionPolicy Bypass -File .\test_flow.ps1
```

---

## 8. Comprehensive Interview Master Guide

### 60-Second Elevator Pitch
> *"StreamFlow is a media streaming backend and web platform built with Java 21, Spring Boot 3, and Spring Security 6. I designed it to solve centralized security and access governance challenges in content distribution.*  
> *The architecture includes an integrated API Gateway layer that handles custom sliding-window rate limiting to defend against brute-force attacks, stateless JWT authentication with BCrypt hashing, and dynamic Role-Based Access Control (RBAC).*  
> *A unique feature I implemented is time-bounded permission passes: users can request temporary trial access (like 2 hours for Anime), which administrators can approve in real-time. The system calculates expiration timestamps in SQL and automatically revokes access once the window closes.*  
> *I also built an automated integration suite in PowerShell that validates all 13 security and business assertions with zero errors."*

---

### Top 12 Technical Interview Questions & Answers

#### Q1: Why did you choose JWT over traditional server-side HTTP Sessions?
**Answer:**  
*"Traditional HTTP sessions require stateful server memory (or a shared session cache like Redis) to store session IDs. When scaling horizontally across multiple backend instances, stateful sessions either require sticky routing on the load balancer or distributed session synchronization, which adds latency and architectural overhead.*  
*With JSON Web Tokens (JWT), authentication is completely stateless. The client sends the signed token in the `Authorization: Bearer` header on every request. Our Spring Boot gateway validates the HMAC-SHA256 signature and extracts the claims (username, user ID, roles) in memory without a single database lookup. This allows any instance in a cluster to verify requests independently, offering seamless horizontal scalability."*

#### Q2: How does your sliding-window rate limiter work, and why not use a fixed-window counter?
**Answer:**  
*"Fixed-window rate limiters count requests within discrete time buckets (e.g., 00:00 to 00:59). Their main flaw is the **boundary burst problem**: a user can send 30 requests at 00:59 and another 30 requests at 01:00, effectively executing 60 requests in a two-second window without triggering the limit.*  
*To solve this, I built a **sliding-window log algorithm** in `RateLimitingFilter.java`. Each client IP is mapped in a thread-safe `ConcurrentHashMap` to a `Deque<Long>` containing timestamps of recent requests. When a request arrives, the filter purges timestamps older than the 60-second window (`now - 60000`). It then checks if the remaining count is below the quota (30 for auth routes, 120 for catalog routes). This guarantees that at any arbitrary point in time, the user cannot exceed the limit within any continuous 60-second span."*

#### Q3: How do you handle concurrency and race conditions in the rate limiter?
**Answer:**  
*"In a multi-threaded servlet environment like Tomcat, multiple requests from the same IP can arrive simultaneously on different worker threads. In `RateLimitingFilter.java`, I used `ConcurrentHashMap.compute()` which executes atomically on the map entry. Inside the compute block, the `Deque` operations (purging expired timestamps and adding the current timestamp) occur within a synchronized or atomic context. In an enterprise distributed setting, I would offload this to Redis using an atomic Redis Lua script executing `ZREMRANGEBYSCORE` and `ZCARD` to ensure cluster-wide atomicity with sub-millisecond latency."*

#### Q4: How is Role-Based Access Control (RBAC) enforced in Spring Security 6?
**Answer:**  
*"In Spring Security 6, we configure the `SecurityFilterChain` bean. When `JwtAuthenticationFilter` validates a token, it extracts the roles and maps each to a `SimpleGrantedAuthority`. In Spring Security conventions, roles are prefixed with `ROLE_` (e.g., `ROLE_ADMIN`, `ROLE_USER`).*  
*Route-level protection is configured using `.requestMatchers("/api/admin/**").hasRole("ADMIN")`. Spring Security inspects the `Authentication` object stored in `SecurityContextHolder`. If the user lacks `ROLE_ADMIN`, an `AccessDeniedException` is thrown, which translates into an HTTP `403 Forbidden` response before reaching the controller. For fine-grained method security, we can also use annotations like `@PreAuthorize("hasRole('ADMIN')")`."*

#### Q5: How do you handle dynamic role changes in a stateless JWT architecture?
**Answer:**  
*"In a purely stateless JWT architecture, the token contains the user's roles at the time of issuance. If an admin promotes a user from `ROLE_USER` to `ROLE_ADMIN`, an existing token would still contain only `ROLE_USER` until it expires.*  
*In our implementation, we solve this through two mechanisms:*  
1. *The admin endpoint `PUT /api/admin/users/{userId}/roles` updates the database record immediately.*  
2. *On the client side, our frontend or mobile client detects role changes or refreshes the token by calling `/api/auth/me` or `/api/auth/login`, immediately receiving an updated JWT.*  
3. *For high-security operations, critical endpoints (like financial operations or permission approvals) check the live database grant or token version rather than relying solely on the cached JWT payload."*

#### Q6: How do time-decaying temporary access permissions work in the database?
**Answer:**  
*"Instead of running expensive background polling jobs that constantly sweep the database to delete expired permissions, we use an **on-demand timestamp evaluation strategy**.*  
*In the `user_permission_grants` table, each record contains an `expires_at` timestamp (which is `NULL` for permanent passes, and a future timestamp for temporary passes). When a user requests content playback (`GET /api/media/{id}/stream`), `MediaService` runs a SQL query:*  
```sql
SELECT g FROM UserPermissionGrant g 
WHERE g.user = :user AND g.permission = :perm 
AND (g.expiresAt IS NULL OR g.expiresAt > :now)
```  
*If the current time has passed `expires_at`, the query returns empty, and access is instantly rejected with HTTP 403. This eliminates the need for cron jobs, prevents stale access, and guarantees 100% time accuracy."*

#### Q7: Why did you choose a Relational Database (SQL) over NoSQL?
**Answer:**  
*"Media platforms handle financial purchases, transactional subscriptions, and strict access governance. Relational databases provide **ACID guarantees**:*  
- *Atomicity: When a user purchases a subscription, generating the `payment_transactions` record and creating the `user_permission_grants` entry must succeed together or roll back completely.*  
- *Consistency & Referencing: Foreign keys ensure that access requests and permission grants can never point to non-existent users or deleted permissions (`ON DELETE CASCADE`).*  
- *NoSQL documents would either require denormalizing user permissions across every media record (causing update anomalies) or performing multi-document queries without referential integrity."*

#### Q8: How does Spring Boot's internal filter chain order these components?
**Answer:**  
*"Servlet filters run in a strict, deterministic sequence:*  
1. *`RateLimitingFilter`: Positioned first. We want to drop malicious or flooding requests as early as possible before allocating resources for cryptography or database access.*  
2. *`JwtAuthenticationFilter`: Positioned before `UsernamePasswordAuthenticationFilter`. It inspects the header, verifies the token, and establishes user identity in `SecurityContextHolder`.*  
3. *`SecurityFilterChain` (Authorization): Evaluates route matchers and role prerequisites.*  
4. *`DispatcherServlet` & `@RestController`: The request is only handed over to Spring MVC once all filters approve it."*

#### Q9: How do you prevent brute-force credential stuffing attacks?
**Answer:**  
*"We implement defense-in-depth across three layers:*  
1. *Layer 1 (Gateway): Route-specific rate limiting throttles `/api/auth/login` to **30 requests per minute per IP**, blocking automated dictionary attacks.*  
2. *Layer 2 (Cryptography): Passwords are never stored in plaintext. We use `BCryptPasswordEncoder` with 10 salt rounds. BCrypt is deliberately computationally expensive (CPU and memory intensive), making offline rainbow-table or brute-force attempts unfeasible.*  
3. *Layer 3 (Error Masking): When login fails, the response states `Invalid username or password` uniformly, preventing user enumeration attacks."*

#### Q10: How would you scale this architecture to handle 100,000 concurrent users?
**Answer:**  
*"To scale StreamFlow for high concurrency:*  
1. *Stateless Application Tier: Because our Spring Boot backend uses stateless JWTs, we can spin up multiple container instances behind a load balancer (NGINX or AWS ALB).*  
2. *Distributed Rate Limiting: Migrate the local `ConcurrentHashMap` rate limiter to a shared **Redis cluster** using Redis sliding logs or token bucket with Redis Lua scripts.*  
3. *Database Scaling: Deploy PostgreSQL with Read Replicas. High-frequency read queries (fetching media catalog, checking permissions) are served by replicas, while writes (purchases, user registration, role updates) target the primary node.*  
4. *CDN Caching: Cache media metadata and video segments (HLS / DASH chunks) on an edge CDN like Cloudflare or AWS CloudFront so that 95% of streaming bandwidth never touches the origin servers."*

#### Q11: What is the difference between RBAC and ABAC? Which one does this project use?
**Answer:**  
*"**RBAC (Role-Based Access Control)** assigns permissions to named roles (e.g., `ROLE_ADMIN`, `ROLE_USER`), and users inherit privileges based on their assigned roles.*  
*StreamFlow implements **RBAC** for system administrative operations (only `ROLE_ADMIN` can manage roles, approve requests, and access analytics).*  
*However, for content streaming, we augment RBAC with **ABAC-like attributes** (Attribute-Based Access Control): streaming access depends not just on a role, but on contextual attributes—specifically the content category (`required_permission`) and the temporal validity attribute (`expires_at > NOW()`). This hybrid design gives us both clean role management and fine-grained, time-sensitive access control."*

#### Q12: What was the most challenging technical issue you solved on this project?
**Answer:**  
*"One subtle challenge was ensuring **sliding-window rate limiting idempotency and thread-safety** under concurrent bursts without causing memory leaks from inactive IPs.*  
*If a high volume of unique IP addresses send one request and never return, the in-memory map would grow unbounded. I implemented an eviction mechanism that cleans up empty queues and prunes records exceeding the time window.*  
*Another challenge was ensuring the automated PowerShell test suite remained completely idempotent across repeated runs: when testing temporary permission expiration and 403 access guards, previously approved grants in the database would cause tests to pass when they were expected to fail. I resolved this by designing dynamic test accounts within the test runner, guaranteeing 100% test reproducibility every time."*

---

### Key Architecture Trade-offs & Talking Points

| Architectural Decision | Alternative Considered | Why We Chose Our Approach |
| :--- | :--- | :--- |
| **Monolith vs Microservices** | Multi-service Node.js / Spring Microservices with Eureka | Microservices introduce distributed tracing overhead, network serialization latency, and deployment complexity. A modular monolith provides the exact same architectural boundaries with zero network latency between layers. |
| **Stateless JWT vs HTTP Sessions** | Stateful Redis Sessions | Eliminates session synchronization across nodes, lowers database I/O, and provides instant horizontal elasticity. |
| **Sliding Window vs Fixed Window** | Fixed 1-Minute Counters | Eliminates the 'boundary burst' vulnerability where twice the allowable traffic can strike right at the reset boundary. |
| **On-Demand Expiry Check vs Cron Jobs** | Scheduled Quartz / Spring `@Scheduled` cleanups | Immediate consistency: checking `expires_at > NOW()` in SQL guarantees real-time enforcement with zero polling overhead. |
