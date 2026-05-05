package com.empresa.reportgenerator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Ponto de entrada da aplicação Spring Boot.
 *
 * As anotações aqui ativam funcionalidades globais:
 * - @SpringBootApplication: combina @Configuration + @EnableAutoConfiguration + @ComponentScan
 * - @EnableCaching: ativa o cache em memória (usado para templates pré-definidos)
 * - @EnableRetry: ativa o mecanismo de retry (usado no AuditService para operações de banco)
 * - @EnableAsync: permite métodos assíncronos com @Async (usado na geração de relatórios grandes)
 */
@SpringBootApplication
@EnableCaching
@EnableRetry
@EnableAsync
public class ReportGeneratorApplication {

    public static void main(String[] args) {
        SpringApplication.run(ReportGeneratorApplication.class, args);
    }
}
