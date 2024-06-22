package com.dynamicwebservice.config;

import lombok.extern.slf4j.Slf4j;
import org.h2.tools.Server;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;

import java.sql.SQLException;

@Slf4j
public class H2ServerInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    @Override
    public void initialize(ConfigurableApplicationContext context) {
        try {
            Server.createTcpServer("-tcp", "-tcpAllowOthers", "-tcpPort", "9092", "-ifNotExists").start();
            log.info("H2 TCP server started on port 9092");
        } catch (SQLException e) {
            throw new RuntimeException("Failed to start H2 TCP server", e);
        }
    }
}
