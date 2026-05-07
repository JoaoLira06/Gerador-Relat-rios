package com.empresa.reportgenerator.service;

import com.empresa.reportgenerator.dto.report.ReportRequest;
import com.empresa.reportgenerator.dto.report.ReportResult;
import com.empresa.reportgenerator.dto.template.Template;
import com.empresa.reportgenerator.entity.User;
import com.empresa.reportgenerator.generator.GeneratorFactory;
import com.empresa.reportgenerator.generator.ReportGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * Serviço principal de geração de relatórios.
 * Orquestra: validação → template → geração → auditoria.
 */
@Service
@RequiredArgsConstructor
public class ReportService {

    private final ValidationService validationService;
    private final TemplateService templateService;
    private final GeneratorFactory generatorFactory;
    private final AuditService auditService;
    private final UserService userService;

    /**
     * Gera um relatório de forma síncrona.
     * Retorna os bytes do arquivo gerado (PDF ou Excel).
     *
     * @param request Requisição com templateId, formato e dados
     * @return Array de bytes do arquivo gerado
     */
    public byte[] generateReport(ReportRequest request) {
        long startTime = System.currentTimeMillis();

        // Pega o usuário autenticado do SecurityContext
        String username = SecurityContextHolder.getContext()
                .getAuthentication().getName();
        User user = userService.findByUsername(username);

        try {
            // 1. Valida a requisição (campos obrigatórios, limites)
            validationService.validateReportRequest(request);

            // 2. Busca o template (com cache)
            Template template = templateService.getTemplate(request.getTemplateId());

            // 3. Valida os dados contra o schema do template
            validationService.validateDataAgainSchema(request.getData(), template);

            // 4. Seleciona o generator correto (PDF ou Excel)
            ReportGenerator generator = generatorFactory.getGenerator(request.getOutputFormat());

            // 5. Gera o arquivo
            byte[] reportBytes = generator.generate(template, request.getData());

            // 6. Registra auditoria de sucesso
            long duration = System.currentTimeMillis() - startTime;
            auditService.logSuccess(
                    user.getId(),
                    username,
                    template.getId(),
                    template.getName(),
                    request.getOutputFormat(),
                    duration
            );

            return reportBytes;

        } catch (Exception e) {
            // 7. Registra auditoria de falha
            long duration = System.currentTimeMillis() - startTime;
            auditService.logFailure(
                    user.getId(),
                    username,
                    request.getTemplateId(),
                    "Desconhecido",
                    request.getOutputFormat(),
                    e.getMessage(),
                    duration
            );

            // Re-lança a exceção para o Controller tratar
            throw e;
        }
    }

    /**
     * Retorna o Content-Type correto para o formato solicitado.
     * Usado pelo Controller para configurar o header da resposta HTTP.
     *
     * @param request Requisição com o formato
     * @return Content-Type string
     */
    public String getContentType(ReportRequest request) {
        return switch (request.getOutputFormat()) {
            case PDF -> "application/pdf";
            case EXCEL -> "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
        };
    }

    /**
     * Retorna o nome do arquivo para o header Content-Disposition.
     *
     * @param request  Requisição com o formato
     * @param template Template usado
     * @return Nome do arquivo (ex: "relatorio-vendas.pdf")
     */
    public String getFileName(ReportRequest request, Template template) {
        String baseName = template.getName()
                .toLowerCase()
                .replaceAll("[^a-z0-9]", "-");

        String extension = switch (request.getOutputFormat()) {
            case PDF -> ".pdf";
            case EXCEL -> ".xlsx";
        };

        return "relatorio-" + baseName + extension;
    }
}
