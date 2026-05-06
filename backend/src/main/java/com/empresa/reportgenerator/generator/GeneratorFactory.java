package com.empresa.reportgenerator.generator;

import com.empresa.reportgenerator.entity.enums.OutputFormat;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;


/**
 * Factory para selecionar o generator correto baseado no formato de saída.
 *
 * Padrão Factory: centraliza a criação/seleção de objetos.
 *
 * O Spring injeta automaticamente TODAS as implementações de ReportGenerator
 * na lista — não precisamos registrar cada uma manualmente!
 */


@Component
@RequiredArgsConstructor
public class GeneratorFactory {

    // Spring injeta automaticamente: [PdfReportGenerator, ExcelReportGenerator]
    private final List<ReportGenerator> generators;

    /**
     * Retorna o generator adequado para o formato solicitado.
     *
     * @param format Formato desejado (PDF ou EXCEL)
     * @return Generator correspondente ao formato
     * @throws IllegalArgumentException se o formato não for suportado
     */
    public ReportGenerator getGenerator(OutputFormat format) {
        return generators.stream()
                .filter(g -> g.getSupportedFormat() == format)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "Formato não suportado: " + format
                ));
    }

}
