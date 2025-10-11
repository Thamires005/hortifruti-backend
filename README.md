# Sistema de Gerenciamento de Hortifruti

Sistema completo de gerenciamento de hortifruti desenvolvido com **Spring Boot 3.x**, **Angular v20** e **PostgreSQL 16**, com autenticação via **Keycloak**.

![Java](https://img.shields.io/badge/Java-21-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-green)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-blue)
![Angular](https://img.shields.io/badge/Angular-20-red)
![Docker](https://img.shields.io/badge/Docker-Compose-blue)

## Índice

- [Características](#características)
- [Arquitetura](#arquitetura)
- [Tecnologias](#tecnologias)
- [Pré-requisitos](#pré-requisitos)
- [Instalação](#instalação)
- [Configuração](#configuração)
- [Execução](#execução)
- [Documentação da API](#documentação-da-api)
- [Estrutura do Projeto](#estrutura-do-projeto)
- [Funcionalidades](#funcionalidades)
- [Segurança](#segurança)
- [Contribuição](#contribuição)

---

## Características

### Backend
- API REST completa e documentada
- Autenticação e autorização via OAuth 2.0 (Keycloak)
- Arquitetura em camadas (Controller → Service → Repository)
- Validação de dados com Bean Validation
- Tratamento global de exceções
- Transações gerenciadas
- Clean Code e SOLID principles
- Performance otimizada (sem N+1 queries)

### Frontend
- Interface moderna e responsiva
- Autenticação integrada com Keycloak
- Lazy Loading de módulos
- Formulários reativos
- Guards para rotas protegidas
- Interceptors para tokens

### Funcionalidades de Negócio
- Gerenciamento de categorias e produtos
- Controle de estoque automatizado
- Cadastro de clientes e fornecedores
- Carrinho de compras
- Processamento de pedidos
- Gestão de pagamentos e entregas
- Relatórios de estoque baixo
- Alertas de produtos próximos ao vencimento

---

## Arquitetura

### Arquitetura Geral

```
┌─────────────────┐
│  Angular v20    │ ◄──── Frontend (Porta 4200)
│  (Frontend)     │
└────────┬────────┘
         │ HTTP/REST
         │ + Bearer Token
┌────────▼────────┐
│  Spring Boot    │ ◄──── Backend (Porta 8080)
│  (Backend)      │
└────────┬────────┘
         │ JDBC
┌────────▼────────┐
│  PostgreSQL 16  │ ◄──── Database (Porta 5432)
└─────────────────┘

┌─────────────────┐
│   Keycloak      │ ◄──── Auth Server (Porta 8180)
│  (OAuth 2.0)    │
└─────────────────┘
```

### Arquitetura do Backend

```
br.unip.ads.pim.meuhortifruti
├── config/              → Configurações (Security, CORS, Jackson)
├── controller/          → Endpoints REST
├── dto/                 → Data Transfer Objects
├── entity/              → Entidades JPA
├── repository/          → Repositórios Spring Data
├── service/             → Lógica de negócio
├── security/            → Configurações de segurança
└── exception/           → Tratamento de exceções
```

### Padrões Implementados

- **DTO Pattern**: Separação entre entidades e dados transferidos
- **Repository Pattern**: Abstração do acesso a dados
- **Service Layer**: Encapsulamento da lógica de negócio
- **Exception Handler**: Tratamento centralizado de erros
- **Builder Pattern**: Construção de objetos complexos

---

## Tecnologias

### Backend
| Tecnologia | Versão | Descrição |
|------------|--------|-----------|
| Java | 21 | Linguagem de programação |
| Spring Boot | 3.x | Framework de aplicação |
| Spring Data JPA | 3.x | Persistência de dados |
| Spring Security | 6.x | Segurança e autenticação |
| PostgreSQL | 16 | Banco de dados |
| Keycloak | Latest | Servidor de autenticação |
| Lombok | Latest | Redução de boilerplate |
| Jackson | 2.x | Serialização JSON |

### Frontend
| Tecnologia | Versão | Descrição |
|------------|--------|-----------|
| Angular | 20 | Framework frontend |
| TypeScript | 5.x | Linguagem tipada |
| RxJS | 7.x | Programação reativa |
| Angular Material | 20 | Componentes UI |
| Keycloak Angular | Latest | Integração Keycloak |

### DevOps
| Tecnologia | Versão | Descrição |
|------------|--------|-----------|
| Docker | Latest | Containerização |
| Docker Compose | Latest | Orquestração de containers |
| Maven | 3.9+ | Gerenciamento de dependências |

---

## Pré-requisitos

### Desenvolvimento Local
- **Java JDK 21** ou superior
- **Node.js 18+** e **npm 9+**
- **PostgreSQL 16**
- **Maven 3.9+**
- **Docker** e **Docker Compose** (opcional)

### Variáveis de Ambiente
```bash
# Database
DB_HOST=localhost
DB_PORT=5432
DB_NAME=hortifruti_db
DB_USER=postgres
DB_PASSWORD=postgres

# Keycloak
KEYCLOAK_URL=http://localhost:8180
KEYCLOAK_REALM=hortifruti
KEYCLOAK_CLIENT_ID=hortifruti-client
KEYCLOAK_CLIENT_SECRET=your-secret
```

---

## Instalação

### Opção 1: Docker Compose (Recomendado)

```bash
# Clone o repositório
git clone https://github.com/seu-usuario/hortifruti-system.git
cd hortifruti-system

# Inicie todos os serviços
docker-compose up -d

# Aguarde os containers iniciarem (pode levar 2-3 minutos)
docker-compose logs -f
```

### Opção 2: Instalação Manual

#### 1. Banco de Dados

```bash
# Instalar PostgreSQL 16
# Criar banco de dados
psql -U postgres
CREATE DATABASE hortifruti_db;
\q
```

#### 2. Backend

```bash
cd hortifruti-backend

# Configurar application.yml
cp src/main/resources/application.yml.example src/main/resources/application.yml
# Edite com suas configurações

# Compilar
mvn clean install

# Executar
mvn spring-boot:run
```

#### 3. Keycloak

```bash
# Baixar Keycloak
wget https://github.com/keycloak/keycloak/releases/download/23.0.0/keycloak-23.0.0.zip
unzip keycloak-23.0.0.zip
cd keycloak-23.0.0

# Iniciar
bin/kc.sh start-dev --http-port=8180
```

#### 4. Frontend

```bash
cd hortifruti-frontend

# Instalar dependências
npm install

# Executar em modo desenvolvimento
ng serve
```

---

## Configuração

### Backend - application.yml

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/hortifruti_db
    username: postgres
    password: postgres
  
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
    properties:
      hibernate:
        format_sql: true
        dialect: org.hibernate.dialect.PostgreSQLDialect

  security:
    oauth2:
      resourceserver:
        jwt:
          issuer-uri: http://localhost:8180/realms/hortifruti

server:
  port: 8080

keycloak:
  realm: hortifruti
  auth-server-url: http://localhost:8180
  resource: hortifruti-client
  credentials:
    secret: your-client-secret
```

### Frontend - environment.ts

```typescript
export const environment = {
  production: false,
  apiUrl: 'http://localhost:8080/v1',
  keycloak: {
    url: 'http://localhost:8180',
    realm: 'hortifruti',
    clientId: 'hortifruti-client'
  }
};
```

### Keycloak

1. Acesse `http://localhost:8180`
2. Login: `admin` / `admin`
3. Criar Realm: `hortifruti`
4. Criar Client: `hortifruti-client`
5. Criar Roles:
   - `ROLE_ADMIN`
   - `ROLE_CLIENTE`
6. Criar usuários de teste

---

## Execução

### Com Docker Compose

```bash
# Iniciar todos os serviços
docker-compose up -d

# Ver logs
docker-compose logs -f

# Parar serviços
docker-compose down

# Parar e remover volumes (limpa banco)
docker-compose down -v
```

### Sem Docker

```bash
# Terminal 1 - PostgreSQL
# (já deve estar rodando)

# Terminal 2 - Keycloak
cd keycloak-23.0.0
bin/kc.sh start-dev --http-port=8180

# Terminal 3 - Backend
cd hortifruti-backend
mvn spring-boot:run

# Terminal 4 - Frontend
cd hortifruti-frontend
ng serve
```

### Acessar Aplicação

| Serviço | URL | Credenciais |
|---------|-----|-------------|
| Frontend | http://localhost:4200 | - |
| Backend API | http://localhost:8080 | Token JWT |
| Keycloak | http://localhost:8180 | admin / admin |
| PostgreSQL | localhost:5432 | postgres / postgres |

---

## Documentação da API

A documentação completa da API está disponível em:

**[API_DOCUMENTATION.md](./API_DOCUMENTATION.md)**

### Endpoints Principais

| Recurso | Endpoint | Métodos |
|---------|----------|---------|
| Categorias | `/v1/categorias` | GET, POST, PUT, DELETE |
| Fornecedores | `/v1/fornecedores` | GET, POST, PUT, DELETE |
| Produtos | `/v1/produtos` | GET, POST, PUT, DELETE |
| Estoque | `/v1/estoque` | GET, POST, PUT, PATCH, DELETE |
| Clientes | `/v1/clientes` | GET, POST, PUT, DELETE |
| Carrinho | `/v1/carrinho` | GET, POST, PUT, DELETE |
| Pedidos | `/v1/pedidos` | GET, POST, PATCH, DELETE |

---

## Estrutura do Projeto

```
hortifruti-system/
│
├── hortifruti-backend/               # Backend Spring Boot
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/
│   │   │   │   └── br/unip/ads/pim/meuhortifruti/
│   │   │   │       ├── config/
│   │   │   │       ├── controller/
│   │   │   │       ├── dto/
│   │   │   │       ├── entity/
│   │   │   │       ├── exception/
│   │   │   │       ├── repository/
│   │   │   │       ├── security/
│   │   │   │       └── service/
│   │   │   └── resources/
│   │   │       ├── application.yml
│   │   │       └── data.sql
│   │   └── test/
│   ├── pom.xml
│   └── Dockerfile
│
├── hortifruti-frontend/              # Frontend Angular
│   ├── src/
│   │   ├── app/
│   │   │   ├── core/
│   │   │   ├── shared/
│   │   │   ├── features/
│   │   │   │   ├── autenticacao/
│   │   │   │   ├── cliente/
│   │   │   │   ├── produto/
│   │   │   │   ├── carrinho/
│   │   │   │   ├── pedido/
│   │   │   │   └── ...
│   │   │   └── layout/
│   │   ├── assets/
│   │   └── environments/
│   ├── angular.json
│   ├── package.json
│   └── Dockerfile
│
├── docker-compose.yml               # Orquestração Docker
├── API_DOCUMENTATION.md             # Documentação da API
└── README.md                        # Este arquivo
```

---

## Funcionalidades

### Para Clientes (ROLE_CLIENTE)
- Cadastro e autenticação
- Visualização de produtos e categorias
- Busca e filtro de produtos
- Adição de produtos ao carrinho
- Finalização de pedidos
- Acompanhamento de pedidos
- Histórico de compras
- Atualização de perfil

### Para Administradores (ROLE_ADMIN)
- Gerenciamento de categorias
- Cadastro e gestão de produtos
- Controle de estoque
- Gestão de fornecedores
- Visualização de todos os pedidos
- Atualização de status (pedido/pagamento/entrega)
- Relatórios de estoque baixo
- Alertas de vencimento de produtos
- Visualização de clientes

---

## Segurança

### Autenticação
- OAuth 2.0 via Keycloak
- JWT tokens com expiração configurável
- Refresh tokens para renovação

### Autorização
- Role-based access control (RBAC)
- Endpoints protegidos por roles
- Validação de permissões no backend

### Proteções Implementadas
- CORS configurado
- SQL Injection (via JPA)
- XSS (sanitização de inputs)
- CSRF token
- Validação de dados em todas as camadas
- Senhas nunca retornadas em responses

---

## Testes

### Backend
```bash
cd hortifruti-backend
mvn test
```

### Frontend
```bash
cd hortifruti-frontend
npm test
```

---

## Performance

### Otimizações Implementadas
- Batch queries (findAllById)
- Lazy loading de relacionamentos
- Índices em colunas de busca
- Connection pooling
- Cache de segundo nível (quando aplicável)
- Transações otimizadas

### Métricas Esperadas
- Tempo de resposta médio: < 200ms
- Suporte para 100+ requisições concorrentes
- Zero N+1 queries

---

## Solução de Problemas

### Problema: Erro ao conectar no banco
```bash
# Verificar se PostgreSQL está rodando
docker ps | grep postgres

# Ver logs do container
docker logs hortifruti-postgres
```

### Problema: Erro de autenticação
```bash
# Verificar se Keycloak está rodando
curl http://localhost:8180/health

# Reiniciar Keycloak
docker-compose restart keycloak
```

### Problema: Backend não inicia
```bash
# Verificar dependências
mvn dependency:tree

# Limpar e recompilar
mvn clean install
```

---

### Padrões de Código
- Seguir Clean Code principles
- Manter cobertura de testes > 80%
- Documentar métodos públicos
- Validar todos os inputs
- Usar nomes descritivos em português

---

## Licença

Este projeto foi desenvolvido para fins acadêmicos como parte do PIM (Projeto Integrado Multidisciplinar) da UNIP.

---

## Autores

- **David** - [GitHub]()
- **Júlia** - [GitHub]()
- **Letícia** - [GitHub]()
- **Maria Eduarda** - [GitHub]()
- **Thamires** - [GitHub]()

---

## Suporte

Para dúvidas ou problemas:
- Abra uma issue no GitHub
- Email: suporte@meuhortifruti.com.br

---

## Agradecimentos



---

**Última atualização:** Outubro 2025
