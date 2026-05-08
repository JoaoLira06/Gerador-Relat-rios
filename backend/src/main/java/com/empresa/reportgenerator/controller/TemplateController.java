package com.empresa.reportgenerator.controller;

import com.empresa.reportgenerator.dto.template.CreateTemplateRequest;
import com.empresa.reportgenerator.dto.template.Template;
import com.empresa.reportgenerator.dto.template.UpdateTemplateRequest;
import com.empresa.reportgenerator.service.TemplateService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller de gerenciamento de templates.
 * GET: USER e ADMIN
 * POST, PUT, DELETE: apenas ADMIN
 */
@RestController
@RequestMapping("/api/templates")
@RequiredArgsConstructor
public class TemplateController {

    private final TemplateService templateService;

    /**
     * GET /api/templates
     * Lista todos os templates disponíveis.
     */



    @GetMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<List<Template>> listTemplates() {
        return ResponseEntity.ok(templateService.listTemplates());
    }

    /**
     * GET /api/templates/{id}
     * Busca um template pelo ID.
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<Template> getTemplate(@PathVariable Long id) {
        return ResponseEntity.ok(templateService.getTemplate(id));
    }

    /**
     * POST /api/templates
     * Cria um novo template customizado. Apenas ADMIN.
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Template> createTemplate(
            @Valid @RequestBody CreateTemplateRequest request) {

        Template created = templateService.createTemplate(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * PUT /api/templates/{id}
     * Atualiza um template existente. Apenas ADMIN.
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Template> updateTemplate(
            @PathVariable Long id,
            @RequestBody UpdateTemplateRequest request) {

        Template updated = templateService.updateTemplate(id, request);
        return ResponseEntity.ok(updated);
    }

    /**
     * DELETE /api/templates/{id}
     * Deleta um template. Apenas ADMIN.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteTemplate(@PathVariable Long id) {
        templateService.deleteTemplate(id);
        return ResponseEntity.noContent().build();
    }
}
