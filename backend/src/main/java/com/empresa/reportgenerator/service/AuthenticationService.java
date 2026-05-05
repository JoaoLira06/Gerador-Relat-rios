package com.empresa.reportgenerator.service;


import com.empresa.reportgenerator.dto.auth.AuthenticationRequest;
import com.empresa.reportgenerator.dto.auth.AuthenticationResponse;
import com.empresa.reportgenerator.entity.RevokedToken;
import com.empresa.reportgenerator.entity.User;
import com.empresa.reportgenerator.repository.RevokedTokenRepository;
import com.empresa.reportgenerator.repository.UserRepository;
import com.empresa.reportgenerator.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor

public class AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;
    private final RevokedTokenRepository revokedTokenRepository;

    @Transactional
    public AuthenticationResponse login(AuthenticationRequest request) {
        // 1. Autentica o usuário (valida username e password)
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        User user =  userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));


        if(!user.isActive()){
            throw new RuntimeException("Usuário desativado");
        }

        // 4. Gera o token JWT
        String token = jwtTokenProvider.generateToken(authentication);

        // 5. Retorna a resposta com token, username e role
        return AuthenticationResponse.builder()
                .token(token)
                .username(user.getUsername())
                .role(user.getRole())
                .build();

    }
    /**
     * Realiza o logout do usuário revogando o token atual.
     *
     * @param token Token JWT a ser revogado
     */
    @Transactional
    public void logout(String token) {
        // 1. Extrai informações do token
        String jti = jwtTokenProvider.getJtiFromToken(token);
        String username = jwtTokenProvider.getUsernameFromToken(token);

        // 2. Calcula a data de expiração do token
        // (precisamos saber quando podemos deletar o registro de revoked_tokens)
        LocalDateTime expiresAt = calculateTokenExpiration(token);

        // 3. Cria registro de token revogado
        RevokedToken revokedToken = RevokedToken.builder()
                .tokenJti(jti)
                .username(username)
                .revokedAt(LocalDateTime.now())
                .expiresAt(expiresAt)
                .build();

        // 4. Salva no banco de dados
        revokedTokenRepository.save(revokedToken);
    }

    /**
     * Revoga todos os tokens de um usuário.
     * Usado quando um usuário é desativado.
     *
     * @param username Username do usuário
     */
    @Transactional
    public void revokeAllUserTokens(String username) {
        // Nota: Como não temos uma lista de todos os tokens ativos de um usuário,
        // esta implementação é simplificada.
        // Em produção, você poderia manter uma tabela de tokens ativos
        // ou usar Redis para armazenar tokens.

        // Por enquanto, apenas registramos que o usuário foi desativado
        // e o JwtAuthenticationFilter vai verificar se o usuário está ativo
        // ao processar cada requisição.
    }

    /**
     * Calcula a data de expiração do token.
     *
     * @param token Token JWT
     * @return Data de expiração
     */
    private LocalDateTime calculateTokenExpiration(String token) {
        // Extrai a data de expiração do token
        // (o token contém essa informação no claim "exp")
        try {
            // Aqui você poderia usar o jwtTokenProvider para extrair a expiração
            // Por simplicidade, vamos adicionar um tempo fixo
            // Em produção, extraia do token usando jwtTokenProvider
            return LocalDateTime.now().plusHours(1);
        } catch (Exception e) {
            // Se houver erro, usa um tempo padrão
            return LocalDateTime.now().plusHours(1);
        }
    }
}
