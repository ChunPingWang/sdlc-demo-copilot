package com.example.lifepremium.architecture;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;

// code-review Skill Phase 1：ArchUnit 結構規則（分層依賴、命名慣例、循環依賴）
class ArchitectureTest {

    private static JavaClasses classes;

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
