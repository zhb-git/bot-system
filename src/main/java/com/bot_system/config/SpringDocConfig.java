package com.bot_system.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.servers.Server;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@OpenAPIDefinition(
        servers = {
                @Server(description = "开发环境api", url = "http://localhost:8080")
        }
)
@Configuration
public class SpringDocConfig {
    @Bean
    public OpenAPI openApi() {
        return new OpenAPI()
                .info(this.getApiInfo());
    }
    private Info getApiInfo() {
        return new Info()
                .title("机器人系统")
                .version("v1.0.0")
                .contact(new Contact().name("Java之父").email("Java之父@163.com"))
                .license(new License().name("Apache 2.0").url("https://springdoc.org"));
    }
}
