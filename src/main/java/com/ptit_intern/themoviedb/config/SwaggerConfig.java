package com.ptit_intern.themoviedb.config;

import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.OpenAPI;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("MovieDB API")
                        .version("1.0.0")
                        .description("API documentation for MovieDB project")
                        .contact(new Contact().name("PTIT Intern").email("bxt203@gmail.com"))
                        .license(new License().name("Apache 2.0").url("http://springdoc.org")));
    }
}
