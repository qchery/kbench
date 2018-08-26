package com.qchery.kbench;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"org.guvnor", "com.qchery.kbench"})
@ServletComponentScan(basePackages = "com.qchery")
public class KBenchApplication {

    public static void main(String[] args) {
        SpringApplication.run(KBenchApplication.class, args);
    }

}
