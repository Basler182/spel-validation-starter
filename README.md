# SpEL Validation Starter 🚀

[![Java](https://img.shields.io/badge/Java-25%2B-orange)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.x-green)](https://spring.io/projects/spring-boot)

**Stop writing custom `ConstraintValidator` classes for every logical check.**  
This library allows you to validate complex relationships between fields directly in your DTOs using the **Spring
Expression Language (SpEL)**.

## ✨ Features

* **Cross-Field Validation:** Compare fields easily (e.g., `startDate < endDate`).
* **Spring Bean Access:** Call Services/Repositories in your validation logic (e.g., `@userService.exists(email)`).
* **Java Records:** Full support for modern Java Records.
* **Targeted Errors:** Attach errors to specific fields for better UI feedback.

## 📦 Installation

Add the dependency to your project:

### Maven

```xml

<dependency>
    <groupId>com.example</groupId>
    <artifactId>spel-validation-starter</artifactId>
    <version>1.0.0</version>
</dependency>
```

## 🛠 Usage Examples

### 1. Simple Cross-Field Validation

Ensure logic between two fields.

```java
@SpelAssert(
        value = "checkIn.isBefore(checkOut)",
        message = "Check-out must be after Check-in",
        applyTo = "checkOut" // Highlights the 'checkOut' field in the frontend
)
public record BookingRequest(LocalDate checkIn, LocalDate checkOut) {
}
```

### 2. Conditional Validation

Require a field only if another field has a specific value.

```java
@SpelAssert(
        value = "!isVip || (discountCode != null && discountCode.length() > 0)",
        message = "VIPs must provide a discount code",
        applyTo = "discountCode"
)
public class OrderDTO {
    private boolean isVip;
    private String discountCode;
}
```

### 3. Advanced: Database Checks (Bean Access)

Invoke a Spring Bean directly. Perfect for uniqueness checks.

```java
@SpelAssert(
        value = "@userService.isEmailUnique(email)",
        message = "Email is already taken",
        applyTo = "email"
)
public class RegistrationRequest {
    private String email;
}
```

### 4. Method Parameter Validation (Cross-Parameter)

Validate relationships between method arguments. You can use the **parameter names** directly in your SpEL expression (e.g., `#startDateTime`).

> **Note:** The `#args` array (e.g., `#args[1]`) is also still supported for legacy compatibility.

```java
public class BookingService {

    @SpelAssert(
            value = "#startDateTime.isBefore(#endDateTime) && #startDateTime.plusDays(7).isAfter(#endDateTime)",
            message = "Date range must be valid and less than 7 days",
            applyTo = "startDateTime"
    )
    public void getMeterValues(
            Long id, 
            ZonedDateTime startDateTime,
            ZonedDateTime endDateTime
    ) {
        // ...
    }
}
```