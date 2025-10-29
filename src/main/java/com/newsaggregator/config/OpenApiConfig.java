package com.newsaggregator.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    
    @Bean
    public OpenAPI newsAggregatorOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("News Aggregator API")
                        .description("Enterprise News Aggregator microservice that aggregates news from Guardian and NY Times APIs")
                        .version("v1.0.0")
                        .contact(new Contact()
                                .name("News Aggregator Team")
                                .email("support@newsaggregator.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0")));
    }
}