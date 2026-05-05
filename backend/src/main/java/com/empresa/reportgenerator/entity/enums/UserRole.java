package com.empresa.reportgenerator.entity.enums;

/**
 * Define os perfis de acesso do sistema.
 *
 * Por que enum e não String?
 * - Segurança em tempo de compilação: UserRole.ADMIN não tem como errar a grafia
 * - Autocompletar na IDE
 * - Fácil de usar em switch/if e no Spring Security (@PreAuthorize)
 *
 * No banco de dados, o valor é salvo como texto: "USER" ou "ADMIN"
 * (configurado com @Enumerated(EnumType.STRING) na entidade)
 */
public enum UserRole {

    /**
     * Usuário comum: pode gerar relatórios usando templates existentes.
     * Não pode criar/editar templates nem gerenciar outros usuários.
     */
    USER,

    /**
     * Administrador: acesso total ao sistema.
     * Pode gerenciar templates, usuários e visualizar logs de auditoria.
     */
    ADMIN
}
