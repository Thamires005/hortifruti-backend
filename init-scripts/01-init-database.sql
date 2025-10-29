-- ============================================
-- SCRIPT DE INICIALIZAÇÃO DO BANCO HORTIFRUTI
-- ============================================
-- Este script é executado automaticamente pelo PostgreSQL
-- na primeira inicialização do container
-- ============================================

\c hortifruti_db;

-- Configurar encoding
SET client_encoding = 'UTF8';

-- ============================================
-- CRIAR EXTENSÕES
-- ============================================

CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE EXTENSION IF NOT EXISTS "pg_trgm";

-- ============================================
-- FUNÇÕES AUXILIARES
-- ============================================

-- Função para atualizar timestamp automaticamente
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

-- ============================================
-- CONFIGURAÇÕES DE PERFORMANCE
-- ============================================

ALTER DATABASE hortifruti_db SET synchronous_commit TO 'off';
ALTER DATABASE hortifruti_db SET work_mem TO '64MB';
ALTER DATABASE hortifruti_db SET maintenance_work_mem TO '256MB';
ALTER DATABASE hortifruti_db SET effective_cache_size TO '1GB';

-- ============================================
-- GRANTS E PERMISSÕES
-- ============================================

GRANT ALL PRIVILEGES ON DATABASE hortifruti_db TO hortifruti_user;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO hortifruti_user;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO hortifruti_user;

-- Garantir permissões futuras
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON TABLES TO hortifruti_user;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON SEQUENCES TO hortifruti_user;

-- ============================================
-- LOG DE SUCESSO
-- ============================================

DO $$
BEGIN
    RAISE NOTICE '';
    RAISE NOTICE '================================================';
    RAISE NOTICE 'INICIALIZAÇÃO DO BANCO CONCLUÍDA!';
    RAISE NOTICE '================================================';
    RAISE NOTICE '';
    RAISE NOTICE 'Database: hortifruti_db';
    RAISE NOTICE 'Extensões habilitadas:';
    RAISE NOTICE '  - uuid-ossp';
    RAISE NOTICE '  - pg_trgm';
    RAISE NOTICE '';
    RAISE NOTICE 'O JPA do Spring Boot criará as tabelas automaticamente.';
    RAISE NOTICE 'Dados iniciais serão inseridos após a criação das tabelas.';
    RAISE NOTICE '';
    RAISE NOTICE '================================================';
    RAISE NOTICE '';
END $$;
