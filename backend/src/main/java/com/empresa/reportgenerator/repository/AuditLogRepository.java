package com.empresa.reportgenerator.repository;

import com.empresa.reportgenerator.entity.AuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repositório para acesso aos logs de auditoria.
 * Suporta queries complexas com filtros e paginação.
 */
@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
    
    /**
     * Busca logs de auditoria por username com paginação.
     * Usado pelo ADMIN para ver atividades de um usuário específico.
     * 
     * Query gerada: SELECT * FROM audit_logs WHERE username = ? ORDER BY created_at DESC
     */
    Page<AuditLog> findByUsernameOrderByCreatedAtDesc(String username, Pageable pageable);
    
    /**
     * Busca logs de auditoria em um intervalo de datas com paginação.
     * Usado para relatórios de auditoria por período.
     * 
     * Query customizada com @Query para filtro de range de datas.
     */
    @Query("SELECT a FROM AuditLog a WHERE a.createdAt BETWEEN :startDate AND :endDate ORDER BY a.createdAt DESC")
    Page<AuditLog> findByDateRange(
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate,
        Pageable pageable
    );
    
    /**
     * Busca logs de auditoria por username e intervalo de datas com paginação.
     * Combina os dois filtros anteriores.
     * 
     * Query customizada para filtro combinado.
     */
    @Query("SELECT a FROM AuditLog a WHERE a.username = :username AND a.createdAt BETWEEN :startDate AND :endDate ORDER BY a.createdAt DESC")
    Page<AuditLog> findByUsernameAndDateRange(
        @Param("username") String username,
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate,
        Pageable pageable
    );
    
    /**
     * Busca logs de auditoria que falharam (success = false).
     * Usado para monitoramento de erros.
     * 
     * Query gerada: SELECT * FROM audit_logs WHERE success = false ORDER BY created_at DESC
     */
    List<AuditLog> findBySuccessFalseOrderByCreatedAtDesc();
    
    /**
     * Conta quantos relatórios foram gerados por um template específico.
     * Usado para estatísticas de uso de templates.
     * 
     * Query gerada: SELECT COUNT(*) FROM audit_logs WHERE template_id = ?
     */
    long countByTemplateId(Long templateId);
}
