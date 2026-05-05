package com.empresa.reportgenerator.dto.report;

import com.empresa.reportgenerator.entity.enums.OutputFormat;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor


public class ReportRequest {

    @NotNull(message = "Template ID é obrigatório")
    private Long templateId;

    @NotNull(message = "Formato de saída é obrigatório")
    private OutputFormat outputFormat;

    @NotNull(message = "Dados do relatório são obrigatórios")
    @Valid
    private ReportData data;
}
