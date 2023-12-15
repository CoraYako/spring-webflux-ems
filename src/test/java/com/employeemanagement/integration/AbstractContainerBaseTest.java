package com.employeemanagement.integration;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;

import static java.time.format.SignStyle.ALWAYS;

public abstract class AbstractContainerBaseTest {
    static final MongoDBContainer MONGO_DB_CONTAINER;

    static {
        MONGO_DB_CONTAINER = new MongoDBContainer("mongo:latest");
        MONGO_DB_CONTAINER.start();
    }

    @DynamicPropertySource
    public static void dynamicPropertySource(DynamicPropertyRegistry registry) {
        registry.add("spring.output.ansi.enabled", ALWAYS::toString);
        registry.add("spring.data.mongodb.host", MONGO_DB_CONTAINER::getHost);
        registry.add("spring.data.mongodb.port", MONGO_DB_CONTAINER::getFirstMappedPort);
        registry.add("spring.data.mongodb.database", () -> "employeetest");
        registry.add("spring.data.mongodb.authentication-database", () -> "admin");
        registry.add("spring.data.mongodb.auto-index-creation", () -> "true");
        registry.add("spring.data.mongodb.auto-create-indexes", () -> "true");
        registry.add("spring.data.mongodb.initialize", () -> "true");
    }
}
