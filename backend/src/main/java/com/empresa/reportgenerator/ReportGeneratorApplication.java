package com.empresa.reportgenerator;

import com.empresa.reportgenerator.entity.User;
import com.empresa.reportgenerator.entity.enums.UserRole;
import com.empresa.reportgenerator.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
@EnableCaching
@EnableRetry
@EnableAsync
public class ReportGeneratorApplication {

    public static void main(String[] args) {
        SpringApplication.run(ReportGeneratorApplication.class, args);
    }

    /**
     * Cria o usuário admin na inicialização se não existir.
     * Remove esse método após o primeiro uso em produção!
     */
    @Bean
    public CommandLineRunner initData(UserRepository userRepository,
                                      PasswordEncoder passwordEncoder) {
        return args -> {
            if (userRepository.findByUsername("admin").isEmpty()) {
                User admin = User.builder()
                        .username("admin")
                        .passwordHash(passwordEncoder.encode("admin123"))
                        .email("admin@empresa.com")
                        .role(UserRole.ADMIN)
                        .active(true)
                        .build();
                userRepository.save(admin);
                System.out.println("✅ Usuário admin criado! Senha: admin123");
            } else {
                System.out.println("ℹ️ Usuário admin já existe.");
            }
        };
    }
}
