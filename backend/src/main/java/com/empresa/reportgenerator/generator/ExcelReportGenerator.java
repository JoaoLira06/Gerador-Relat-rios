package com.empresa.reportgenerator.generator;

import com.empresa.reportgenerator.dto.report.ReportData;
import com.empresa.reportgenerator.dto.template.Template;
import com.empresa.reportgenerator.entity.enums.OutputFormat;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Gerador de relatórios em formato Excel (.xlsx).
 * Usa Apache POI para criar o arquivo.
 */
@Component
public class ExcelReportGenerator implements ReportGenerator {

    @Override
    public OutputFormat getSupportedFormat() {
        return OutputFormat.EXCEL;
    }

    @Override
    public byte[] generate(Template template, ReportData data) {
        // XSSFWorkbook = workbook para formato .xlsx (moderno)
        try (Workbook workbook = new XSSFWorkbook()) {

            // 1. Cria a planilha com o nome do template
            Sheet sheet = workbook.createSheet(
                    template.getName() != null ? template.getName() : "Relatório"
            );

            // 2. Cria estilos de formatação
            CellStyle headerStyle = createHeaderStyle(workbook);
            CellStyle alternateStyle = createAlternateRowStyle(workbook);

            // 3. Extrai as colunas do primeiro registro
            List<String> columns = getColumns(data);

            // 4. Cria a linha de cabeçalho (linha 0)
            createHeaderRow(sheet, columns, headerStyle);

            // 5. Cria as linhas de dados
            createDataRows(sheet, columns, data.getRecords(), alternateStyle);

            // 6. Ajusta largura das colunas automaticamente
            for (int i = 0; i < columns.size(); i++) {
                sheet.autoSizeColumn(i);
            }

            // 7. Serializa para bytes
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return outputStream.toByteArray();

        } catch (IOException e) {
            throw new RuntimeException("Erro ao gerar Excel: " + e.getMessage(), e);
        }
    }

    /**
     * Cria a linha de cabeçalho com estilo destacado.
     */
    private void createHeaderRow(Sheet sheet, List<String> columns, CellStyle style) {
        Row headerRow = sheet.createRow(0);

        for (int i = 0; i < columns.size(); i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(columns.get(i).toUpperCase());
            cell.setCellStyle(style);
        }
    }

    /**
     * Cria as linhas de dados com estilo alternado.
     */
    private void createDataRows(Sheet sheet, List<String> columns,
                                List<Map<String, Object>> records, CellStyle alternateStyle) {
        for (int rowIndex = 0; rowIndex < records.size(); rowIndex++) {
            Row row = sheet.createRow(rowIndex + 1); // +1 porque linha 0 é o cabeçalho
            Map<String, Object> record = records.get(rowIndex);

            for (int colIndex = 0; colIndex < columns.size(); colIndex++) {
                Cell cell = row.createCell(colIndex);
                Object value = record.get(columns.get(colIndex));
                setCellValue(cell, value);

                // Aplica estilo alternado nas linhas pares
                if (rowIndex % 2 == 1) {
                    cell.setCellStyle(alternateStyle);
                }
            }
        }
    }

    /**
     * Define o valor da célula com o tipo correto.
     * POI tem tipos específicos para números, booleanos e strings.
     */
    private void setCellValue(Cell cell, Object value) {
        if (value == null) {
            cell.setCellValue("");
        } else if (value instanceof Number number) {
            // Números são armazenados como double no Excel
            cell.setCellValue(number.doubleValue());
        } else if (value instanceof Boolean bool) {
            cell.setCellValue(bool);
        } else {
            // Tudo mais vira String
            cell.setCellValue(value.toString());
        }
    }

    /**
     * Cria o estilo do cabeçalho: fundo azul escuro, texto branco, negrito.
     */
    private CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();

        // Fundo azul escuro
        style.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        // Fonte branca e negrito
        Font font = workbook.createFont();
        font.setColor(IndexedColors.WHITE.getIndex());
        font.setBold(true);
        style.setFont(font);

        // Borda
        style.setBorderBottom(BorderStyle.THIN);

        return style;
    }

    /**
     * Cria o estilo para linhas alternadas: fundo cinza claro.
     */
    private CellStyle createAlternateRowStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();

        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        return style;
    }

    /**
     * Extrai os nomes das colunas do primeiro registro.
     */
    private List<String> getColumns(ReportData data) {
        if (data.getRecords() == null || data.getRecords().isEmpty()) {
            return List.of();
        }
        return List.copyOf(data.getRecords().get(0).keySet());
    }
}
