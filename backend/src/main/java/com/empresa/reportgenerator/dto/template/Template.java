package com.empresa.reportgenerator.dto.template;

import com.empresa.reportgenerator.entity.enums.TemplateType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * Domain model que representa um template de relatório.
 * Este objeto é usado internamente pelo sistema para processar templates.
 * Não é exposto diretamente na API REST.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Template {
    
    private Long id;
    private String name;
    private String description;
    private TemplateType type;
    private TemplateSchema schema;
    private Map<String, Object> layout;
}
