# Expense Tracker API

A RESTful backend API for personal finance management built with Spring Boot 3.2.

## Tech Stack

| Component | Technology |
|-----------|-----------|
| Framework | Spring Boot 3.2.0 |
| Language | Java 17 |
| Database (Dev) | H2 (in-memory) |
| Database (Prod) | PostgreSQL |
| ORM | Spring Data JPA / Hibernate |
| Validation | Jakarta Bean Validation |
| Build Tool | Maven |
| API Docs | SpringDoc OpenAPI (Swagger UI) |

## Project Structure

```
src/main/java/com/example/expense/
├── controller/         # HTTP request handlers
├── service/            # Business logic
├── repository/         # Database operations
├── model/              # JPA entities
├── dto/                # Request/Response objects
└── exception/          # Custom exceptions & global handler
```

## API Endpoints

### Categories
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/categories` | Create a category |
| GET | `/api/categories` | Get all categories |
| GET | `/api/categories/{id}` | Get category by ID |
| PUT | `/api/categories/{id}` | Update category |
| DELETE | `/api/categories/{id}` | Delete category |

### Transactions
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/transactions` | Create a transaction |
| GET | `/api/transactions` | Get all transactions (supports `?startDate=`, `?endDate=`, `?categoryId=`, `?type=`) |
| GET | `/api/transactions/{id}` | Get transaction by ID |
| PUT | `/api/transactions/{id}` | Update transaction |
| DELETE | `/api/transactions/{id}` | Delete transaction |

### Summary
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/summary/monthly?year=2026&month=4` | Monthly income, expenses & net balance |
| GET | `/api/summary/category?year=2026&month=4` | Spending breakdown by category |

### Budgets
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/budgets` | Set budget for a category/month |
| GET | `/api/budgets/alerts` | Get budget status alerts (OK / WARNING / EXCEEDED) |

---

## Getting Started

### Prerequisites
- Java 17+
- Maven 3.6+

### Run in Development Mode (H2 in-memory database)

```bash
./mvnw spring-boot:run
```

App starts at `http://localhost:8080`

### Run Tests

```bash
./mvnw test
```

### Run in Production Mode (PostgreSQL)

Set environment variables:
```bash
export DB_USERNAME=your_username
export DB_PASSWORD=your_password
```

```bash
./mvnw clean package
java -jar target/expense-tracker-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod
```

---

## How to Test the API

### Option 1 — Swagger UI (Recommended, no extra tools needed)

1. Start the app: `./mvnw spring-boot:run`
2. Open your browser and go to: **http://localhost:8080/swagger-ui.html**
3. All endpoints are listed — click any one, hit **Try it out**, fill in the body, and click **Execute**

### Option 2 — H2 Console (Inspect the database)

1. Start the app in dev mode
2. Open: **http://localhost:8080/h2-console**
3. Use these credentials:
   - JDBC URL: `jdbc:h2:mem:expensedb`
   - Username: `sa`
   - Password: *(leave blank)*
4. Run SQL queries to verify your data

### Option 3 — Postman

1. Download [Postman](https://www.postman.com/downloads/) and start the app
2. Try these requests in order:

**Step 1 — Create a category**
```
POST http://localhost:8080/api/categories
Content-Type: application/json

{
  "name": "Food",
  "type": "EXPENSE"
}
```

**Step 2 — Create another category (income)**
```
POST http://localhost:8080/api/categories
Content-Type: application/json

{
  "name": "Salary",
  "type": "INCOME"
}
```

**Step 3 — Create a transaction**
```
POST http://localhost:8080/api/transactions
Content-Type: application/json

{
  "amount": 150.00,
  "description": "Lunch at restaurant",
  "transactionDate": "2026-04-26",
  "categoryId": 1
}
```

**Step 4 — Get monthly summary**
```
GET http://localhost:8080/api/summary/monthly?year=2026&month=4
```

**Step 5 — Set a budget**
```
POST http://localhost:8080/api/budgets
Content-Type: application/json

{
  "categoryId": 1,
  "amount": 500.00,
  "month": "2026-04"
}
```

**Step 6 — Check budget alerts**
```
GET http://localhost:8080/api/budgets/alerts?year=2026&month=4
```

### Option 4 — Unit & Integration Tests

Run all tests with:
```bash
./mvnw test
```

Covers:
- `CategoryServiceTest` — CRUD logic, not-found exceptions
- `TransactionServiceTest` — create, find, delete, filters
- `BudgetServiceTest` — alert thresholds (OK / WARNING / EXCEEDED)
- `CategoryControllerTest` — HTTP status codes, validation errors
- `TransactionControllerTest` — HTTP status codes, validation errors
