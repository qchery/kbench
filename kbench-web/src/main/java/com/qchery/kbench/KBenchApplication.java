package com.qchery.kbench;

import com.qchery.kbench.server.controller.KieServerControllerApplication;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

@SpringBootApplication
@ServletComponentScan(basePackages = "com.qchery")
public class KBenchApplication {

    public static void main(String[] args) {
        SpringApplication.run(new Class[]{
                KBenchApplication.class, KieServerControllerApplication.class
        }, args);
    }

}
