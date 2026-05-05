package com.empresa.reportgenerator.entity;

import com.empresa.reportgenerator.entity.enums.OutputFormat;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;



@Entity
@Table(name = "audit_logs", indexes = {
        @Index(name = "idx_audit_created_at", columnList = "created_at"),
        @Index(name = "idx_audit_username", columnList = "username"),
        @Index(name = "idx_audit_template_id", columnList = "template_id")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor



public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Guardamos o ID e o username separados.
    // Por quê? Se o usuário for deletado, o ID some — mas o username fica aqui para histórico
    @Column(name = "user_id")
    private Long userId;

    @Column(nullable = false, length = 50)
    private String username;

    // Mesmo raciocínio: template pode ser deletado, mas o nome fica registrado
    @Column(name = "template_id")
    private Long templateId;

    @Column(name = "template_name", nullable = false, length = 100)
    private String templateName;

    // Qual formato foi gerado: PDF ou EXCEL
    @Enumerated(EnumType.STRING)
    @Column(name = "output_format", nullable = false, length = 20)
    private OutputFormat outputFormat;

    // true = gerou com sucesso | false = falhou
    @Column(nullable = false)
    private Boolean success;

    // Quanto tempo levou para gerar o relatório (em milissegundos)
    // Útil para identificar relatórios lentos e otimizar performance
    @Column(name = "duration_ms")
    private Long durationMs;

    // Preenchido apenas quando success = false
    // Guardamos a mensagem de erro para o admin poder investigar
    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;

    // Logs de auditoria NUNCA são editados — só inseridos
    // Por isso só temos @PrePersist, sem @PreUpdate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
