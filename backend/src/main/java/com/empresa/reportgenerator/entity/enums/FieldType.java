package com.empresa.reportgenerator.entity.enums;

/**
 * Tipos de dados suportados nos campos de um template.
 *
 * Esses tipos são usados pelo ValidationService para verificar se os dados
 * enviados pelo usuário correspondem ao que o template espera.
 *
 * Exemplo: se um campo é do tipo DATE, o ValidationService vai verificar
 * se o valor está no formato ISO-8601 (ex: "2024-01-15").
 */
public enum FieldType {

    /** Texto livre. Ex: nome do produto, descrição */
    STRING,

    /** Número inteiro ou decimal. Ex: quantidade, percentual */
    NUMBER,

    /**
     * Data no formato ISO-8601: "YYYY-MM-DD".
     * Ex: "2024-01-15" para 15 de janeiro de 2024.
     * O ValidationService rejeita qualquer outro formato.
     */
    DATE,

    /** Verdadeiro ou falso. Ex: produto ativo, cliente inadimplente */
    BOOLEAN,

    /**
     * Valor monetário com precisão decimal.
     * Diferente de NUMBER porque recebe formatação especial:
     * símbolo de moeda, separadores e tratamento de negativos.
     * Ex: R$ 1.234,56 ou (R$ 100,00) para valores negativos
     */
    CURRENCY
}
