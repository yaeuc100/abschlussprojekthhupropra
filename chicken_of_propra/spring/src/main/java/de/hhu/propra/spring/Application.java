package de.hhu.propra.spring;

import de.hhu.propra.application.stereotypes.ApplicationService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import static org.springframework.context.annotation.FilterType.ANNOTATION;

@SpringBootApplication()
@ComponentScan(
        includeFilters = {
                @ComponentScan.Filter(type = ANNOTATION, classes = ApplicationService.class)
        }
)
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}
