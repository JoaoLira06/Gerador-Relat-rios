package com.empresa.reportgenerator.dto.template;

import com.empresa.reportgenerator.entity.enums.FieldType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Domain model que representa a definição de um campo no schema do template.
 * Cada campo tem um nome, tipo e flag indicando se é obrigatório.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FieldDefinition {
    
    private String name;
    private FieldType type;
    private boolean required;
    private String description;
}
