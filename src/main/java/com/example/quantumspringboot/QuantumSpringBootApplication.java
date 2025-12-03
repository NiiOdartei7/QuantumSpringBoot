package com.example.quantumspringboot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class QuantumSpringBootApplication {

    public static void main(String[] args) {
        System.setProperty("RUST_LOG", "debug,cosmian_kms=debug,cosmian_sdk=debug");

        SpringApplication.run(QuantumSpringBootApplication.class, args);
    }

}
