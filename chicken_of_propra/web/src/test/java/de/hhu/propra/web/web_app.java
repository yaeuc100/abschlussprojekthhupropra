package de.hhu.propra.web;

import de.hhu.propra.application.stereotypes.ApplicationService;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;


@SpringBootApplication()
@ComponentScan(basePackages = {"de.hhu.propra"}
        ,includeFilters = @ComponentScan.Filter(type = FilterType.ANNOTATION, classes = {ApplicationService.class}))
public class web_app { }
