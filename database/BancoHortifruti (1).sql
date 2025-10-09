-- 1. Remove todas as FOREIGN KEYS do banco de dados
DECLARE @sql NVARCHAR(MAX) = N'';

SELECT @sql += 'ALTER TABLE ' + QUOTENAME(OBJECT_SCHEMA_NAME(parent_object_id)) + '.' + QUOTENAME(OBJECT_NAME(parent_object_id)) 
    + ' DROP CONSTRAINT ' + QUOTENAME(name) + ';' + CHAR(13)
FROM sys.foreign_keys;

EXEC sp_executesql @sql;


-- 2. Drop das tabelas na ordem correta (se existirem)
DROP TABLE IF EXISTS 
    Produto_Carrinho,
    Produto_Fornecedor,
    ItemPedido,
    Entrega,
    Pagamento,
    Pedido,
    Carrinho,
    Usuario,
    Cliente,
    Estoque,
    Produto,
    Categoria,
    Fornecedor;


-- 3. Cria��o das tabelas

-- Tabela Categoria
CREATE TABLE Categoria (
    CategoriaID INT PRIMARY KEY IDENTITY(1,1),
    Nome NVARCHAR(100) NOT NULL
);

-- Tabela Fornecedor
CREATE TABLE Fornecedor (
    FornecedorID INT PRIMARY KEY IDENTITY(1,1),
    Nome NVARCHAR(100) NOT NULL,
    CNPJ NVARCHAR(18) NOT NULL,
    Telefone NVARCHAR(20) NULL
);

-- Tabela Produto
CREATE TABLE Produto (
    ProdutoID INT PRIMARY KEY IDENTITY(1,1),
    Nome NVARCHAR(100) NOT NULL,
    Descricao NVARCHAR(255) NULL,
    Preco DECIMAL(10,2) NOT NULL,
    CategoriaID INT NOT NULL,
    FOREIGN KEY (CategoriaID) REFERENCES Categoria(CategoriaID)
);

-- Tabela Estoque
CREATE TABLE Estoque (
    EstoqueID INT PRIMARY KEY IDENTITY(1,1),
    ProdutoID INT NOT NULL,
    Quantidade INT NOT NULL,
    FOREIGN KEY (ProdutoID) REFERENCES Produto(ProdutoID)
);

-- Tabela Cliente
CREATE TABLE Cliente (
    ClienteID INT PRIMARY KEY IDENTITY(1,1),
    Nome NVARCHAR(100) NOT NULL,
    CPF NVARCHAR(14) NOT NULL,
    Endereco NVARCHAR(255) NULL,
    Telefone NVARCHAR(20) NULL
);

-- Tabela Usuario (para login)
CREATE TABLE Usuario (
    UsuarioID INT PRIMARY KEY IDENTITY(1,1),
    Nome NVARCHAR(100) NOT NULL,
    Email NVARCHAR(100) NOT NULL UNIQUE,
    Senha NVARCHAR(100) NOT NULL
);

-- Tabela Carrinho
CREATE TABLE Carrinho (
    CarrinhoID INT PRIMARY KEY IDENTITY(1,1),
    ClienteID INT NOT NULL,
    DataCriacao DATE NOT NULL,
    FOREIGN KEY (ClienteID) REFERENCES Cliente(ClienteID)
);

-- Tabela Produto_Carrinho (associa��o de produtos no carrinho)
CREATE TABLE Produto_Carrinho (
    ProdutoID INT NOT NULL,
    CarrinhoID INT NOT NULL,
    Quantidade INT NOT NULL,
    PRIMARY KEY (ProdutoID, CarrinhoID),
    FOREIGN KEY (ProdutoID) REFERENCES Produto(ProdutoID),
    FOREIGN KEY (CarrinhoID) REFERENCES Carrinho(CarrinhoID)
);

-- Tabela Pedido
CREATE TABLE Pedido (
    PedidoID INT PRIMARY KEY IDENTITY(1,1),
    ClienteID INT NOT NULL,
    DataPedido DATE NOT NULL,
    Status NVARCHAR(50) NOT NULL,
    FOREIGN KEY (ClienteID) REFERENCES Cliente(ClienteID)
);

-- Tabela ItemPedido (itens do pedido)
CREATE TABLE ItemPedido (
    ItemPedidoID INT PRIMARY KEY IDENTITY(1,1),
    PedidoID INT NOT NULL,
    ProdutoID INT NOT NULL,
    Quantidade INT NOT NULL,
    PrecoUnitario DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (PedidoID) REFERENCES Pedido(PedidoID),
    FOREIGN KEY (ProdutoID) REFERENCES Produto(ProdutoID)
);

-- Tabela Entrega
CREATE TABLE Entrega (
    EntregaID INT PRIMARY KEY IDENTITY(1,1),
    PedidoID INT NOT NULL,
    DataEntrega DATE NULL,
    Status NVARCHAR(50) NOT NULL,
    FOREIGN KEY (PedidoID) REFERENCES Pedido(PedidoID)
);

-- Tabela Pagamento
CREATE TABLE Pagamento (
    PagamentoID INT PRIMARY KEY IDENTITY(1,1),
    PedidoID INT NOT NULL,
    Valor DECIMAL(10,2) NOT NULL,
    DataPagamento DATE NOT NULL,
    MetodoPagamento NVARCHAR(50) NOT NULL,
    FOREIGN KEY (PedidoID) REFERENCES Pedido(PedidoID)
);

-- Tabela Produto_Fornecedor (associa��o de produto com fornecedor)
CREATE TABLE Produto_Fornecedor (
    ProdutoID INT NOT NULL,
    FornecedorID INT NOT NULL,
    PRIMARY KEY (ProdutoID, FornecedorID),
    FOREIGN KEY (ProdutoID) REFERENCES Produto(ProdutoID),
    FOREIGN KEY (FornecedorID) REFERENCES Fornecedor(FornecedorID)
);
SELECT * 
FROM INFORMATION_SCHEMA.TABLES 
WHERE TABLE_TYPE = 'BASE TABLE';

