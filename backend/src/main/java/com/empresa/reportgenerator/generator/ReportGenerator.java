package com.empresa.reportgenerator.generator;

import com.empresa.reportgenerator.dto.report.ReportData;
import com.empresa.reportgenerator.dto.template.Template;
import com.empresa.reportgenerator.entity.enums.OutputFormat;

public interface ReportGenerator {

    byte[] generate(Template template, ReportData data);


    /**
     * Retorna o formato de saída suportado por esta implementação.
     * Usado pelo GeneratorFactory para selecionar o generator correto.
     *
     * @return OutputFormat (PDF ou EXCEL)
     */
    OutputFormat getSupportedFormat();

}
