package architecture;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;

public class VerifyArchitectureTest {

    public static final String[] ALL_PACKAGES = {"hexagon..", "infra..", "spring..", "javalin.."};

    @Test
    public void hexagonShouldOnlyDependOnHexagon() {
        JavaClasses importedClasses = new ClassFileImporter().withImportOption(
                        new com.tngtech.archunit.core.importer.ImportOption.DoNotIncludeTests())
                .importPackages(ALL_PACKAGES);
        classes().that().resideInAPackage("hexagon").should()
                .onlyDependOnClassesThat()
                .resideInAnyPackage("hexagon..", "java..", "javax..",
                        "lombok..")
                .check(importedClasses);
    }

    @Test
    public void infraPrimaryAdapterShouldOnlyDependOnPrimaryPort() {
        JavaClasses importedClasses = new ClassFileImporter().withImportOption(
                        new com.tngtech.archunit.core.importer.ImportOption.DoNotIncludeTests())
                .importPackages(ALL_PACKAGES);
        classes().that().resideInAPackage("infra.primary..").should()
                .onlyDependOnClassesThat()
                .resideInAnyPackage("infra.primary..", "hexagon.primary.port", "java..",
                        "javax..", "org.springframework..", "io.javalin..", "jakarta.servlet..")
                .check(importedClasses);
    }

    @Test
    public void infraSecondaryAdapterJpaShouldOnlyDependOnSecondaryPortAndHexagon() {
        JavaClasses importedClasses = new ClassFileImporter().withImportOption(
                        new com.tngtech.archunit.core.importer.ImportOption.DoNotIncludeTests())
                .importPackages(ALL_PACKAGES);
        classes().that().resideInAPackage("infra.secondary.jpa")
                .should().onlyDependOnClassesThat()
                .resideInAnyPackage("infra.secondary.jpa..", "hexagon..", "hexagon.secondary.port",
                        "java..", "javax..", "dev.paseto..", "jakarta.persistence..", "lombok..")
                .check(importedClasses);
    }

    @Test
    public void infraSecondaryAdapterInMemoryShouldOnlyDependOnSecondaryPortAndHexagon() {
        JavaClasses importedClasses = new ClassFileImporter().withImportOption(
                        new com.tngtech.archunit.core.importer.ImportOption.DoNotIncludeTests())
                .importPackages(ALL_PACKAGES);
        classes().that().resideInAPackage("infra.secondary.inmemory")
                .should().onlyDependOnClassesThat()
                .resideInAnyPackage("infra.secondary.inmemory", "hexagon..", "hexagon.secondary.port",
                        "java..", "javax..", "dev.paseto..", "jakarta.persistence..", "lombok..")
                .check(importedClasses);
    }

    @Test
    public void infraSecondaryAdapterPaymentShouldOnlyDependOnSecondaryPort() {
        JavaClasses importedClasses = new ClassFileImporter().withImportOption(
                        new com.tngtech.archunit.core.importer.ImportOption.DoNotIncludeTests())
                .importPackages(ALL_PACKAGES);
        classes().that().resideInAPackage("infra.secondary.payment").should()
                .onlyDependOnClassesThat()
                .resideInAnyPackage("infra.secondary.payment", "hexagon.secondary.port", "java..",
                        "javax..")
                .check(importedClasses);
    }

    @Test
    public void infraSecondaryAdapterInMemoryShouldOnlyDependOnSecondaryPort() {
        JavaClasses importedClasses = new ClassFileImporter().withImportOption(
                        new com.tngtech.archunit.core.importer.ImportOption.DoNotIncludeTests())
                .importPackages(ALL_PACKAGES);
        classes().that().resideInAPackage("infra.secondary.inmemory").should()
                .onlyDependOnClassesThat()
                .resideInAnyPackage("infra.secondary.inmemory", "hexagon..", "java..",
                        "javax..")
                .check(importedClasses);
    }

    @Test
    public void infraSecondaryAdapterEmailNotificationShouldOnlyDependOnSecondaryPort() {
        JavaClasses importedClasses = new ClassFileImporter().withImportOption(
                        new com.tngtech.archunit.core.importer.ImportOption.DoNotIncludeTests())
                .importPackages(ALL_PACKAGES);
        classes().that().resideInAPackage("infra.secondary.mail").should()
                .onlyDependOnClassesThat()
                .resideInAnyPackage("infra.secondary.mail", "hexagon.secondary.port", "java..",
                        "javax..")
                .check(importedClasses);
    }

    @Test
    public void infraSecondaryAdapterTokenShouldOnlyDependOnSecondaryPort() {
        JavaClasses importedClasses = new ClassFileImporter().withImportOption(
                        new com.tngtech.archunit.core.importer.ImportOption.DoNotIncludeTests())
                .importPackages(ALL_PACKAGES);
        classes().that().resideInAPackage("infra.secondary.token").should()
                .onlyDependOnClassesThat()
                .resideInAnyPackage("infra.secondary.token", "hexagon.secondary.port", "java..",
                        "javax..", "dev.paseto..")
                .check(importedClasses);
    }
}
