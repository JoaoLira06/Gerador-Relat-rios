package com.empresa.reportgenerator.controller;

import com.empresa.reportgenerator.dto.report.ReportRequest;
import com.empresa.reportgenerator.dto.template.Template;
import com.empresa.reportgenerator.service.ReportService;
import com.empresa.reportgenerator.service.TemplateService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Controller de geração de relatórios.
 */
@RestController
    @RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;
    private final TemplateService templateService;

    /**
     * POST /api/reports/generate
     * Gera um relatório e retorna o arquivo para download.
     */
    @PostMapping("/generate")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<byte[]> generateReport(
            @Valid @RequestBody ReportRequest request) {

        // 1. Gera o arquivo
        byte[] reportBytes = reportService.generateReport(request);

        // 2. Busca o template para montar o nome do arquivo
        Template template = templateService.getTemplate(request.getTemplateId());

        // 3. Monta os headers da resposta
        String contentType = reportService.getContentType(request);
        String fileName = reportService.getFileName(request, template);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(contentType));
        headers.setContentDispositionFormData("attachment", fileName);
        headers.setContentLength(reportBytes.length);

        return ResponseEntity.ok()
                .headers(headers)
                .body(reportBytes);
    }
}
