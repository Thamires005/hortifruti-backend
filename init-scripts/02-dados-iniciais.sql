-- ============================================
-- DADOS INICIAIS - HORTIFRUTI
-- ============================================
-- Este script deve ser executado APÓS a aplicação criar as tabelas
-- Execute manualmente ou via aplicação após o primeiro start
-- ============================================

\c hortifruti_db;

-- ============================================
-- INSERIR CATEGORIAS PADRÃO
-- ============================================

INSERT INTO categoria (nome) VALUES
    ('Frutas'),
    ('Verduras'),
    ('Legumes'),
    ('Temperos'),
    ('Orgânicos'),
    ('Tubérculos'),
    ('Grãos'),
    ('Cereais')
ON CONFLICT DO NOTHING;

-- ============================================
-- INSERIR FORNECEDORES DE EXEMPLO
-- ============================================

INSERT INTO fornecedor (nome, cnpj, telefone, email, endereco, prod_fornecidos) VALUES
    ('Hortifruti Premium Ltda', '12.345.678/0001-34', '(11) 98765-4321', 'contato@hortifrutipremium.com', 'Rua das Flores, 123, São Paulo - SP', 'Frutas e Verduras Orgânicas'),
    ('Fazenda Verde', '98.765.432/0001-76', '(11) 99988-7766', 'vendas@fazendaverde.com', 'Estrada Rural, Km 45, Campinas - SP', 'Legumes e Temperos Frescos'),
    ('Distribuidora FrutaSul', '11.122.233/0001-55', '(11) 98877-6655', 'comercial@frutasul.com', 'Av. dos Estados, 789, São Paulo - SP', 'Frutas Importadas e Nacionais'),
    ('Orgânicos da Terra', '22.333.444/0001-99', '(11) 97766-5544', 'contato@organicosterra.com', 'Rua Verde, 456, São Paulo - SP', 'Produtos Orgânicos Certificados')
ON CONFLICT DO NOTHING;

-- ============================================
-- INSERIR PRODUTOS DE EXEMPLO
-- ============================================

-- Frutas
INSERT INTO produto (nome, preco, quantidade, dt_validade, id_categoria) VALUES
    ('Maçã Fuji', 6.99, 100, CURRENT_DATE + INTERVAL '7 days', (SELECT id_categoria FROM categoria WHERE nome = 'Frutas' LIMIT 1)),
    ('Banana Prata', 4.50, 150, CURRENT_DATE + INTERVAL '5 days', (SELECT id_categoria FROM categoria WHERE nome = 'Frutas' LIMIT 1)),
    ('Laranja Pera', 3.99, 120, CURRENT_DATE + INTERVAL '10 days', (SELECT id_categoria FROM categoria WHERE nome = 'Frutas' LIMIT 1)),
    ('Morango', 12.90, 50, CURRENT_DATE + INTERVAL '3 days', (SELECT id_categoria FROM categoria WHERE nome = 'Frutas' LIMIT 1)),
    ('Melancia', 8.50, 30, CURRENT_DATE + INTERVAL '7 days', (SELECT id_categoria FROM categoria WHERE nome = 'Frutas' LIMIT 1))
ON CONFLICT DO NOTHING;

-- Verduras
INSERT INTO produto (nome, preco, quantidade, dt_validade, id_categoria) VALUES
    ('Alface Americana', 3.50, 80, CURRENT_DATE + INTERVAL '4 days', (SELECT id_categoria FROM categoria WHERE nome = 'Verduras' LIMIT 1)),
    ('Rúcula', 4.20, 60, CURRENT_DATE + INTERVAL '3 days', (SELECT id_categoria FROM categoria WHERE nome = 'Verduras' LIMIT 1)),
    ('Couve', 2.80, 70, CURRENT_DATE + INTERVAL '5 days', (SELECT id_categoria FROM categoria WHERE nome = 'Verduras' LIMIT 1)),
    ('Espinafre', 5.50, 40, CURRENT_DATE + INTERVAL '3 days', (SELECT id_categoria FROM categoria WHERE nome = 'Verduras' LIMIT 1))
ON CONFLICT DO NOTHING;

-- Legumes
INSERT INTO produto (nome, preco, quantidade, dt_validade, id_categoria) VALUES
    ('Tomate', 5.90, 100, CURRENT_DATE + INTERVAL '7 days', (SELECT id_categoria FROM categoria WHERE nome = 'Legumes' LIMIT 1)),
    ('Cenoura', 3.20, 90, CURRENT_DATE + INTERVAL '10 days', (SELECT id_categoria FROM categoria WHERE nome = 'Legumes' LIMIT 1)),
    ('Pepino', 4.50, 60, CURRENT_DATE + INTERVAL '6 days', (SELECT id_categoria FROM categoria WHERE nome = 'Legumes' LIMIT 1)),
    ('Abobrinha', 6.80, 50, CURRENT_DATE + INTERVAL '8 days', (SELECT id_categoria FROM categoria WHERE nome = 'Legumes' LIMIT 1))
ON CONFLICT DO NOTHING;

-- Temperos
INSERT INTO produto (nome, preco, quantidade, dt_validade, id_categoria) VALUES
    ('Salsinha', 2.50, 100, CURRENT_DATE + INTERVAL '5 days', (SELECT id_categoria FROM categoria WHERE nome = 'Temperos' LIMIT 1)),
    ('Cebolinha', 2.50, 100, CURRENT_DATE + INTERVAL '5 days', (SELECT id_categoria FROM categoria WHERE nome = 'Temperos' LIMIT 1)),
    ('Alho', 18.90, 80, CURRENT_DATE + INTERVAL '15 days', (SELECT id_categoria FROM categoria WHERE nome = 'Temperos' LIMIT 1)),
    ('Gengibre', 12.50, 40, CURRENT_DATE + INTERVAL '12 days', (SELECT id_categoria FROM categoria WHERE nome = 'Temperos' LIMIT 1))
ON CONFLICT DO NOTHING;

-- ============================================
-- CRIAR ESTOQUE PARA OS PRODUTOS
-- ============================================

INSERT INTO estoque (id_produto, quant_produtos)
SELECT id_produto, quantidade
FROM produto
ON CONFLICT DO NOTHING;

-- ============================================
-- CRIAR ÍNDICES PARA PERFORMANCE
-- ============================================

-- Produto
CREATE INDEX IF NOT EXISTS idx_produto_nome ON produto(nome);
CREATE INDEX IF NOT EXISTS idx_produto_nome_gin ON produto USING gin(nome gin_trgm_ops);
CREATE INDEX IF NOT EXISTS idx_produto_categoria ON produto(id_categoria);
CREATE INDEX IF NOT EXISTS idx_produto_validade ON produto(dt_validade);

-- Cliente
CREATE INDEX IF NOT EXISTS idx_cliente_cpf ON cliente(cpf);
CREATE INDEX IF NOT EXISTS idx_cliente_email ON cliente(email);

-- Pedido
CREATE INDEX IF NOT EXISTS idx_pedido_cliente ON pedido(id_cliente);
CREATE INDEX IF NOT EXISTS idx_pedido_status ON pedido(status_pedido);

-- ItemPedido
CREATE INDEX IF NOT EXISTS idx_item_pedido_pedido ON item_pedido(id_pedido);

-- Carrinho
CREATE INDEX IF NOT EXISTS idx_carrinho_cliente ON carrinho(id_cliente);

-- Estoque
CREATE INDEX IF NOT EXISTS idx_estoque_produto ON estoque(id_produto);

-- Fornecedor
CREATE INDEX IF NOT EXISTS idx_fornecedor_cnpj ON fornecedor(cnpj);

-- ============================================
-- ADICIONAR COMENTÁRIOS NAS TABELAS
-- ============================================

COMMENT ON TABLE categoria IS 'Categorias de produtos do hortifruti';
COMMENT ON TABLE fornecedor IS 'Fornecedores de produtos';
COMMENT ON TABLE produto IS 'Produtos disponíveis para venda';
COMMENT ON TABLE estoque IS 'Controle de estoque dos produtos';
COMMENT ON TABLE usuario IS 'Usuários do sistema (superclasse)';
COMMENT ON TABLE cliente IS 'Clientes cadastrados no sistema';
COMMENT ON TABLE carrinho IS 'Carrinhos de compras dos clientes';
COMMENT ON TABLE pedido IS 'Pedidos realizados pelos clientes';
COMMENT ON TABLE item_pedido IS 'Itens de cada pedido';
COMMENT ON TABLE pagamento IS 'Informações de pagamento dos pedidos';
COMMENT ON TABLE entrega IS 'Informações de entrega dos pedidos';

-- ============================================
-- ATUALIZAR ESTATÍSTICAS
-- ============================================

ANALYZE;

-- ============================================
-- LOG DE SUCESSO
-- ============================================

DO $$
DECLARE
    v_categorias INTEGER;
    v_fornecedores INTEGER;
    v_produtos INTEGER;
BEGIN
    SELECT COUNT(*) INTO v_categorias FROM categoria;
    SELECT COUNT(*) INTO v_fornecedores FROM fornecedor;
    SELECT COUNT(*) INTO v_produtos FROM produto;
    
    RAISE NOTICE '';
    RAISE NOTICE '================================================';
    RAISE NOTICE 'DADOS INICIAIS INSERIDOS COM SUCESSO!';
    RAISE NOTICE '================================================';
    RAISE NOTICE '';
    RAISE NOTICE 'Categorias inseridas: %', v_categorias;
    RAISE NOTICE 'Fornecedores inseridos: %', v_fornecedores;
    RAISE NOTICE 'Produtos inseridos: %', v_produtos;
    RAISE NOTICE '';
    RAISE NOTICE 'Índices criados para otimização de performance';
    RAISE NOTICE 'Estatísticas atualizadas';
    RAISE NOTICE '';
    RAISE NOTICE '================================================';
    RAISE NOTICE 'Sistema pronto para uso!';
    RAISE NOTICE '================================================';
    RAISE NOTICE '';
END $$;
