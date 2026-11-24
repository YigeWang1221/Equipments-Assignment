package com.zjgs.equip;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@ConfigurationPropertiesScan
@MapperScan("com.zjgs.equip.**.mapper")
@SpringBootApplication
public class EquipApplication {
    public static void main(String[] args) {
        SpringApplication.run(EquipApplication.class, args);
    }
}
