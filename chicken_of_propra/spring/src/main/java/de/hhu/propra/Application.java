package de.hhu.propra;

import de.hhu.propra.application.stereotypes.ApplicationService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

@SpringBootApplication
@ComponentScan(basePackages = {"de.hhu.propra"},
    includeFilters = @ComponentScan.Filter(type = FilterType.ANNOTATION,
        classes = ApplicationService.class)
)
public class Application {
  public static void main(String[] args) {
    SpringApplication.run(Application.class, args);
  }

}