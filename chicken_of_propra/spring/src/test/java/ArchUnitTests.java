import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.library.GeneralCodingRules;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.library.Architectures.layeredArchitecture;
import static com.tngtech.archunit.library.plantuml.PlantUmlArchCondition.Configurations.consideringOnlyDependenciesInDiagram;
import static com.tngtech.archunit.library.plantuml.PlantUmlArchCondition.adhereToPlantUmlDiagram;

@AnalyzeClasses(importOptions = ImportOption.DoNotIncludeTests.class)
public class ArchUnitTests {

    @ArchTest
    ArchRule keinAutowired = GeneralCodingRules.NO_CLASSES_SHOULD_USE_FIELD_INJECTION;

    @ArchTest
    ArchRule layerTest = layeredArchitecture()
            .layer("Web").definedBy("..web..")
            .layer("Database").definedBy("..database..")
            .layer("Application").definedBy("..application..")
            .layer("Domain").definedBy("..domain..")
            .whereLayer("Web").mayNotBeAccessedByAnyLayer()
            .whereLayer("Database").mayNotBeAccessedByAnyLayer()
            .whereLayer("Domain").mayOnlyBeAccessedByLayers("Application")
            .whereLayer("Application").mayOnlyBeAccessedByLayers("Domain");

    @ArchTest
    ArchRule checkUML = classes()
            .should(adhereToPlantUmlDiagram(getClass().getResource("Architektur.puml"),
                    consideringOnlyDependenciesInDiagram()));


}
