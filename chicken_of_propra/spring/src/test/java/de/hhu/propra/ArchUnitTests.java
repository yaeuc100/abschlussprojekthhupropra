package de.hhu.propra;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.library.GeneralCodingRules;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.library.Architectures.layeredArchitecture;
import static com.tngtech.archunit.library.Architectures.onionArchitecture;
import static com.tngtech.archunit.library.plantuml.PlantUmlArchCondition.Configurations.consideringOnlyDependenciesInDiagram;
import static com.tngtech.archunit.library.plantuml.PlantUmlArchCondition.adhereToPlantUmlDiagram;

@AnalyzeClasses(importOptions = ImportOption.DoNotIncludeTests.class, packages = "de.hhu.propra")
public class ArchUnitTests {


  @ArchTest
  ArchRule keinAutowired = GeneralCodingRules.NO_CLASSES_SHOULD_USE_FIELD_INJECTION;

  @ArchTest
  ArchRule onionTest = onionArchitecture()
      .domainModels("..domain..")
      .applicationServices("..application..")
      .adapter("Web", "..web..")
      .adapter("Database", "..database..")
      .adapter("Spring", "..spring..")
      .allowEmptyShould(true);


  @ArchTest
  ArchRule pruefeUML = classes()
      .should(adhereToPlantUmlDiagram(getClass().getResource("/Architektur.puml"),
          consideringOnlyDependenciesInDiagram()));


}
