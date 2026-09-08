# ArchUnit 規則範本

供 `springboot-codegen` 產出 `src/test/java/{package}/architecture/ArchitectureTest.java` 時參考，
供 `code-review` Skill Phase 1 執行的結構規則清單。

```java
package com.example.lifepremium.architecture;

import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.*;
import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;

class ArchitectureTest {

    private static com.tngtech.archunit.core.domain.JavaClasses classes;

    @BeforeAll
    static void setup() {
        classes = new ClassFileImporter()
                .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
                .importPackages("com.example.lifepremium");
    }

    @Test
    void controllers_should_not_depend_on_repositories() {
        ArchRule rule = noClasses().that().resideInAPackage("..controller..")
                .should().dependOnClassesThat().resideInAPackage("..repository..");
        rule.check(classes);
    }

    @Test
    void services_should_only_depend_on_allowed_packages() {
        ArchRule rule = classes().that().resideInAPackage("..service..")
                .should().onlyDependOnClassesThat()
                .resideInAnyPackage("..service..", "..repository..", "..domain..",
                        "..dto..", "..mapper..", "..exception..", "java..", "javax..",
                        "jakarta..", "org.springframework..");
        rule.check(classes);
    }

    @Test
    void domain_should_not_depend_on_upper_layers() {
        ArchRule rule = noClasses().that().resideInAPackage("..domain..")
                .should().dependOnClassesThat().resideInAnyPackage(
                        "..controller..", "..service..", "..repository..");
        rule.check(classes);
    }

    @Test
    void no_cyclic_dependencies_between_packages() {
        ArchRule rule = slices().matching("com.example.lifepremium.(*)..")
                .should().beFreeOfCycles();
        rule.check(classes);
    }

    @Test
    void repository_classes_should_be_named_correctly() {
        ArchRule rule = classes().that().resideInAPackage("..repository..")
                .should().haveSimpleNameEndingWith("Repository");
        rule.check(classes);
    }

    @Test
    void service_impl_classes_should_be_named_correctly() {
        ArchRule rule = classes().that().resideInAPackage("..service.impl..")
                .should().haveSimpleNameEndingWith("ServiceImpl");
        rule.check(classes);
    }

    @Test
    void controller_classes_should_be_named_correctly() {
        ArchRule rule = classes().that().resideInAPackage("..controller..")
                .should().haveSimpleNameEndingWith("Controller");
        rule.check(classes);
    }
}
```

## 規則清單摘要

| # | 規則 | 對應命名/開發標準章節 |
|---|------|----------------------|
| 1 | `controller` 不得依賴 `repository` | 分層依賴規則 |
| 2 | `service` 只能依賴允許清單內的 package | 分層依賴規則 |
| 3 | `domain` 不得依賴 `controller`/`service`/`repository` | 分層依賴規則 |
| 4 | package 間無循環依賴 | 分層依賴規則 |
| 5 | Repository 類別命名以 `Repository` 結尾 | 命名規範 |
| 6 | Service 實作類別命名以 `ServiceImpl` 結尾 | 命名規範 |
| 7 | Controller 類別命名以 `Controller` 結尾 | 命名規範 |
