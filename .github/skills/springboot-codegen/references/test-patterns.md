# 測試範本（test-patterns）

## §1 Cucumber Step Definitions 範本

```java
package com.example.{projectcode}.bdd.steps;

import io.cucumber.java.en.*;
import org.springframework.beans.factory.annotation.Autowired;

// FR: FR-{MODULE}-001
public class {Module}Steps {

    @Autowired
    private {Business}Service service;

    @Given("被保人年齡為 {int} 歲")
    public void 被保人年齡為(int age) {
        throw new io.cucumber.java.PendingException();
    }

    @When("系統試算保費")
    public void 系統試算保費() {
        throw new io.cucumber.java.PendingException();
    }

    @Then("年繳保費應為 {long} 元")
    public void 年繳保費應為(long expected) {
        throw new io.cucumber.java.PendingException();
    }
}
```

```java
package com.example.{projectcode}.bdd;

import io.cucumber.junit.platform.engine.Constants;
import org.junit.platform.suite.api.*;

@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features")
@ConfigurationParameter(key = Constants.GLUE_PROPERTY_NAME, value = "com.example.{projectcode}.bdd.steps")
public class CucumberTestRunner {}
```

## §2 Controller Integration Test 範本

```java
package com.example.{projectcode}.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class {Resource}ControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void should_return_200_when_calculate_success() throws Exception {
        mockMvc.perform(post("/api/v1/premium/calculate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {"productCode":"LIFE-WL-01","age":35,"gender":"M",
                             "insuredAmount":1000,"paymentPeriod":20}"""))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("SUCCESS"));
    }

    @Test
    void should_return_400_when_age_out_of_range() throws Exception {
        mockMvc.perform(post("/api/v1/premium/calculate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {"productCode":"LIFE-WL-01","age":99,"gender":"M",
                             "insuredAmount":1000,"paymentPeriod":20}"""))
                .andExpect(status().isBadRequest());
    }
}
```

## §3 Service Unit Test 範本

```java
package com.example.{projectcode}.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PremiumCalculationServiceTest {

    @Mock
    private RateEntryRepository rateEntryRepository;

    @InjectMocks
    private PremiumCalculationServiceImpl service;

    // FR-PREMIUM-001 正常流程：BR-004/BR-005 計算規則
    @Test
    void calculate_should_return_correct_annual_and_monthly_premium() {
        // Given: 費率 12.50/千元，保額 1000 萬，20 年期
        // When: service.calculate(request)
        // Then: 年繳 125000、月繳 10729（BR-005 四捨五入）
    }

    // FR-PREMIUM-001 例外情境：BR-001 年齡邊界
    @Test
    void calculate_should_throw_AgeOutOfRangeException_when_age_over_70() {
        assertThatThrownBy(() -> { /* service.calculate(invalidRequest) */ })
                .isInstanceOf(AgeOutOfRangeException.class);
    }
}
```

## §4 Repository Test 範本

```java
package com.example.{projectcode}.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class RateEntryRepositoryTest {

    @Autowired
    private RateEntryRepository repository;

    @Test
    void findByProductCodeAndAgeAndGender_should_return_rate_when_exists() {
        // Given: 測試資料已透過 @Sql 或 TestEntityManager 準備
        // When / Then
        assertThat(repository.findAll()).isNotNull();
    }
}
```
