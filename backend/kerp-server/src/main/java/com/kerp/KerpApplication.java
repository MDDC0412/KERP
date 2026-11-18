package com.kerp;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan("com.kerp.modules.**.mapper")
@SpringBootApplication
public class KerpApplication {

    public static void main(String[] args) {
        SpringApplication.run(KerpApplication.class, args);
        System.out.println("启动成功");
    }
}

