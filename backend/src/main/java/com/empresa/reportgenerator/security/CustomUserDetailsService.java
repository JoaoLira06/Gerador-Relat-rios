package com.empresa.reportgenerator.security;

import com.empresa.reportgenerator.entity.User;
import com.empresa.reportgenerator.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Serviço customizado para carregar usuários do banco de dados.
 * Implementa a interface UserDetailsService do Spring Security.
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * Carrega o usuário pelo username.
     * Chamado automaticamente pelo Spring Security durante a autenticação.
     *
     * @param username Username do usuário
     * @return UserDetails com informações do usuário
     * @throws UsernameNotFoundException se o usuário não for encontrado
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 1. Busca o usuário no banco de dados
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "Usuário não encontrado: " + username
                ));

        // 2. Converte a role do usuário para o formato do Spring Security
        // O Spring Security espera roles com prefixo "ROLE_"
        String roleWithPrefix = "ROLE_" + user.getRole().name();
        List<SimpleGrantedAuthority> authorities = List.of(
                new SimpleGrantedAuthority(roleWithPrefix)
        );

        // 3. Retorna um UserDetails do Spring Security
        // Spring Security usa isso para validar a senha e as roles
        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPasswordHash())  // Hash BCrypt
                .authorities(authorities)
                .accountExpired(false)
                .accountLocked(!user.isActive())   // Bloqueia se inativo
                .credentialsExpired(false)
                .disabled(!user.isActive())        // Desabilita se inativo
                .build();
    }
}
