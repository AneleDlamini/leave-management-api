# Leave Management REST API

A production-style REST API built with Java and Spring Boot for managing employee leave requests and approval workflows. The application enables employees to submit and track vacation requests while allowing managers to review, approve, or reject requests through secure, role-based endpoints.

This project demonstrates REST API development, layered application architecture, business rule validation, role-based security, automated testing, and API documentation.

---

## Features

### Employee Functionality

- Register and manage employee records
- View remaining annual leave balance
- Submit vacation requests
- View all personal vacation requests
- Filter requests by status (Pending / Approved)

### Manager Functionality

- View all vacation requests
- Review individual vacation requests
- Approve or reject pending requests

### Business Rules

- Prevent employees from exceeding their annual leave allocation
- Prevent duplicate or invalid requests
- Validate employee existence before processing requests
- Validate request status updates

### Security

- Role-based access control using Spring Security
- Protected endpoints for Employees and Managers
- Authentication and authorization for API access

### Testing

- Comprehensive unit tests covering:
  - Employee management
  - Leave balance calculations
  - Vacation request creation
  - Request approval workflow
  - Validation and exception handling
  - Business rules

### API Documentation

- Interactive API documentation using Swagger/OpenAPI

---

# Technology Stack

| Technology | Purpose |
|------------|---------|
| Java 17 | Programming Language |
| Spring Boot | Backend Framework |
| Spring Security | Authentication & Role-based Authorization |
| Spring Data JPA | Data Persistence |
| Maven | Dependency Management & Build Tool |
| Swagger / OpenAPI | API Documentation |
| JUnit 5 | Unit Testing |
| H2 Database | Database |

---

# Architecture

The project follows a layered architecture to promote separation of concerns and maintainability.

```
Controller
     │
     ▼
 Service
     │
     ▼
Repository
     │
     ▼
 Database
```

The application separates:

- Controllers – Handle HTTP requests and responses.
- Services – Contain business logic and validation.
- Repositories – Manage data persistence.
- Entities – Represent domain models.
- Security – Handles authentication and authorization.

---

# API Overview

## Employee Endpoints

| Method | Endpoint | Description |
|---------|----------|-------------|
| GET | `/workers` | Retrieve all workers |
| GET | `/workers/{email}` | Retrieve worker by email |
| GET | `/workers/{id}/vacation-balance` | View remaining leave balance |
| POST | `/requests` | Submit a vacation request |
| GET | `/requests/worker/{id}` | View employee requests |
| GET | `/requests/status/{status}` | Filter requests by status |

## Manager Endpoints

| Method | Endpoint | Description |
|---------|----------|-------------|
| GET | `/requests` | View all vacation requests |
| PUT | `/requests/{id}/status` | Approve or reject a request |
| GET | `/requests/{id}` | Retrieve a specific request |

*(Endpoints may differ slightly depending on your implementation.)*

---

# Running the Application

## Prerequisites

- Java 17+
- Maven 3.8+

## Clone the Repository

```bash
git clone https://github.com/<your-username>/leave-management-api.git
```

## Build

```bash
./mvnw clean install
```

## Run

```bash
./mvnw spring-boot:run
```

The application will start on:

```
http://localhost:8080
```

---

# API Documentation

Swagger UI is available at:

```
http://localhost:8080/swagger-ui/index.html
```

Swagger provides interactive documentation for exploring and testing all available API endpoints.

---

# Running Tests

Execute all unit tests using:

```bash
./mvnw test
```

---

# Future Improvements

Potential enhancements include:

- Detect overlapping employee leave requests
- JWT authentication
- Refresh token support
- Docker containerisation
- PostgreSQL production configuration
- Integration tests
- CI/CD with GitHub Actions
- Manager dashboard and analytics
- Email notifications for request status changes

---

# What I Learned

This project strengthened my understanding of:

- Designing RESTful APIs with Spring Boot
- Implementing layered software architecture
- Applying business rules and validation
- Role-based security using Spring Security
- Writing maintainable unit tests
- Documenting APIs with Swagger/OpenAPI
- Building production-style backend applications
