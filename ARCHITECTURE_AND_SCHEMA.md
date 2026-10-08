# STREAMFLOW: Enterprise Media Streaming Platform
## Architecture, SQL Database Schema & Implementation Guide

---

## 1. Project Overview & Problem Solved
This project replaces the fragmented, unscalable multi-server microservice setup with an **Enterprise-grade Spring Boot 3 Monolith** backed by a **Relational SQL Database (PostgreSQL / H2)**, **Spring Security 6 with stateless JWT**, **BCrypt password hashing**, **Sliding-window Rate Limiting**, and **Role-Based Access Control (RBAC)** with **Time-Limited Temporary Permission Grants**.

---

## 2. Test Credentials
| Account Type | Username | Password | Roles | Default Permissions | Description |
| :--- | :--- | :--- | :--- | :--- | :--- |
| **Admin** | `admin` | `admin123` | `ROLE_ADMIN`, `ROLE_USER` | `ALL_ACCESS_ADMIN` | Can view all users, grant/revoke permissions, approve temporary access requests, and see financial stats. |
| **User** | `user` | `user123` | `ROLE_USER` | `STREAM_MOVIES` (Active) | Can stream Movies immediately. Anime, Series, and Music are locked until purchased or approved by admin. |
| **New Users** | *Any* | *Any* | `ROLE_USER` | None (Fresh account) | Can register directly via the "Create Account" tab on the web page. |

---

## 3. Relational Database Schema (SQL)

### Entity Relationship (ER) Diagram
```
+------------------+         +-----------------------+         +------------------+
|      users       | 1     * |      user_roles       | *     1 |      roles       |
+------------------+---------+-----------------------+---------+------------------+
| id (PK)          |         | user_id (FK)          |         | id (PK)          |
| username (UQ)    |         | role_id (FK)          |         | name (UQ)        |
| email (UQ)       |         +-----------------------+         +------------------+
| password (BCrypt)|
| enabled          |         +-----------------------+         +------------------+
| created_at       | 1     * |user_permission_grants | *     1 |   permissions    |
+------------------+---------+-----------------------+---------+------------------+
         |                   | id (PK)               |         | id (PK)          |
         |                   | user_id (FK)          |         | name (UQ)        |
         |                   | permission_id (FK)    |         | description      |
         |                   | granted_at            |         | price            |
         |                   | expires_at (Nullable) |         +------------------+
         |                   | grant_type            |                   |
         |                   +-----------------------+                   |
         |                                                               |
         | 1                                                           * | 1
         |----------------------+----------------------------------------+
         |                      |
         v *                    v *
+------------------+   +----------------------+   +-----------------------+
| access_requests  |   | payment_transactions |   |     media_content     |
+------------------+   +----------------------+   +-----------------------+
| id (PK)          |   | id (PK)              |   | id (PK)               |
| user_id (FK)     |   | user_id (FK)         |   | title                 |
| permission_id(FK)|   | permission_id (FK)   |   | description           |
| requested_hours  |   | amount               |   | category (MOVIE/etc.) |
| reason           |   | payment_method       |   | video_url             |
| status           |   | status (SUCCESS/FAIL)|   | thumbnail_url         |
| admin_notes      |   | transaction_ref (UQ) |   | required_permission   |
| created_at       |   | created_at           |   | duration_minutes      |
| reviewed_at      |   +----------------------+   | release_year          |
+------------------+                              +-----------------------+
```

### Table Definitions & Why SQL is Used Here:
1. **`users`**: Stores user authentication credentials. Passwords are encrypted with BCrypt (10 rounds).
2. **`roles`**: Stores security roles (`ROLE_USER`, `ROLE_ADMIN`).
3. **`user_roles`**: Join table providing a clean many-to-many relationship between users and roles.
4. **`permissions`**: Fine-grained streaming permissions (`STREAM_MOVIES`, `STREAM_SERIES`, `STREAM_ANIME`, `STREAM_MUSIC`, `VIP_ACCESS`) with individual subscription pricing.
5. **`user_permission_grants`**: **The core innovation of this project.** It tracks both permanent subscriptions and **temporary, time-limited access grants** (with `expires_at` timestamps). When a user requests 2 hours from an admin and the admin approves, an entry is created with `expires_at = NOW() + 2 hours`.
6. **`media_content`**: The streaming catalog items, tagged with category and the `required_permission` needed to watch.
7. **`access_requests`**: Stores user requests for temporary (e.g. 2, 6, 24 hours) access sent to the admin, with status tracking (`PENDING`, `APPROVED`, `REJECTED`).
8. **`payment_transactions`**: Logs all purchase transactions with unique transaction references (`TX-...`), amounts, and timestamps for financial auditing.

---

## 4. Security & Authentication Flow

```
User (Browser)               RateLimitingFilter             JwtAuthFilter               SecurityFilterChain             Controller / DB
     |                               |                             |                              |                            |
     |---- POST /api/auth/login ---->|                             |                              |                            |
     |                               |-- Check IP request count --|                              |                            |
     |                               |   (Max 30 req/min auth)    |                              |                            |
     |                               |---------------------------->|                              |                            |
     |                                                             |-- Skip JWT parsing for /auth|                             |
     |                                                             |----------------------------->|                            |
     |                                                                                            |-- Verify BCrypt Password ->|
     |                                                                                            |   Generate signed JWT token|
     |<------------------------------ Return JWT Token + Roles + Active Permissions --------------|                            |
     |                                                                                                                         |
     |                                                                                                                         |
     |---- GET /api/media/3/stream ->|                             |                              |                            |
     |     (Header: Bearer <token>)  |-- Check IP limit ---------->|                              |                            |
     |                                                             |-- Validate HMAC-SHA256 Token |                            |
     |                                                             |   Extract Username & Roles   |                            |
     |                                                             |   Set SecurityContextHolder -|                            |
     |                                                             |----------------------------->|                            |
     |                                                                                            |-- Verify user permission --|
     |                                                                                            |   If granted: Return stream|
     |<------------------------------ 200 OK (Stream Unlocked) / 403 Forbidden -------------------|                            |
```

---

## 5. End-to-End User Flow Explained for Interviews

### Step 1: User Registration & Login
- When an unregistered user arrives, they open the **Create Account** tab.
- Their password is encrypted using `BCryptPasswordEncoder` before writing to the `users` table.
- When they log in, Spring Security authenticates them, generates an HMAC-SHA256 **JWT token** containing their username and roles, and returns it to the client.
- The client stores the token in `localStorage` and includes it in all subsequent requests:
  `Authorization: Bearer <token>`

### Step 2: Content Discovery & Permission Verification
- The user views the **Media Catalog** (Movies, Anime, TV Series, Music).
- The backend evaluates the user's active permissions against each item's `required_permission`.
- If the permission is active (and not expired), the item displays **🟢 Unlocked** with a **Stream Now** button.
- If the permission is missing, the item displays **🔒 Locked** with two options:
  1. **💳 Purchase Access**: Pay to instantly unlock for 30 days.
  2. **⏱ Request Access**: Request the administrator for temporary access (e.g. 2 hours, 6 hours).

### Step 3: Temporary Access Request & Admin Approval
- The user clicks **Request Access**, selects their desired duration (e.g., 2 hours), provides a reason, and submits the form.
- A record is created in `access_requests` with status `PENDING`.
- The Admin opens the **Admin Console** (`http://localhost:8080` logged in as `admin / admin123`), views pending requests in real time, and clicks **Approve**.
- The backend automatically creates a `user_permission_grants` entry with `expires_at = LocalDateTime.now().plusHours(2)`.
- The user can immediately stream the media! Once the 2 hours elapse, the system automatically revokes access.

### Step 4: Purchase / Payment Simulation
- The user can also purchase a monthly pass for any category (e.g. TV Series for $12.99).
- The system generates an audited `payment_transactions` record with a unique transaction reference (e.g. `TX-509110BA`), marks status `SUCCESS`, and creates an active 30-day grant.
- Revenue metrics are dynamically updated on the Admin dashboard.

---

## 6. How to Run the Project
1. Open PowerShell or Command Prompt in `d:\testing-project - Copy`.
2. Run:
   ```bash
   START_PROJECT.bat
   ```
   *Or with Maven directly:*
   ```bash
   mvn spring-boot:run
   ```
3. Open your browser and navigate to:
   - **Web Application**: [http://localhost:8080](http://localhost:8080)
   - **H2 SQL Database Console**: [http://localhost:8080/h2-console](http://localhost:8080/h2-console)
     - JDBC URL: `jdbc:h2:mem:mediastreamdb`
     - User Name: `sa`
     - Password: *(leave blank)*
