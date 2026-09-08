package com.example.lifepremium.bdd.steps;

import com.example.lifepremium.domain.Product;
import com.example.lifepremium.domain.RateEntry;
import com.example.lifepremium.domain.RateTableVersion;
import com.example.lifepremium.dto.request.PremiumCalculateRequest;
import com.example.lifepremium.dto.response.PremiumCalculateResponse;
import com.example.lifepremium.exception.BusinessException;
import com.example.lifepremium.repository.ProductRepository;
import com.example.lifepremium.repository.RateEntryRepository;
import com.example.lifepremium.repository.RateTableVersionRepository;
import com.example.lifepremium.service.PremiumCalculationService;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

// FR-PREMIUM-001：壽險保費試算
@CucumberContextConfiguration
@SpringBootTest
@Transactional
public class PremiumCalculationSteps {

    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private RateTableVersionRepository rateTableVersionRepository;
    @Autowired
    private RateEntryRepository rateEntryRepository;
    @Autowired
    private PremiumCalculationService premiumCalculationService;

    private Product product;
    private RateTableVersion rateTableVersion;

    private int age;
    private String gender;
    private long insuredAmount;
    private int paymentPeriod;

    private PremiumCalculateResponse response;
    private BusinessException thrownException;

    @Given("商品 {string} 已建立費率表版本 {string}")
    public void 商品已建立費率表版本(String productCode, String versionNo) {
        product = productRepository.findByProductCode(productCode)
                .orElseGet(() -> productRepository.save(new Product(productCode, "終身壽險")));
        rateTableVersion = rateTableVersionRepository.findByProductAndVersionNo(product, versionNo)
                .orElseGet(() -> rateTableVersionRepository.save(
                        new RateTableVersion(product, versionNo, LocalDate.now())));
    }

    @Given("費率表包含以下費率")
    public void 費率表包含以下費率(DataTable dataTable) {
        List<Map<String, String>> rows = dataTable.asMaps();
        for (Map<String, String> row : rows) {
            int rowAge = Integer.parseInt(row.get("年齡"));
            String rowGender = row.get("性別");
            int rowPaymentPeriod = Integer.parseInt(row.get("繳費年期"));
            BigDecimal rate = new BigDecimal(row.get("費率"));

            boolean exists = rateEntryRepository.existsByRateTableVersionAndAgeAndGenderAndPaymentPeriod(
                    rateTableVersion, rowAge, rowGender, rowPaymentPeriod);
            if (!exists) {
                rateEntryRepository.save(new RateEntry(rateTableVersion, rowAge, rowGender, rowPaymentPeriod, rate));
            }
        }
    }

    @Given("被保人年齡為 {int} 歲")
    public void 被保人年齡為(int age) {
        this.age = age;
    }

    @Given("被保人性別為 {string}")
    public void 被保人性別為(String gender) {
        this.gender = gender;
    }

    @Given("保額為 {long} 萬元")
    public void 保額為萬元(long amountInTenThousand) {
        this.insuredAmount = amountInTenThousand * 10_000L;
    }

    @Given("繳費年期為 {int} 年")
    public void 繳費年期為(int paymentPeriod) {
        this.paymentPeriod = paymentPeriod;
    }

    @When("系統試算保費")
    public void 系統試算保費() {
        try {
            PremiumCalculateRequest request = new PremiumCalculateRequest(
                    product.getProductCode(), age, gender, insuredAmount, paymentPeriod);
            this.response = premiumCalculationService.calculate(request, null);
            this.thrownException = null;
        } catch (BusinessException ex) {
            this.thrownException = ex;
            this.response = null;
        }
    }

    @Then("年繳保費應為 {long} 元")
    public void 年繳保費應為(long expected) {
        assertThat(thrownException).isNull();
        assertThat(response.annualPremium()).isEqualTo(expected);
    }

    @Then("月繳保費應為 {long} 元")
    public void 月繳保費應為(long expected) {
        assertThat(thrownException).isNull();
        assertThat(response.monthlyPremium()).isEqualTo(expected);
    }

    @Then("系統應回傳錯誤 {string}")
    public void 系統應回傳錯誤(String errorCode) {
        assertThat(thrownException).isNotNull();
        assertThat(thrownException.getErrorCode().name()).isEqualTo(errorCode);
    }
}
