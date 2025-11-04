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
git clone https://github.com/nirajk14/chess-backend.git
cd chess-backend
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

## 🎮 Matchmaking API (v1)

### Base URL
`/api/v1/matchmaking`

### 🔹 1. Join Matchmaking Queue
**Endpoint:** `POST /api/v1/matchmaking/join`  
**Description:** Adds the authenticated user to the matchmaking queue.  
**Headers:**  
| Key | Value | Required |  
|-----|--------|-----------|  
| Authorization | Bearer <JWT_TOKEN> | ✅ |  
**Response:**
```json
{
  "success": true,
  "message": "Joined matchmaking queue",
  "data": {
    "userId": 4,
    "username": "player1",
    "joinedAt": "2025-11-04T16:30:30.725"
  }
}
```  
**Failure Response:**
```json
{
  "success": false,
  "message": "Missing or invalid token"
}
```

### 🔹 2. Subscribe to Matchmaking Events (SSE)
**Endpoint:** `GET /api/v1/matchmaking/queue/{userId}/sse`  
**Description:** Opens a Server-Sent Events (SSE) stream for the given user. The server pushes real-time matchmaking status updates (e.g., searching, match_found).  
**Produces:** `text/event-stream`  
**Example Initial Event:**
```
event: searching
data: {"status":"searching"}
```  
**Example Match Found Event:**
```
event: match_found
data: {"opponentId":5,"opponentUsername":"player2","matchId":101}
```  
**Notes:** The connection times out after 10 minutes (600,000 ms). The client should auto-reconnect if disconnected unexpectedly.

### 🧪 Testing Tips
**Join Queue with cURL:**
```
curl -X POST http://localhost:8080/api/v1/matchmaking/join -H "Authorization: Bearer <JWT_TOKEN>"
```  
**Connect to SSE Stream:**
```
curl http://localhost:8080/api/v1/matchmaking/queue/<USER_ID>/sse
```  
You should receive:
```
event: searching
data: {"status":"searching"}
```  
and later:
```
event: match_found
data: {"opponentId":5,"opponentUsername":"player2","matchId":101}
```




