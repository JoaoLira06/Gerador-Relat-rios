package com.empresa.reportgenerator.dto.template;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Domain model que representa o schema de um template.
 * Define quais campos o template espera receber nos dados do relatório.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TemplateSchema {
    
    private List<FieldDefinition> fields;
}
