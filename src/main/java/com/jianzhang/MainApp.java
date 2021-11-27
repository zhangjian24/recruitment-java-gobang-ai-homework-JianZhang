package com.jianzhang;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;

@SpringBootApplication
public class MainApp implements ApplicationListener<ApplicationReadyEvent> {

    private static final Logger LOGGER = LoggerFactory.getLogger(MainApp.class);

    @Value("${server.port}")
    Integer port;

    public static void main(String[] args) {
        SpringApplication.run(MainApp.class);
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        LOGGER.info("===============================================");
        LOGGER.info("visit server with：http://localhost:{}", port);
        LOGGER.info("===============================================");
    }
}
