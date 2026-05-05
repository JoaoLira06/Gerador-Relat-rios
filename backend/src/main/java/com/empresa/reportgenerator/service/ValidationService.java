package com.empresa.reportgenerator.service;


import com.empresa.reportgenerator.dto.report.ReportData;
import com.empresa.reportgenerator.dto.report.ReportRequest;
import com.empresa.reportgenerator.dto.template.FieldDefinition;
import com.empresa.reportgenerator.dto.template.Template;
import com.empresa.reportgenerator.entity.enums.FieldType;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class ValidationService {

    //Limite máximo de requisição
    private static final int MAX_RECORDS  = 50_000;

    public void validateReportRequest(ReportRequest request) {
        List<String> errors = new ArrayList<>();

        if (request == null) {
            throw new IllegalArgumentException("Requisição não pode ser nula");
        }

        if (request.getTemplateId() == null) {
            errors.add("Template ID é obrigatório");
        }

        if (request.getOutputFormat() == null) {
            errors.add("Formato de saída é obrigatório");
        }

        if (request.getData() == null) {
            errors.add("Dados do relatório são obrigatórios");
        } else {
            validateReportData(request.getData());
        }

        if (!errors.isEmpty()) {
            throw new IllegalArgumentException("Erros de validação: " + String.join(", ", errors));
        }
    }

    public void validateReportData(ReportData data) {
        List<String> errors = new ArrayList<>();

        if (data.getRecords() == null || data.getRecords().isEmpty()) {
            errors.add("Lista de registros não pode ser vazia");
        } else if (data.getRecords().size() > MAX_RECORDS) {
            errors.add("Número de registros excede o limite de " + MAX_RECORDS);
        }

        if (!errors.isEmpty()) {
            throw new IllegalArgumentException("Erros nos dados: " + String.join(", ", errors));
        }
    }

    public void validateDataAgainSchema(ReportData data, Template template) {
            if(template.getSchema() == null || template.getSchema().getFields() == null) {
                return;
        }

            List<String> errors = new ArrayList<>();
            List<FieldDefinition> fields = template.getSchema().getFields();

                for(Map<String, Object> record : data.getRecords()) {
                    for(FieldDefinition field : fields) {

                        if (field.isRequired() && !record.containsKey(field.getName())) {
                            errors.add("Campo obrigatório ausente: " + field.getName());
                            continue;
                        }

                        Object value = record.get(field.getName());
                        if (value != null) {
                            validateFieldType(field.getName(), value, field.getType(), errors);
                        }
                    }
                }

        if (!errors.isEmpty()) {
            throw new IllegalArgumentException("Dados não batem com o schema: " +
                    String.join(", ", errors));
        }
    }

    /**
     * Valida se um valor bate com o tipo esperado do campo.
     *
     * @param fieldName Nome do campo (para mensagem de erro)
     * @param value     Valor a ser validado
     * @param fieldType Tipo esperado
     * @param errors    Lista de erros para adicionar
     */
    private void validateFieldType(String fieldName, Object value,
                                   FieldType fieldType, List<String> errors) {
        switch (fieldType) {
            case NUMBER, CURRENCY -> {
                // Aceita Number (Integer, Long, Double, BigDecimal)
                if (!(value instanceof Number)) {
                    errors.add("Campo '" + fieldName + "' deve ser numérico");
                }
            }
            case DATE -> {
                // Aceita String no formato ISO-8601
                if (value instanceof String dateStr) {
                    if (!isValidIsoDate(dateStr)) {
                        errors.add("Campo '" + fieldName +
                                "' deve estar no formato ISO-8601 (ex: 2024-01-15)");
                    }
                } else {
                    errors.add("Campo '" + fieldName + "' deve ser uma data em formato String");
                }
            }
            case STRING -> {
                // Aceita qualquer valor (converte para String)
                // Sem validação adicional necessária
            }
            case BOOLEAN -> {
                if (!(value instanceof Boolean)) {
                    errors.add("Campo '" + fieldName + "' deve ser booleano (true/false)");
                }
            }
        }
    }

    /**
     * Valida se uma string está no formato ISO-8601.
     * Aceita formatos: yyyy-MM-dd, yyyy-MM-ddTHH:mm:ss
     *
     * @param dateStr String de data a ser validada
     * @return true se válida, false caso contrário
     */
    public boolean isValidIsoDate(String dateStr) {
        if (dateStr == null || dateStr.isBlank()) {
            return false;
        }

        // Tenta formato de data simples: yyyy-MM-dd
        try {
            DateTimeFormatter.ISO_LOCAL_DATE.parse(dateStr);
            return true;
        } catch (DateTimeParseException e) {
            // Tenta formato de data e hora: yyyy-MM-ddTHH:mm:ss
            try {
                DateTimeFormatter.ISO_LOCAL_DATE_TIME.parse(dateStr);
                return true;
            } catch (DateTimeParseException ex) {
                return false;
            }
        }
    }

    /**
     * Valida se um número está dentro dos bounds permitidos.
     *
     * @param value Valor a ser validado
     * @param min   Valor mínimo permitido
     * @param max   Valor máximo permitido
     * @param fieldName Nome do campo (para mensagem de erro)
     * @throws IllegalArgumentException se o valor estiver fora dos bounds
     */
    public void validateNumericBounds(Number value, double min, double max, String fieldName) {
        double doubleValue = value.doubleValue();
        if (doubleValue < min || doubleValue > max) {
            throw new IllegalArgumentException(
                    "Campo '" + fieldName + "' deve estar entre " + min + " e " + max +
                            ". Valor recebido: " + doubleValue
            );
        }
    }
}





