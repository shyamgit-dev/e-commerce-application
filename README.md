# 🛒 E-Commerce Application — Monolithic Backend

A production-style **E-Commerce Backend Application** built using **Java, Spring Boot, Spring Security, JPA/Hibernate, MySQL, and Razorpay**.

The application implements complete e-commerce workflows including authentication, product management, cart, checkout, payments, refunds, orders, reviews & ratings, coupons, notifications, and email notifications.

This project was initially developed as a **monolithic Spring Boot application** with a focus on clean architecture, RESTful APIs, authentication, transactional business logic, and real-world backend workflows.

> 🚀 **Next Phase:** The monolithic application will be used as the foundation for designing and implementing a **Microservices-based E-Commerce Architecture**.

---

## 📌 Features

### 🔐 Authentication & Authorization

* User registration and login
* JWT-based authentication
* Spring Security
* Role-based authorization
* Protected REST APIs
* Authenticated-user validation
* User-specific resource access

---

### 👤 User & Address Management

* User profile management
* Multiple addresses per user
* Add, update and delete addresses
* User-specific address validation during checkout

---

### 📦 Product Management

* Create, update and delete products
* Product listing
* Product details
* Product stock management
* Pagination
* Product availability validation

---

### 🛒 Cart Management

* Add products to cart
* Update cart item quantity
* Remove cart items
* Calculate cart subtotal
* Validate available stock
* Clear cart after successful payment

---

### 💳 Checkout & Order Management

Complete checkout workflow:

```text
Cart
 ↓
Checkout
 ↓
Address Validation
 ↓
Stock Validation
 ↓
Coupon Validation
 ↓
Order Creation
 ↓
Payment
```

Order lifecycle:

```text
CREATED
   ↓
PAYMENT_PENDING
   ↓
CONFIRMED
   ↓
SHIPPED
   ↓
DELIVERED
```

Supported order operations:

* Create order
* View orders
* View order details
* Cancel order
* Update order status
* Restore product stock after cancellation
* Order ownership validation

---

### 💰 Razorpay Payment Integration

Integrated **Razorpay** for online payments.

Implemented:

* Razorpay order creation
* Payment ID handling
* Razorpay signature verification
* Payment status management
* Payment-to-order association
* Payment failure handling
* Payment success handling
* Payment verification
* Transaction-safe payment workflow

Payment flow:

```text
Create Order
     ↓
Create Razorpay Order
     ↓
Customer Completes Payment
     ↓
Receive Payment ID + Signature
     ↓
Verify Razorpay Signature
     ↓
Update Payment
     ↓
Confirm Order
     ↓
Reduce Inventory
     ↓
Clear Cart
```

---

### 🔄 Payment Refunds

Implemented Razorpay refund integration.

Supported:

* Full refunds
* Partial refunds
* Razorpay refund ID tracking
* Refund status tracking
* Refund timestamp
* Payment refund validation

Refund workflow:

```text
Order Cancellation
       ↓
Check Payment Status
       ↓
Payment Successful?
       ↓
Create Razorpay Refund
       ↓
Store Refund Details
       ↓
Cancel Order
       ↓
Restore Inventory
```

---

### ⭐ Reviews & Ratings

Implemented product review and rating functionality.

Features:

* Authenticated users can review products
* Users must have purchased and received the product
* One review per user per product
* Existing review can be updated
* Rating validation
* Automatic product average rating calculation
* Public review retrieval

Example relationship:

```text
User 1 ─────────── N Review
Product 1 ──────── N Review
```

---

### 🎟️ Coupon Management

Implemented coupon-based discounts.

Supported:

* Fixed discounts
* Percentage discounts
* Minimum order amount
* Maximum discount limit
* Coupon expiry
* Active/inactive coupons
* Global usage limit
* Per-user usage limit
* Coupon usage tracking
* Prevent duplicate coupon usage

Coupon workflow:

```text
Checkout
   ↓
Coupon Code
   ↓
Validate Coupon
   ├── Active?
   ├── Expired?
   ├── Usage Limit?
   ├── User Usage Limit?
   └── Minimum Order Amount?
          ↓
       Apply Discount
```

---

### 🔔 In-App Notifications

Implemented user-specific notification management.

Features:

* Create notifications
* Payment success notifications
* Read/unread status
* Mark individual notification as read
* Mark all notifications as read
* Delete notifications
* Pagination
* Sorting by creation date
* Filter by notification status
* User-specific notification retrieval

Example:

```text
Payment Successful
        ↓
Create In-App Notification
        ↓
Status = UNREAD
```

---

### 📧 Email Notifications

Implemented email notification functionality using Spring Mail.

Current implementation supports transactional emails such as:

* Payment successful email

Example workflow:

```text
Payment Verification
        ↓
Payment SUCCESS
        ↓
Create In-App Notification
        ↓
Send Email Notification
```

Email sending can be executed asynchronously using Spring's `@Async` support.

---

## 🏗️ Architecture

The current application follows a layered monolithic architecture.

```text
                Client
                  │
                  ▼
             REST Controller
                  │
                  ▼
             Service Layer
                  │
          ┌───────┴───────┐
          ▼               ▼
      Repository       External APIs
          │               │
          ▼               ▼
        MySQL          Razorpay
```

### Application Layers

```text
Controller
    ↓
Service
    ↓
Repository
    ↓
Entity
    ↓
Database
```

Additional components:

```text
DTO
Exception Handling
Security
Validation
External API Integration
Notification Service
Email Service
```

---

## 🧩 Major Entities

The application contains entities representing the core e-commerce domain:

```text
User
 │
 ├── Address
 ├── Cart
 │     └── CartItem
 │             └── Product
 │
 ├── Order
 │     ├── OrderItem
 │     ├── Payment
 │     ├── Coupon
 │     └── CouponUsage
 │
 ├── Review
 │
 └── Notification
```

---

## 🔒 Security

Security is implemented using:

* Spring Security
* JWT
* Bearer Token authentication
* Role-based authorization
* Authenticated user extraction
* Resource ownership validation

Example:

```http
Authorization: Bearer <JWT_TOKEN>
```

User-specific resources are validated using the authenticated user's identity.

---

## ⚠️ Global Exception Handling

Implemented centralized exception handling using:

```text
@RestControllerAdvice
```

Handled scenarios include:

* Resource not found
* Invalid actions
* Access denied
* Insufficient stock
* Invalid payment state
* Razorpay exceptions
* Validation errors
* Authentication/authorization errors

This provides consistent API error responses instead of exposing raw exceptions.

---

## 🗄️ Database

### Database

**MySQL**

### ORM

**Spring Data JPA / Hibernate**

Relationships implemented include:

```text
One-to-One
One-to-Many
Many-to-One
```

Database operations include:

* Derived JPA queries
* JPQL queries
* Pagination
* Sorting
* Filtering
* Aggregation queries

---

## 🛠️ Technology Stack

| Technology      | Usage                          |
| --------------- | ------------------------------ |
| Java            | Backend development            |
| Spring Boot     | Application framework          |
| Spring MVC      | REST APIs                      |
| Spring Security | Authentication & Authorization |
| JWT             | Token-based authentication     |
| Spring Data JPA | Database access                |
| Hibernate       | ORM                            |
| MySQL           | Relational database            |
| Razorpay        | Payment processing             |
| Spring Mail     | Email notifications            |
| Maven           | Dependency management          |
| Lombok          | Boilerplate reduction          |
| ModelMapper     | DTO mapping                    |
| JUnit           | Testing                        |
| Mockito         | Mocking                        |
| Git             | Version control                |
| Postman         | API testing                    |
| Swagger/OpenAPI | API documentation              |

---

## 📊 Pagination, Sorting & Filtering

Pagination and sorting are implemented for collection-based APIs.

Example:

```http
GET /notifications?page=0&size=10
```

Sorting:

```http
GET /notifications?page=0&size=10&sort=createdAt,desc
```

Filtering:

```http
GET /notifications?status=UNREAD
```

Combined:

```http
GET /notifications?page=0&size=10&status=UNREAD
```

---

## 🔄 Transaction Management

Business-critical operations use transaction management through:

```java
@Transactional
```

Examples include:

* Checkout
* Payment verification
* Order cancellation
* Refund processing
* Coupon usage
* Notification creation

This helps maintain consistency when multiple database operations are involved in a single business workflow.

---

## 🚀 Getting Started

### Prerequisites

Make sure the following are installed:

* Java 17+
* Maven
* MySQL
* Git

---

### 1. Clone the repository

```bash
git clone <YOUR_GITHUB_REPOSITORY_URL>
```

```bash
cd <PROJECT_DIRECTORY>
```

---

### 2. Configure MySQL

Create a MySQL database:

```sql
CREATE DATABASE ecommerce;
```

Update your application configuration:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/ecommerce
spring.datasource.username=YOUR_USERNAME
spring.datasource.password=YOUR_PASSWORD
```

---

### 3. Configure JWT

Add your JWT secret:

```properties
jwt.secret=YOUR_JWT_SECRET
```

---

### 4. Configure Razorpay

Add your Razorpay credentials:

```properties
razorpay.key=YOUR_RAZORPAY_KEY
razorpay.secret=YOUR_RAZORPAY_SECRET
```

For development, use Razorpay Test Mode credentials.

---

### 5. Configure Email

Configure your SMTP credentials:

```properties
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=YOUR_EMAIL
spring.mail.password=YOUR_APP_PASSWORD

spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
```

> Never commit passwords, API keys, JWT secrets, or other credentials to GitHub.

---

### 6. Run the application

Using Maven:

```bash
mvn spring-boot:run
```

Or run the main Spring Boot application class from your IDE.

---

## 🧪 API Testing

The APIs can be tested using:

* Postman
* Swagger/OpenAPI

Recommended workflow:

```text
Register
  ↓
Login
  ↓
Receive JWT
  ↓
Authorize API requests
  ↓
Browse Products
  ↓
Add Product to Cart
  ↓
Checkout
  ↓
Apply Coupon
  ↓
Create Payment
  ↓
Verify Payment
  ↓
Order Confirmed
  ↓
Receive Notification
  ↓
Receive Email
```

---

## 🔮 Future Architecture — Microservices

The monolithic application serves as the foundation for the next phase of the project.

The planned architecture is:

```text
                     API Gateway
                          │
              ┌───────────┴───────────┐
              │                       │
              ▼                       ▼
        User Service            Product Service
              │                       │
              └───────────┬───────────┘
                          │
                    Order Service
                          │
              ┌───────────┼───────────┐
              ▼           ▼           ▼
        Payment       Inventory   Notification
        Service        Service       Service
              │
              ▼
           Razorpay
```

Planned technologies:

* Spring Cloud
* Eureka Service Discovery
* Spring Cloud Gateway
* OpenFeign
* Resilience4j
* Apache Kafka
* Redis
* Docker
* Docker Compose
* Centralized Configuration
* Centralized Logging
* Monitoring & Observability

---

## 🎯 Learning Objectives

This project was built to gain practical experience with:

* REST API development
* Spring Boot application architecture
* Spring Security
* JWT authentication
* JPA/Hibernate relationships
* Transaction management
* Payment gateway integration
* External API integration
* Exception handling
* Pagination and filtering
* Business rule implementation
* Database design
* Notification systems
* Email integration
* Backend system design

---

## 👨‍💻 Author

**Shyam Pandey**

Java Backend Developer | Spring Boot | REST APIs | Microservices

---

## ⭐ Project Status

### Monolithic Version

**Completed ✅**

### Next Phase

**Microservices Architecture 🚀**
