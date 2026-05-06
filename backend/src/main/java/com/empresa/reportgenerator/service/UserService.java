package com.empresa.reportgenerator.service;

import com.empresa.reportgenerator.entity.User;
import com.empresa.reportgenerator.entity.enums.UserRole;
import com.empresa.reportgenerator.repository.UserRepository;
import jdk.jshell.spi.ExecutionControl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public User createUser(String username, String password, String email, UserRole role) {
        if(userRepository.existsByUsername(username)){
            throw new IllegalArgumentException("Usuário já está sendp usado: "+ username);
        }
        if(userRepository.existsByEmail(email)){
            throw new IllegalArgumentException("Email já em uso: " + email);
        }
        User user = User.builder()
                .username(username)
                .email(email)
                .passwordHash(passwordEncoder.encode(password))
                .role(role)
                .active(true)
                .build();

        return userRepository.save(user);
    }

    public User updateUser(Long id, String password, String email, UserRole role){
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado: " + id));

            if(email != null & !email.equals(user.getEmail())){
                if (userRepository.existsByEmail(email)) {
                    throw new IllegalArgumentException("Email já está em uso: " + email);
                }
                user.setEmail(email);
            }

        if (password != null) {
            // Hasheia a nova senha antes de salvar
            user.setPasswordHash(passwordEncoder.encode(password));
        }

        if (role != null) {
            user.setRole(role);
        }

        return userRepository.save(user);
    }
    public void deactivateUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado: " + id));

        // Marca como inativo — o CustomUserDetailsService vai bloquear o login
        user.setActive(false);
        userRepository.save(user);
    }

    /**
     * Reativa um usuário desativado.
     *
     * @param id ID do usuário a ser reativado
     */
    @Transactional
    public void activateUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado: " + id));

        user.setActive(true);
        userRepository.save(user);
    }

    /**
     * Busca um usuário pelo username.
     *
     * @param username Username do usuário
     * @return Usuário encontrado
     */
    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado: " + username));
    }

    /**
     * Lista todos os usuários.
     *
     * @return Lista de usuários
     */
    public List<User> listUsers() {
        return userRepository.findAll();
    }

}




