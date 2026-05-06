package com.empresa.reportgenerator.service;

import com.empresa.reportgenerator.entity.AuditLog;
import com.empresa.reportgenerator.entity.enums.OutputFormat;
import com.empresa.reportgenerator.repository.AuditLogRepository;
import com.empresa.reportgenerator.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;


            // Registra todas as operações de geração de relatórios.//

@Service
@RequiredArgsConstructor
public class AuditService {
    private final AuditLogRepository auditLogRepository;


    @Transactional
    public void logSuccess(Long userId, String username, Long templateId,
                          String templateName, OutputFormat outputFormat, Long durationMs) {
        AuditLog log = AuditLog.builder()
                .userId(userId)
                .username(username)
                .templateId(templateId)
                .templateName(templateName)
                .outputFormat(outputFormat)
                .success(true)
                .durationMs(durationMs)
                .createdAt(LocalDateTime.now())
                .build();

        auditLogRepository.save(log);
    }

    @Transactional
    public void logFailure(Long userId, String username, Long templateId,
                           String templateName, OutputFormat outputFormat,
                           String errorMessage, Long durationMs) {
        AuditLog log = AuditLog.builder()
                .userId(userId)
                .username(username)
                .templateId(templateId)
                .templateName(templateName)
                .outputFormat(outputFormat)
                .success(false)
                .errorMessage(errorMessage)
                .durationMs(durationMs)
                .createdAt(LocalDateTime.now())
                .build();

        auditLogRepository.save(log);
    }

                //Busca logs de auditoria com filtros opcionais e paginação//
    public Page<AuditLog> getAuditLogs(String username, LocalDateTime startDate,
                                       LocalDateTime endDate, Pageable pageable) {
        // Decide qual query usar baseado nos filtros fornecidos
        if (username != null && startDate != null && endDate != null) {
            // Filtro por username E período
            return auditLogRepository.findByUsernameAndDateRange(
                    username, startDate, endDate, pageable);

        } else if (username != null) {
            // Filtro só por username
            return auditLogRepository.findByUsernameOrderByCreatedAtDesc(username, pageable);

        } else if (startDate != null && endDate != null) {
            // Filtro só por período
            return auditLogRepository.findByDateRange(startDate, endDate, pageable);

        } else {
            // Sem filtros — retorna todos paginados
            return auditLogRepository.findAll(pageable);
        }
    }


}
