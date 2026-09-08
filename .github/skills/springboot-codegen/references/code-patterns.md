# 程式碼範本（code-patterns）

## §1 Entity 範本

```java
package com.example.{projectcode}.domain;

import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "{table_name}")
public class {Entity} {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    // ...業務欄位...

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    protected {Entity}() {
        // JPA 需要無參建構子
    }

    // getters / 建構子 / equals+hashCode（以 id 為準）
}
```

## §2 Repository 範本

```java
package com.example.{projectcode}.repository;

import com.example.{projectcode}.domain.{Entity};
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface {Entity}Repository extends JpaRepository<{Entity}, UUID> {
    Optional<{Entity}> findBy{Field}(String {field});
}
```

## §3 DTO 範本（Java Record）

```java
package com.example.{projectcode}.dto.request;

import jakarta.validation.constraints.*;

public record {Resource}{Action}Request(
        @NotBlank String productCode,
        @Min(0) @Max(70) int age,
        @Pattern(regexp = "M|F") String gender,
        @Min(100) @Max(50_000_000) long insuredAmount,
        int paymentPeriod
) {}
```

```java
package com.example.{projectcode}.dto.response;

public record ApiResponse<T>(String code, String message, T data, String timestamp) {
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>("SUCCESS", null, data, java.time.OffsetDateTime.now().toString());
    }
}
```

## §4 Exception 範本

```java
package com.example.{projectcode}.exception;

public abstract class BusinessException extends RuntimeException {
    private final ErrorCode errorCode;
    protected BusinessException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
    public ErrorCode getErrorCode() { return errorCode; }
}
```

```java
package com.example.{projectcode}.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import com.example.{projectcode}.dto.response.ApiResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Void>> handleBusiness(BusinessException ex) {
        HttpStatus status = ex.getErrorCode().httpStatus();
        return ResponseEntity.status(status)
                .body(new ApiResponse<>(ex.getErrorCode().name(), ex.getMessage(), null,
                        java.time.OffsetDateTime.now().toString()));
    }
}
```

## §5/§6 Service 與 Mapper 範本

```java
package com.example.{projectcode}.service;

public interface {Business}Service {
    {Response} calculate({Request} request);
}
```

```java
package com.example.{projectcode}.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class {Business}ServiceImpl implements {Business}Service {

    private final {Entity}Repository repository;

    public {Business}ServiceImpl({Entity}Repository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional(readOnly = true)
    public {Response} calculate({Request} request) {
        // 業務規則驗證 + 計算邏輯
        return null;
    }
}
```

## §7 Controller 範本

```java
package com.example.{projectcode}.controller;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import com.example.{projectcode}.dto.request.*;
import com.example.{projectcode}.dto.response.*;

@RestController
@RequestMapping("/api/v1/{resources}")
public class {Resource}Controller {

    private final {Business}Service service;

    public {Resource}Controller({Business}Service service) {
        this.service = service;
    }

    @PostMapping("/calculate")
    public ApiResponse<{Response}> calculate(@Valid @RequestBody {Request} request) {
        return ApiResponse.success(service.calculate(request));
    }
}
```
