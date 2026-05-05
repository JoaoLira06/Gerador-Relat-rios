package com.empresa.reportgenerator.entity.enums;

/**
 * Classifica a origem de um template.
 *
 * Essa distinção é importante porque:
 * - Templates PREDEFINED são criados pelo sistema (via script de inicialização)
 *   e não podem ser deletados por usuários
 * - Templates CUSTOM são criados por administradores e podem ser gerenciados
 *   via API (criar, editar, deletar)
 */
public enum TemplateType {

    /**
     * Template fornecido pelo sistema.
     * Exemplos: Cash Flow, Balance Sheet, Vendas, Estoque, Clientes.
     * Ficam em cache em memória para acesso rápido.
     */
    PREDEFINED,

    /**
     * Template criado por um administrador para necessidades específicas
     * da empresa. Armazenado no banco de dados.
     */
    CUSTOM
}
