package com.empresa.reportgenerator.generator;

import com.empresa.reportgenerator.dto.report.ReportData;
import com.empresa.reportgenerator.dto.template.Template;
import com.empresa.reportgenerator.entity.enums.OutputFormat;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Gerador de relatórios em formato PDF.
 * Usa Apache PDFBox para criar o arquivo.
 */
@Component
public class PdfReportGenerator implements ReportGenerator {

    // Margens e dimensões da página
    private static final float MARGIN = 50f;
    private static final float PAGE_WIDTH = PDRectangle.A4.getWidth();
    private static final float PAGE_HEIGHT = PDRectangle.A4.getHeight();
    private static final float CONTENT_WIDTH = PAGE_WIDTH - 2 * MARGIN;

    // Tamanhos de fonte
    private static final float TITLE_FONT_SIZE = 16f;
    private static final float HEADER_FONT_SIZE = 11f;
    private static final float BODY_FONT_SIZE = 9f;
    private static final float LINE_HEIGHT = 15f;

    @Override
    public OutputFormat getSupportedFormat() {
        return OutputFormat.PDF;
    }

    @Override
    public byte[] generate(Template template, ReportData data) {
        try (PDDocument document = new PDDocument()) {

            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);

            try (PDPageContentStream content = new PDPageContentStream(document, page)) {
                float yPosition = PAGE_HEIGHT - MARGIN;

                // 1. Renderiza o título do relatório
                yPosition = renderTitle(content, template.getName(), yPosition);

                // 2. Renderiza os cabeçalhos das colunas
                List<String> columns = getColumns(data);
                yPosition = renderTableHeader(content, columns, yPosition);

                // 3. Renderiza os dados
                renderTableRows(document, content, columns, data.getRecords(), yPosition, page);
            }

            // Serializa o documento para bytes
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            document.save(outputStream);
            return outputStream.toByteArray();

        } catch (IOException e) {
            throw new RuntimeException("Erro ao gerar PDF: " + e.getMessage(), e);
        }
    }

    /**
     * Renderiza o título do relatório no topo da página.
     */
    private float renderTitle(PDPageContentStream content, String title,
                               float yPosition) throws IOException {
        PDType1Font boldFont = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);

        content.beginText();
        content.setFont(boldFont, TITLE_FONT_SIZE);
        content.newLineAtOffset(MARGIN, yPosition);
        content.showText(title != null ? title : "Relatório");
        content.endText();

        return yPosition - TITLE_FONT_SIZE - 20f;
    }

    /**
     * Renderiza o cabeçalho da tabela com fundo cinza.
     */
    private float renderTableHeader(PDPageContentStream content,
                                     List<String> columns, float yPosition) throws IOException {
        if (columns.isEmpty()) return yPosition;

        PDType1Font boldFont = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);
        float columnWidth = CONTENT_WIDTH / columns.size();

        // Fundo cinza para o cabeçalho
        content.setNonStrokingColor(0.85f, 0.85f, 0.85f);
        content.addRect(MARGIN, yPosition - LINE_HEIGHT, CONTENT_WIDTH, LINE_HEIGHT);
        content.fill();

        // Renderiza cada coluna individualmente
        content.setNonStrokingColor(0f, 0f, 0f);
        for (int i = 0; i < columns.size(); i++) {
            content.beginText();
            content.setFont(boldFont, HEADER_FONT_SIZE);
            content.newLineAtOffset(MARGIN + (i * columnWidth) + 2, yPosition - LINE_HEIGHT + 3);
            content.showText(truncateText(columns.get(i).toUpperCase(), columnWidth));
            content.endText();
        }

        return yPosition - LINE_HEIGHT - 2f;
    }

    /**
     * Renderiza as linhas de dados da tabela.
     * Cria novas páginas automaticamente quando necessário.
     */
    private float renderTableRows(PDDocument document, PDPageContentStream content,
                                   List<String> columns, List<Map<String, Object>> records,
                                   float yPosition, PDPage currentPage) throws IOException {
        if (columns.isEmpty() || records.isEmpty()) return yPosition;

        PDType1Font regularFont = new PDType1Font(Standard14Fonts.FontName.HELVETICA);
        float columnWidth = CONTENT_WIDTH / columns.size();
        boolean isAlternate = false;

        for (Map<String, Object> record : records) {
            // Verifica se precisa de nova página
            if (yPosition < MARGIN + LINE_HEIGHT) {
                content.close();
                PDPage newPage = new PDPage(PDRectangle.A4);
                document.addPage(newPage);
                yPosition = PAGE_HEIGHT - MARGIN;
            }

            // Fundo alternado para melhor legibilidade
            if (isAlternate) {
                content.setNonStrokingColor(0.95f, 0.95f, 0.95f);
                content.addRect(MARGIN, yPosition - LINE_HEIGHT, CONTENT_WIDTH, LINE_HEIGHT);
                content.fill();
                content.setNonStrokingColor(0f, 0f, 0f);
            }

            // Renderiza cada célula da linha
            for (int i = 0; i < columns.size(); i++) {
                Object value = record.get(columns.get(i));
                String cellText = formatValue(value);

                content.beginText();
                content.setFont(regularFont, BODY_FONT_SIZE);
                content.newLineAtOffset(MARGIN + (i * columnWidth) + 2,
                        yPosition - LINE_HEIGHT + 3);
                content.showText(truncateText(cellText, columnWidth));
                content.endText();
            }

            yPosition -= LINE_HEIGHT;
            isAlternate = !isAlternate;
        }

        return yPosition;
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

    /**
     * Formata um valor para exibição no PDF.
     */
    private String formatValue(Object value) {
        if (value == null) return "";
        return value.toString();
    }

    /**
     * Trunca texto para caber na largura da coluna.
     */
    private String truncateText(String text, float maxWidth) {
        if (text == null) return "";
        int maxChars = (int) (maxWidth / 6);
        if (text.length() > maxChars) {
            return text.substring(0, Math.max(0, maxChars - 3)) + "...";
        }
        return text;
    }
}
