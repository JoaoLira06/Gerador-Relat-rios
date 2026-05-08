package com.empresa.reportgenerator.controller;

import com.empresa.reportgenerator.entity.AuditLog;
import com.empresa.reportgenerator.service.AuditService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

/**
 * Controller de auditoria.
 * Apenas ADMIN pode acessar os logs.
 */
@RestController
@RequestMapping("/api/audit-logs")
@RequiredArgsConstructor
public class AuditController {

    private final AuditService auditService;

    /**
     * GET /api/audit-logs
     * Lista logs de auditoria com filtros opcionais e paginação.
     *
     * Parâmetros opcionais:
     * - username: filtrar por usuário
     * - startDate: data inicial (ISO-8601)
     * - endDate: data final (ISO-8601)
     * - page: número da página (padrão: 0)
     * - size: itens por página (padrão: 20)
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<AuditLog>> getAuditLogs(
            @RequestParam(required = false) String username,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size,
                Sort.by("createdAt").descending());

        Page<AuditLog> logs = auditService.getAuditLogs(
                username, startDate, endDate, pageable);

        return ResponseEntity.ok(logs);
    }
}
