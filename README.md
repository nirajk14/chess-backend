# Spring Boot Chess Backend

A Spring Boot Application for Chess Backend

---

## Tech Stack

- **Java 17+**
- **Spring Boot 3.x**
- **Spring Data JPA**
- **PostgreSQL** (or any relational database)
- **Lombok** (`@Data`, `@Builder`, etc.)
- **JJWT** for JWT tokens
- **Gradle** for build automation

---

## Getting Started

### Prerequisites

- Java 17 or later
- Gradle 8.x or later
- PostgreSQL (or your preferred DB)
- OpenSSL (optional, for generating JWT secret key)

---

### 1. Clone the Repository

```bash
git clone https://github.com/yourusername/project-name.git
cd project-name
```

---

### 2. Create `.env` File

In the root of your project, create a file named `.env` and add the following variables:

```env
JWT_SECRET=your-generated-super-long-secret-key
```

> ⚠️ **Important:** Make sure `.env` is in your `.gitignore` so the secret key is never committed to source control.

---

### 3. Run Database Migrations

Ensure your database is running. Create the necessary schema if not done automatically.  
Example for PostgreSQL:

```sql
CREATE DATABASE chessdb;
```

---

### 4. Build and Run

Using Gradle:

```bash
./gradlew bootRun
```

The application should start on **http://localhost:8080/**.

---

### 5. API Endpoints

#### Signup

```
POST /api/v1/signup
Content-Type: application/json

Body:
{
    "username": "yourusername",
    "password": "yourpassword",
    "confirmPassword": "yourpassword"
}
```

Response:

```json
{
  "success": true,
  "message": "User registered successfully",
  "data": {
    "id": 1,
    "username": "yourusername"
  }
}
```

#### Login

```
POST /api/v1/login
Content-Type: application/json

Body:
{
    "username": "yourusername",
    "password": "yourpassword"
}
```

Response Header:

```
Authorization: Bearer <JWT_TOKEN>
```

Response Body:

```json
{
  "success": true,
  "message": "Login successful",
  "data": {
    "username": "yourusername",
    "token": "<JWT_TOKEN>"
  }
}
```

--



