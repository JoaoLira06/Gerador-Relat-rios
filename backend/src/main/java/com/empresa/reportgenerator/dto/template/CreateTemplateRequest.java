package com.empresa.reportgenerator.dto.template;

import com.empresa.reportgenerator.entity.enums.TemplateType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateTemplateRequest {
    
    @NotBlank(message = "Nome do template é obrigatório")
    @Size(max = 100, message = "Nome deve ter no máximo 100 caracteres")
    private String name;
    
    @Size(max = 500, message = "Descrição deve ter no máximo 500 caracteres")
    private String description;
    
    @NotNull(message = "Tipo do template é obrigatório")
    private TemplateType type;
    
    @NotBlank(message = "Definição do schema é obrigatória")
    private String schemaDefinition;
    
    @NotBlank(message = "Definição do layout é obrigatória")
    private String layoutDefinition;
}
