package com.empresa.reportgenerator.dto.report;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class ReportData {

    @NotNull(message = "Registros são obrigatórios")
    @Size(min = 1, max = 50000, message = "Número de registros deve estar entre 1 e 50000")
    private List<Map<String, Object>> records;

    private Map<String, Object> metadata;
}
