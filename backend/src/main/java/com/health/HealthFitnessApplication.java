package com.health;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.health.mapper")
public class HealthFitnessApplication {
    public static void main(String[] args) {
        SpringApplication.run(HealthFitnessApplication.class, args);
        System.out.println("========================================");
        System.out.println("  个人健康与运动数据中心 后端启动成功!");
        System.out.println("  访问地址: http://localhost:8081/api");
        System.out.println("========================================");
    }
}
