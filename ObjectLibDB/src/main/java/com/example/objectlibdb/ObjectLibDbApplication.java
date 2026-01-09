package com.example.objectlibdb;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@SpringBootApplication
@MapperScan("com.example.objectlibdb.mapper")
//@ComponentScan(basePackages = {"com.example.objectlibdb"})
public class ObjectLibDbApplication {

    public static void main(String[] args) {
        SpringApplication.run(ObjectLibDbApplication.class, args);
    }

}
