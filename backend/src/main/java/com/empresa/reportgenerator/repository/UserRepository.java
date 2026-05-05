package com.empresa.reportgenerator.repository;

import com.empresa.reportgenerator.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositório para acesso aos dados de usuários.
 * Spring Data JPA gera automaticamente as implementações dos métodos.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    /**
     * Busca um usuário pelo username.
     * Usado na autenticação (login).
     * 
     * Query gerada: SELECT * FROM users WHERE username = ?
     */
    Optional<User> findByUsername(String username);
    
    /**
     * Verifica se existe um usuário com o username informado.
     * Usado para validar se o username já está em uso ao criar usuário.
     * 
     * Query gerada: SELECT COUNT(*) > 0 FROM users WHERE username = ?
     */
    boolean existsByUsername(String username);
    
    /**
     * Verifica se existe um usuário com o email informado.
     * Usado para validar se o email já está em uso ao criar usuário.
     * 
     * Query gerada: SELECT COUNT(*) > 0 FROM users WHERE email = ?
     */
    boolean existsByEmail(String email);
}
