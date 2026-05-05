package com.empresa.reportgenerator.service;

import com.empresa.reportgenerator.dto.template.FieldDefinition;
import com.empresa.reportgenerator.dto.template.Template;
import com.empresa.reportgenerator.dto.template.TemplateSchema;
import com.empresa.reportgenerator.entity.TemplateEntity;
import com.empresa.reportgenerator.entity.enums.FieldType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class TemplateParser {


    private final ObjectMapper objectMapper;
    public Template parse(TemplateEntity entity) {
        try {
            // 1. Parseia o schemaDefinition (JSON → TemplateSchema)
            TemplateSchema schema = parseSchema(entity.getSchemaDefinition());

            // 2. Parseia o layoutDefinition (JSON → Map)
            Map<String, Object> layout = parseLayout(entity.getLayoutDefinition());

            // 3. Monta o Template domain model
            return Template.builder()
                    .id(entity.getId())
                    .name(entity.getName())
                    .description(entity.getDescription())
                    .type(entity.getType())
                    .schema(schema)
                    .layout(layout)
                    .build();

        } catch (JsonProcessingException e) {
            throw new RuntimeException(
                    "Erro ao parsear template '" + entity.getName() + "': " + e.getMessage(), e
            );
        }
    }

    /**
     * Parseia o schemaDefinition JSON em um objeto TemplateSchema.
     *
     * Exemplo de JSON esperado:
     * {
     *   "fields": [
     *     {"name": "date", "type": "DATE", "required": true},
     *     {"name": "amount", "type": "CURRENCY", "required": true}
     *   ]
     * }
     */
    private TemplateSchema parseSchema(String schemaJson) throws JsonProcessingException {
        if (schemaJson == null || schemaJson.isBlank()) {
            return TemplateSchema.builder().fields(new ArrayList<>()).build();
        }

        // Deserializa o JSON em um Map genérico
        Map<String, Object> schemaMap = objectMapper.readValue(
                schemaJson, new TypeReference<Map<String, Object>>() {}
        );

        // Extrai a lista de campos
        List<FieldDefinition> fields = new ArrayList<>();

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> fieldsData =
                (List<Map<String, Object>>) schemaMap.get("fields");

        if (fieldsData != null) {
            for (Map<String, Object> fieldData : fieldsData) {
                FieldDefinition field = FieldDefinition.builder()
                        .name((String) fieldData.get("name"))
                        .type(FieldType.valueOf((String) fieldData.get("type")))
                        .required(Boolean.TRUE.equals(fieldData.get("required")))
                        .description((String) fieldData.getOrDefault("description", ""))
                        .build();
                fields.add(field);
            }
        }

        return TemplateSchema.builder().fields(fields).build();
    }

    /**
     * Parseia o layoutDefinition JSON em um Map genérico.
     *
     * Exemplo de JSON esperado:
     * {
     *   "sections": [
     *     {"type": "header", "title": "Relatório de Vendas"},
     *     {"type": "table", "fields": ["date", "product", "amount"]}
     *   ]
     * }
     */
    private Map<String, Object> parseLayout(String layoutJson) throws JsonProcessingException {
        if (layoutJson == null || layoutJson.isBlank()) {
            return Map.of();
        }

        return objectMapper.readValue(
                layoutJson, new TypeReference<Map<String, Object>>() {}
        );
    }

    /**
     * Valida se um JSON de schema tem a estrutura correta.
     *
     * @param schemaJson JSON a ser validado
     * @return true se válido, false caso contrário
     */
    public boolean isValidSchemaJson(String schemaJson) {
        try {
            if (schemaJson == null || schemaJson.isBlank()) {
                return false;
            }
            Map<String, Object> schema = objectMapper.readValue(
                    schemaJson, new TypeReference<Map<String, Object>>() {}
            );
            // Deve ter o campo "fields"
            return schema.containsKey("fields");
        } catch (JsonProcessingException e) {
            return false;
        }
    }
}
