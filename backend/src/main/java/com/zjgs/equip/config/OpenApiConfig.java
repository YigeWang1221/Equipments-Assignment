package com.zjgs.equip.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI equipOpenAPI() {
        return new OpenAPI().info(new Info()
                .title("器材借用与课程作业管理系统 API")
                .version("1.0.0")
                .description("Spring Boot 3 + MyBatis-Plus + MySQL"));
    }
}
