package com.empresa.reportgenerator.config;

import com.empresa.reportgenerator.security.CustomUserDetailsService;
import com.empresa.reportgenerator.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * Configuração central de segurança da aplicação.
 * Define regras de autenticação, autorização, CORS e integração JWT.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    /**
     * Configura a cadeia de filtros de segurança.
     * Define quais endpoints são públicos e quais precisam de autenticação.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 1. Desabilita CSRF (não necessário para APIs REST stateless)
                .csrf(AbstractHttpConfigurer::disable)

                // 2. Configura CORS
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // 3. Define regras de autorização por endpoint
                .authorizeHttpRequests(auth -> auth
                        // Endpoints públicos (não precisam de token)
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers(
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/v3/api-docs/**",
                                "/v3/api-docs",
                                "/swagger-resources/**",
                                "/webjars/**"
                        ).permitAll()

                        // Endpoints que precisam de autenticação (USER ou ADMIN)
                        .requestMatchers("/api/reports/**").hasAnyRole("USER", "ADMIN")
                        .requestMatchers("/api/templates").hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/templates/**").hasAnyRole("USER", "ADMIN")

                        // Endpoints exclusivos para ADMIN
                        .requestMatchers(HttpMethod.POST, "/api/templates/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/templates/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/templates/**").hasRole("ADMIN")
                        .requestMatchers("/api/audit-logs/**").hasRole("ADMIN")

                        // Qualquer outra requisição precisa de autenticação
                        .anyRequest().authenticated()
                )

                // 4. Configura sessão como STATELESS (sem sessões no servidor)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // 5. Configura o AuthenticationProvider
                .authenticationProvider(authenticationProvider())

                // 6. Adiciona o JwtAuthenticationFilter ANTES do filtro padrão do Spring
                .addFilterBefore(jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * Configura o PasswordEncoder com BCrypt (work factor 12).
     * Usado para hashear e verificar senhas.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    /**
     * Configura o AuthenticationProvider.
     * Conecta o UserDetailsService com o PasswordEncoder.
     */
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    /**
     * Expõe o AuthenticationManager como Bean.
     * Usado pelo AuthenticationService para autenticar usuários.
     */
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * Configura CORS para permitir requisições do frontend.
     * Permite origem http://localhost:5173 (Vite dev server).
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // Origens permitidas (frontend)
        configuration.setAllowedOrigins(List.of("http://localhost:5173"));

        // Métodos HTTP permitidos
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));

        // Headers permitidos
        configuration.setAllowedHeaders(List.of("*"));

        // Permite envio de credenciais (cookies, Authorization header)
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
