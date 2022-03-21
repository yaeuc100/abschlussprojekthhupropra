package de.hhu.propra.database;

import de.hhu.propra.application.stereotypes.ApplicationService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jdbc.repository.config.EnableJdbcRepositories;
import org.springframework.stereotype.Repository;

import static org.springframework.context.annotation.FilterType.ANNOTATION;
@SpringBootApplication()
public class Db_app {
}
