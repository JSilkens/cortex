package be.jsilkens.cortex.architecture;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

@AnalyzeClasses(packages = "be.jsilkens.cortex", importOptions = ImportOption.DoNotIncludeTests.class)
class HexagonalArchitectureTest {

    @ArchTest
    static final ArchRule domain_should_not_depend_on_spring =
        noClasses().that().resideInAnyPackage(
            "be.jsilkens.cortex.common.domain..",
            "be.jsilkens.cortex.domain..",
            "be.jsilkens.cortex.usecase.."
        ).should().dependOnClassesThat().resideInAnyPackage(
            "org.springframework.."
        );

    @ArchTest
    static final ArchRule adapters_should_access_domain_through_usecase =
        noClasses().that().resideInAnyPackage(
            "be.jsilkens.cortex.api..",
            "be.jsilkens.cortex.storage.."
        ).should().dependOnClassesThat().resideInAnyPackage(
            "be.jsilkens.cortex.domain.."
        );

    @ArchTest
    static final ArchRule domain_should_not_depend_on_adapters =
        noClasses().that().resideInAnyPackage(
            "be.jsilkens.cortex.domain..",
            "be.jsilkens.cortex.usecase.."
        ).should().dependOnClassesThat().resideInAnyPackage(
            "be.jsilkens.cortex.api..",
            "be.jsilkens.cortex.llm..",
            "be.jsilkens.cortex.storage..",
            "be.jsilkens.cortex.db.."
        );

    @ArchTest
    static final ArchRule db_adapter_should_not_depend_on_other_adapters =
        noClasses().that().resideInAnyPackage(
            "be.jsilkens.cortex.db.."
        ).should().dependOnClassesThat().resideInAnyPackage(
            "be.jsilkens.cortex.api..",
            "be.jsilkens.cortex.llm..",
            "be.jsilkens.cortex.storage.."
        );

    @ArchTest
    static final ArchRule llm_adapter_should_not_depend_on_other_adapters =
        noClasses().that().resideInAnyPackage(
            "be.jsilkens.cortex.llm.."
        ).should().dependOnClassesThat().resideInAnyPackage(
            "be.jsilkens.cortex.api..",
            "be.jsilkens.cortex.db..",
            "be.jsilkens.cortex.storage.."
        );
}
