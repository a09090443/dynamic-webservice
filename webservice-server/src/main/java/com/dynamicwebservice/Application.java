package com.dynamicwebservice;

import com.dynamicwebservice.config.H2ServerInitializer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(Application.class);
        app.addInitializers(new H2ServerInitializer());
        app.run(args);
    }

}
