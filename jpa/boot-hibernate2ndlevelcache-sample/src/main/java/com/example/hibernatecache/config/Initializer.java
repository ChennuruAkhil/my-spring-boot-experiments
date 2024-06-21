package com.example.hibernatecache.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
class Initializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(Initializer.class);

    private final ApplicationProperties properties;

    Initializer(ApplicationProperties properties) {
        this.properties = properties;
    }

    @Override
    public void run(String... args) {
        log.info("Running Initializer.....");
    }
}
