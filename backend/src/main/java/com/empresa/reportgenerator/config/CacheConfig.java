package com.empresa.reportgenerator.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * Configuração do cache em memória usando Caffeine.
 * Usado para cachear templates pré-definidos.
 */
@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager("templates");
        cacheManager.setCaffeine(
                Caffeine.newBuilder()
                        .maximumSize(100)           // máximo 100 entradas
                        .expireAfterWrite(10, TimeUnit.MINUTES) // expira após 10 minutos
        );
        return cacheManager;
    }
}
