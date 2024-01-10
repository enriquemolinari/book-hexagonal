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
    public void infraSecondaryAdapterShouldOnlyDependOnHexagon() {
        JavaClasses importedClasses = new ClassFileImporter().withImportOption(
                        new com.tngtech.archunit.core.importer.ImportOption.DoNotIncludeTests())
                .importPackages(ALL_PACKAGES);
        classes().that().resideInAPackage("infra.secondary..").should()
                .onlyDependOnClassesThat()
                .resideInAnyPackage("infra.secondary..", "hexagon..", "hexagon.secondary.port", "java..",
                        "javax..", "dev.paseto..", "jakarta.persistence..", "lombok..")
                .check(importedClasses);
    }
}
