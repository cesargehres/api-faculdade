package com.example.tarefas;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EnableJpaRepositories("com.example.tarefas.repository")
@EntityScan("com.example.tarefas.model")
public class TarefasApplication {

    public static void main(String[] args) {
        SpringApplication.run(TarefasApplication.class, args);
    }
}
