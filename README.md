# Referral Tracking System

A Spring Boot application for tracking user referrals. Users can sign up with or without a referral code, and a referral is only marked successful when the referred user completes their profile. The system also provides endpoints for generating referral reports in CSV format and includes integrated Swagger/OpenAPI documentation.

---

## Table of Contents

- [Features](#features)
- [Technologies Used](#technologies-used)
- [Installation and Setup](#installation-and-setup)
- [Configuration](#configuration)
- [API Endpoints](#api-endpoints)
  - [Signup API](#signup-api)
  - [Profile Completion API](#profile-completion-api)
  - [Get Referrals API](#get-referrals-api)
  - [Referral Report API](#referral-report-api)
- [Swagger Documentation](#swagger-documentation)
- [Testing](#testing)
- [Custom Banner](#custom-banner)
- [Contributing](#contributing)
- [License](#license)

---

## Features

- **User Signup with Referral Tracking**  
  - Allows users to sign up with or without providing a referral code.
  - Automatically generates a unique referral code for every user.
  - If a valid referral code is provided, the corresponding referrer is linked and a referral record is created (initial status _PENDING_).

- **Profile Completion**  
  - Users complete their profiles by submitting additional details such as mobile number, gender, and address.
  - Upon profile completion, the referral record status is updated to _SUCCESSFUL_.

- **Get Referrals**  
  - Fetch a list of referrals for a given referrer using a simplified response format (via DTOs).

- **CSV Report Generation**  
  - Generate a downloadable CSV report containing all users and their referral data.
  - Report includes user details, a comma-separated list of referred users, and the total count of referrals.

- **Swagger/OpenAPI Documentation**  
  - Integrated API documentation with Swagger UI.
  - Custom ordering of endpoints ensures that the signup endpoint appears above the profile completion endpoint.

- **Custom Startup Banner**  
  - Displays an ASCII art banner at application startup for a personalized look.

---

## Technologies Used

- **Java 17**
- **Spring Boot 3.4.4**
- **Spring Data JPA**
- **PostgreSQL**
- **Apache Commons CSV**
- **springdoc-openapi** for Swagger integration
- **Maven** as the build tool

---

## Installation and Setup

### 1. Clone the Repository

```bash
git clone https://https://github.com/Shivam-2310/referral-tracking-system.git
cd referral-tracking-system
```

### 2. Configure the Database

Edit the `src/main/resources/application.properties` file with your PostgreSQL credentials:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/referral_db
spring.datasource.username=postgres
spring.datasource.password=root
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
```

### 3. Build the Application

Run Maven to build the project:

```bash
mvn clean install
```

### 4. Run the Application

Start the application using Maven:

```bash
mvn spring-boot:run
```

Alternatively, you can run it from your favorite IDE.

---

## Configuration

### Swagger/OpenAPI Configuration

The application uses **springdoc-openapi** to automatically generate API documentation. Swagger UI is available at:

```
http://localhost:8080/swagger-ui/index.html
```

Custom ordering of endpoints is implemented using an `OpenApiCustomiser` bean (see configuration section) to ensure the signup endpoint appears above the profile completion endpoint.

---

## API Endpoints

### Signup API

- **Endpoint:** `POST /api/signup`
- **Description:** Registers a new user with the basic details.  
  If a referral code is provided, it validates the code, links the referrer, and creates a referral record (initially marked as _PENDING_).
- **Request Payload Example:**

  ```json
  {
    "name": "John Doe",
    "email": "john.doe@example.com",
    "password": "password123",
    "referralCode": ""  // Optional: provide a valid referral code if available
  }
  ```

- **Response:**  
  Returns the created user object with details like `id`, `name`, `email`, `referralCode`, and `profileCompleted`.

---

### Profile Completion API

- **Endpoint:** `POST /api/users/{userId}/complete-profile`
- **Description:** Completes the user's profile by updating additional information.  
  This endpoint also updates the referral record status to _SUCCESSFUL_ if applicable.
- **Request Payload Example:**

  ```json
  {
    "mobileNumber": "1234567890",
    "gender": "MALE",
    "address": "123 MAIN ST, CITY"
  }
  ```

- **Response:**  
  Returns the updated user object with the profile marked as completed.

---

### Get Referrals API

- **Endpoint:** `GET /api/referrals/{userId}`
- **Description:** Retrieves a list of referrals for the specified user (acting as the referrer) using a simplified DTO response.
- **Response Example:**

  ```json
  [
    {
      "referralId": 100,
      "referrerId": 1,
      "referrerName": "John Doe",
      "referredId": 2,
      "referredName": "Alice Smith",
      "status": "SUCCESSFUL"
    },
    {
      "referralId": 101,
      "referrerId": 1,
      "referrerName": "John Doe",
      "referredId": 3,
      "referredName": "Bob Johnson",
      "status": "PENDING"
    }
  ]
  ```

---

### Referral Report API

- **Endpoint:** `GET /api/referral-report`
- **Description:** Generates and returns a CSV report summarizing all users and their referral data.
- **CSV Report Columns:**
    - **User ID**
    - **Name**
    - **Email**
    - **Referral Code**
    - **Referred Users** (a comma-separated list of names)
    - **Referred Users Count**
- **Response:**  
  A downloadable CSV file.

---

## Swagger Documentation

After starting the application, navigate to:

```
http://localhost:8080/swagger-ui/index.html
```

You can view and interact with all the API endpoints directly from Swagger UI.

---

## Testing

### Unit Tests

- Unit tests cover both the service and controller layers.
- To run the tests, execute:

  ```bash
  mvn clean test
  ```

### Integration Tests

- Integration tests ensure that the complete workflow (signup, profile completion, referral fetching, and CSV report generation) works as expected.

---

## Custom Banner

A custom ASCII art banner is displayed during application startup. You can modify the banner by editing the `src/main/resources/banner.txt` file.

