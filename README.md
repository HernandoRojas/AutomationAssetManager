# 🚀 Asset Manager API

A robust Backend System designed for managing corporate hardware inventory, such as Laptops and Mobile Phones. This project demonstrates **Clean Architecture**, **Object-Oriented Design**, and a **"Test-First" QA Automation approach** using the modern Spring Boot ecosystem.

---

## 🏗️ Core Logic & Architecture
The system utilizes an **Inheritance-based Domain Model** to manage diverse hardware assets under a unified structure:
* **Base Entity:** `Device` (Contains common attributes: ID, Brand, Model, Status).
* **Specialized Entities:** `Laptop` (Specifics: RAM, Storage, OS) and `MobilePhone` (Specifics: Phone Number).
* **Business State Machine:** Managed through a `DeviceStatus` Enum (`AVAILABLE`, `IN_USE`, `UNDER_REPAIR`, `DECOMMISSIONED`), ensuring strict enforcement of business rules (e.g., preventing the rental of devices currently under maintenance).



## 🛠️ Technology Stack
* **Language:** Java 17
* **Framework:** Spring Boot 3.4.1
* **Build Tool:** Gradle
* **Persistence:** Spring Data JPA with **Hibernate** (Object-Relational Mapping).
* **Database:** **H2 In-Memory Database** — Selected for high-speed integration testing, zero-configuration portability, and ensuring a "clean slate" for automation suites.

---

## 🧪 Testing Strategy (QA Automation Focus)
This project emphasizes **Stateful Integration Testing**, verifying data integrity from the HTTP entry point down to the persistence layer.

### **API & Integration Testing**
* **Framework:** [RestAssured](https://rest-assured.io/)
* **Approach:** Hybrid "Black-Box" (API response) and "White-Box" (Database state) verification.
* **Automation Logic:** Tests trigger REST endpoints and subsequently utilize the `DeviceRepository` to verify the actual state in the H2 database.
* **Infrastructure:** A centralized `BaseApiTest` class manages the test lifecycle, utilizing `repository.deleteAll()` in the `@BeforeEach` phase to prevent **Test Pollution**.

### **Unit Testing**
* **Framework:** JUnit 5 & Mockito
* **Focus:** Isolated validation of core business logic within the `AssetService` and custom domain logic within the entities.



---

## 🚀 Key Features Implemented
* **Relational Mapping (US-005/006):** Advanced JPA mapping using `InheritanceType.JOINED` for normalized data storage.
* **Data Integrity (US-007):** Implementation of **Jakarta Bean Validation** and a **Global Exception Handler** to ensure professional JSON error responses (400 Bad Request / 404 Not Found).
* **Persistence Layer:** Transitioned from volatile memory to a structured SQL-based storage system.
* **H2 Console:** Integrated web interface for real-time manual data inspection during the development lifecycle.

---

## 🛠️ Installation & Setup Guide

### Prerequisites
* Java 17 (JDK)
* Gradle (No need to download Gradle, the project uses the Gradle wrapper)

Follow these steps to get the project running locally on your machine for development or testing.
### 1. Clone the Repository
Open your terminal and run the following command to clone the project:
```bash
git clone [https://github.com/HernandoRojas/AutomationAssetManager.git](https://github.com/HernandoRojas/AutomationAssetManager.git)
cd AutomationAssetManager
```

### 2. Build the project
Use the Gradle wrapper to clean and build the project. This will download all necessary dependencies (Spring Boot, Hibernate, RestAssured, etc.):
```bash
./gradlew clean build
```
Note: All the tests will run automatically.

### 3. Run the Application
Start the Spring Boot server locally:
```bash
./gradlew bootRun
```

The API will be available at: http://localhost:8080/api/assets

### 4. Run the Automated Test Suite
To execute the RestAssured integration tests and JUnit 5 unit tests:
```bash
./gradlew test
```
Note: To run the tests, you must stop the server.

Press CTRL + C (if you are on windows)

Type 'S' when the message '¿Desea terminar el trabajo por lotes (S/N)?' appears.

#### 4.1 Run the unit tests
To run only unit tests:
```bash
./gradlew test --tests "service.*"
```

#### 4.2 Run the API Integration tests
To run only the API integration tests:
```bash
./gradlew test --tests "com.assetmanager.api.*"
```

Note: After running tests, a detailed HTML report can be found at: build/reports/tests/test/index.html

### 5. Access the H2 Database Console
While the application is running, you can inspect the in-memory database directly:

1. Navigate to: http://localhost:8080/h2-console
2. JDBC URL: jdbc:h2:mem:assetdb
3. User: sa
4. Password: password
5. Click Connect to run SQL queries against the active data.