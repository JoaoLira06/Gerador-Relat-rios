-- Schema do banco de dados: Gerador de Relatórios Empresarial
-- Execute este script manualmente no PostgreSQL para criar o banco
-- Comando: psql -U postgres -d reportdb -f schema.sql

-- Cria o banco se não existir (execute separadamente se necessário)
-- CREATE DATABASE reportdb;

-- =============================================
-- TABELA: users
-- Armazena os usuários do sistema com seus perfis
-- =============================================
CREATE TABLE IF NOT EXISTS users (
    id           BIGSERIAL    PRIMARY KEY,
    username     VARCHAR(50)  NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,          -- Sempre BCrypt, nunca texto puro
    email        VARCHAR(100) NOT NULL UNIQUE,
    role         VARCHAR(20)  NOT NULL,            -- 'USER' ou 'ADMIN'
    active       BOOLEAN      NOT NULL DEFAULT true,
    created_at   TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at   TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- =============================================
-- TABELA: templates
-- Armazena templates pré-definidos e customizados
-- JSONB: formato binário do JSON no PostgreSQL
-- Vantagem: permite consultas dentro do JSON e é mais eficiente que TEXT
-- =============================================
CREATE TABLE IF NOT EXISTS templates (
    id                  BIGSERIAL    PRIMARY KEY,
    name                VARCHAR(100) NOT NULL UNIQUE,
    description         TEXT,
    type                VARCHAR(20)  NOT NULL,     -- 'PREDEFINED' ou 'CUSTOM'
    schema_definition   JSONB        NOT NULL,     -- Campos e tipos do relatório
    layout_definition   JSONB        NOT NULL,     -- Como o relatório é renderizado
    created_by          VARCHAR(50),               -- Username do admin que criou
    created_at          TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (created_by) REFERENCES users(username)
);

-- =============================================
-- TABELA: audit_logs
-- Registra TODA geração de relatório (sucesso ou falha)
-- Requisito 6: auditoria completa com retenção de 365 dias
-- =============================================
CREATE TABLE IF NOT EXISTS audit_logs (
    id            BIGSERIAL    PRIMARY KEY,
    user_id       BIGINT,                          -- Pode ser NULL se usuário foi deletado
    username      VARCHAR(50)  NOT NULL,           -- Guardamos o nome também por isso
    template_id   BIGINT,                          -- Pode ser NULL se template foi deletado
    template_name VARCHAR(100) NOT NULL,
    output_format VARCHAR(20)  NOT NULL,           -- 'PDF' ou 'EXCEL'
    success       BOOLEAN      NOT NULL,
    duration_ms   BIGINT,                          -- Tempo de geração em milissegundos
    error_message TEXT,                            -- Preenchido apenas quando success=false
    created_at    TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL,
    FOREIGN KEY (template_id) REFERENCES templates(id) ON DELETE SET NULL
);

-- Índices para acelerar as consultas mais comuns de auditoria
CREATE INDEX IF NOT EXISTS idx_audit_created_at  ON audit_logs(created_at);
CREATE INDEX IF NOT EXISTS idx_audit_username    ON audit_logs(username);
CREATE INDEX IF NOT EXISTS idx_audit_template_id ON audit_logs(template_id);

-- =============================================
-- TABELA: revoked_tokens
-- Tokens JWT invalidados (logout, desativação de usuário)
-- Sem essa tabela, um token roubado seria válido até expirar
-- =============================================
CREATE TABLE IF NOT EXISTS revoked_tokens (
    id          BIGSERIAL    PRIMARY KEY,
    token_jti   VARCHAR(100) NOT NULL UNIQUE,  -- JWT ID: identificador único do token
    username    VARCHAR(50)  NOT NULL,
    revoked_at  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    expires_at  TIMESTAMP    NOT NULL           -- Quando o token expiraria naturalmente
);

-- Índices para verificação rápida de tokens revogados (acontece em TODA requisição)
CREATE INDEX IF NOT EXISTS idx_revoked_jti        ON revoked_tokens(token_jti);
CREATE INDEX IF NOT EXISTS idx_revoked_expires_at ON revoked_tokens(expires_at);
