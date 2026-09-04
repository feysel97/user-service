# Spring Boot User Profile Microservice

A production-ready, stateless User Profile Management microservice built to operate within a distributed backend architecture. 

This service demonstrates advanced microservice patterns, acting as a protected Resource Server that trusts an external Authentication Service via shared-secret JSON Web Tokens (JWT). It features modern Java 21 data structures, comprehensive search pagination, and database isolation using Docker.

## 🚀 Key Features

* **Stateless JWT Validation:** Custom security filter that parses and verifies incoming JWTs using JJWT 0.12.x without querying an authentication database.
* **Database-per-Service Architecture:** Completely isolated MySQL database running inside Docker, adhering to strict microservice decoupling rules.
* **Role-Based Access Control (RBAC):** Method-level security (`@PreAuthorize`) enforcing administrative privileges extracted directly from token claims.
* **Java 21 Records:** Immutable, boilerplate-free Data Transfer Objects (DTOs) for clean API request/response contracts.
* **Soft Deletion / Account Deactivation:** Profiles are deactivated via status flags (`ACTIVE`, `INACTIVE`) rather than hard-deleted from the database.
* **Dynamic Search & Pagination:** Optimized Spring Data JPA queries to search users by keyword across multiple fields, returning paginated data to prevent memory overflow.
* **Centralized Exception Handling:** Intercepts `AccessDeniedException`, `UserNotFoundException`, and validation errors to return unified JSON error payloads.

## 🛠️ Tech Stack

* **Runtime:** Java 21 (LTS)
* **Framework:** Spring Boot 3.4.2
* **Security:** Spring Security 6, JJWT (0.12.5)
* **Database:** MySQL 8 (Dockerized) & Spring Data JPA (Hibernate)
* **Build Tool:** Gradle 8.5+

## 🛤️ API Endpoints

| Method | Endpoint | Access | Request Body | Description |
|---|---|---|---|---|
| `GET` | `/api/v1/users/me` | Protected | None | Retrieves the profile of the authenticated user based on the JWT subject. |
| `PUT` | `/api/v1/users/me` | Protected | `UserUpdateRequest` | Updates the authenticated user's profile details. |
| `DELETE` | `/api/v1/users/me` | Protected | None | Deactivates the authenticated user's account. |
| `GET` | `/api/v1/users/search` | Admin Only | None (`?keyword=&page=&size=`) | Searches all user profiles by keyword with full pagination. |

## 🏗️ Architecture Overview

This service does **not** handle user registration or password verification. It operates downstream from the Identity Provider (Auth Service).

1. Client authenticates with the Auth Service and receives an Access Token.
2. Client sends a request to the User Service with `Authorization: Bearer <token>`.
3. The User Service validates the cryptographic signature using the shared secret.
4. The service extracts the `username` and `roles` from the payload, building a stateless `SecurityContext`.
5. The REST Controller processes the business logic using the verified identity.

## ⚙️ Getting Started

1. **Clone the repository:**
   ```bash
   git clone <your-repo-url>