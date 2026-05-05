package com.empresa.reportgenerator.repository;

import com.empresa.reportgenerator.entity.TemplateEntity;
import com.empresa.reportgenerator.entity.enums.TemplateType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositório para acesso aos dados de templates.
 */
@Repository
public interface TemplateRepository extends JpaRepository<TemplateEntity, Long> {
    
    /**
     * Busca templates por tipo (PREDEFINED ou CUSTOM).
     * 
     * Query gerada: SELECT * FROM templates WHERE type = ?
     */
    List<TemplateEntity> findByType(TemplateType type);
    
    /**
     * Busca um template pelo nome.
     * Útil para evitar duplicação de nomes.
     * 
     * Query gerada: SELECT * FROM templates WHERE name = ?
     */
    Optional<TemplateEntity> findByName(String name);
    
    /**
     * Busca templates criados por um usuário específico.
     * Usado para listar templates customizados de um usuário.
     * 
     * Query gerada: SELECT * FROM templates WHERE created_by = ?
     */
    List<TemplateEntity> findByCreatedBy(String createdBy);
    
    /**
     * Busca todos os templates pré-definidos ordenados por nome.
     * Usado para popular o cache de templates.
     * 
     * Query customizada com @Query para garantir ordenação.
     */
    @Query("SELECT t FROM TemplateEntity t WHERE t.type = 'PREDEFINED' ORDER BY t.name")
    List<TemplateEntity> findAllPredefinedTemplatesOrderedByName();
}
