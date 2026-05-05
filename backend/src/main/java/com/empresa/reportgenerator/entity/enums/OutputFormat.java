package com.empresa.reportgenerator.entity.enums;

/**
 * Formatos de saída suportados para geração de relatórios.
 *
 * Cada formato carrega seu content-type HTTP — isso é usado no controller
 * para informar ao navegador/cliente que tipo de arquivo está sendo retornado.
 *
 * Exemplo de uso:
 *   response.setContentType(OutputFormat.PDF.getContentType());
 */
public enum OutputFormat {

    /**
     * Portable Document Format — ideal para impressão e compartilhamento.
     * Gerado com Apache PDFBox.
     */
    PDF("application/pdf", ".pdf"),

    /**
     * Excel Open XML — ideal para análise de dados e planilhas.
     * Gerado com Apache POI (XSSFWorkbook).
     * O content-type longo é o padrão oficial para arquivos .xlsx
     */
    EXCEL("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", ".xlsx");

    // Atributos que cada valor do enum carrega
    private final String contentType;
    private final String fileExtension;

    // Construtor do enum (chamado automaticamente para cada valor acima)
    OutputFormat(String contentType, String fileExtension) {
        this.contentType = contentType;
        this.fileExtension = fileExtension;
    }

    public String getContentType() {
        return contentType;
    }

    public String getFileExtension() {
        return fileExtension;
    }
}
