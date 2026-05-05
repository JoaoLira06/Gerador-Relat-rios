package com.empresa.reportgenerator.repository;

import com.empresa.reportgenerator.entity.RevokedToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Repositório para acesso aos tokens revogados.
 * Usado para invalidar tokens JWT após logout ou desativação de usuário.
 */
@Repository
public interface RevokedTokenRepository extends JpaRepository<RevokedToken, Long> {
    
    /**
     * Busca um token revogado pelo JTI (JWT ID).
     * Usado para verificar se um token foi revogado durante a autenticação.
     * 
     * Query gerada: SELECT * FROM revoked_tokens WHERE token_jti = ?
     */
    Optional<RevokedToken> findByTokenJti(String tokenJti);
    
    /**
     * Verifica se um token JTI está revogado.
     * Mais eficiente que findByTokenJti quando só precisa saber se existe.
     * 
     * Query gerada: SELECT COUNT(*) > 0 FROM revoked_tokens WHERE token_jti = ?
     */
    boolean existsByTokenJti(String tokenJti);
    
    /**
     * Remove tokens revogados que já expiraram.
     * Usado para limpeza periódica (job agendado).
     * 
     * Query customizada com @Modifying para DELETE.
     * 
     * @param now Data/hora atual para comparar com expires_at
     * @return Número de registros deletados
     */
    @Modifying
    @Query("DELETE FROM RevokedToken r WHERE r.expiresAt < :now")
    int deleteExpiredTokens(@Param("now") LocalDateTime now);
    
    /**
     * Busca todos os tokens revogados de um usuário específico.
     * Usado quando um usuário é desativado para revogar todos os seus tokens.
     * 
     * Query gerada: SELECT * FROM revoked_tokens WHERE username = ?
     */
    @Query("SELECT r FROM RevokedToken r WHERE r.username = :username")
    java.util.List<RevokedToken> findByUsername(@Param("username") String username);
}
