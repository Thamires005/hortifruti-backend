# Sistema de Gerenciamento de Hortifruti

Sistema completo de gerenciamento de hortifruti desenvolvido com **Spring Boot 3.x**, **Angular v20** e **PostgreSQL 16**, com autenticação via **Keycloak**.

## Tecnologias

### Backend
- Java 21
- Spring Boot 3.x
- Spring Data JPA
- Spring Security com OAuth2
- PostgreSQL 16
- Keycloak 23.0
- Lombok
- Maven

### Infraestrutura
- Docker & Docker Compose
- PostgreSQL (2 instâncias: app + keycloak)
- Volumes Docker para persistência

## Pré-requisitos

- Docker e Docker Compose instalados
- 4GB RAM disponível
- Portas disponíveis: 8080, 8180, 5432, 5433, 5050

## Instalação e Execução

### 1. Clone o repositório
```bash
git clone https://github.com/seu-usuario/hortifruti-backend.git
cd hortifruti-backend
```

### 2. Configure as variáveis de ambiente
O arquivo `.env` já está configurado. Você só precisa ajustar o `KEYCLOAK_CLIENT_SECRET` após criar o client no Keycloak.

### 3. Inicie os containers
```bash
docker-compose up -d
```

Aguarde cerca de 2 minutos para todos os serviços subirem completamente.

### 4. Verifique o status
```bash
docker-compose ps
```

Todos os containers devem estar com status "healthy" ou "running".

### 5. Configure o Keycloak

**IMPORTANTE:** Siga as instruções detalhadas no arquivo `CONFIGURACAO_KEYCLOAK.md` para:
- Criar o realm "hortifruti-realm"
- Criar o client "hortifruti-backend"
- Obter o client secret
- Criar as roles ROLE_ADMIN e ROLE_CLIENTE
- Criar usuários de teste

Após obter o client secret, atualize o arquivo `.env`:
```bash
KEYCLOAK_CLIENT_SECRET=seu_client_secret_aqui
```

E reconstrua o backend:
```bash
docker-compose up -d --build backend
```

## Acessar os Serviços

| Serviço | URL | Credenciais |
|---------|-----|-------------|
| Backend API | http://localhost:8080 | Token JWT necessário |
| Keycloak Admin | http://localhost:8180 | admin / admin |
| PostgreSQL App | localhost:5432 | hortifruti_user / hortifruti_pass |
| PostgreSQL Keycloak | localhost:5433 | keycloak_user / keycloak_pass |
| PgAdmin | http://localhost:5050 | admin@hortifruti.com / admin |

## Estrutura do Projeto

```
hortifruti-backend/
├── src/main/java/br/unip/ads/pim/meuhortifruti/
│   ├── config/              - Configurações (Security, CORS, Keycloak)
│   ├── controller/          - Endpoints REST
│   ├── dto/                 - Data Transfer Objects
│   ├── entity/              - Entidades JPA
│   ├── exception/           - Tratamento de exceções
│   ├── repository/          - Repositórios Spring Data
│   ├── security/            - Segurança OAuth2
│   └── service/             - Lógica de negócio
├── src/main/resources/
│   └── application.yml      - Configurações da aplicação
├── init-scripts/
│   ├── 01-init-database.sql - Schema inicial
│   └── 02-dados-iniciais.sql - Dados de exemplo
├── docker-compose.yml       - Orquestração dos containers
├── Dockerfile               - Imagem do backend
├── .env                     - Variáveis de ambiente
└── CONFIGURACAO_KEYCLOAK.md - Guia de configuração do Keycloak
```

## Persistência de Dados

### Volumes Docker Configurados

O sistema utiliza volumes Docker nomeados para garantir persistência:

```yaml
volumes:
  postgres_data:              # Dados da aplicação
  postgres_keycloak_data:     # Dados do Keycloak
  keycloak_data:              # Configurações do Keycloak
  pgadmin_data:               # Preferências do PgAdmin
```

### Verificar Volumes
```bash
docker volume ls
```

Você deve ver:
- hortifruti-postgres-data
- hortifruti-postgres-keycloak-data
- hortifruti-keycloak-data
- hortifruti-pgadmin-data

### Testar Persistência

1. Crie dados no sistema (categorias, produtos, etc)
2. Pare os containers: `docker-compose down`
3. Suba novamente: `docker-compose up -d`
4. Verifique se os dados ainda existem

### Limpar Todos os Dados
```bash
docker-compose down -v
```
Este comando remove os volumes e apaga todos os dados.

## API Endpoints Implementados

### Categorias
```
GET    /api/v1/categorias           - Listar todas
GET    /api/v1/categorias/{id}      - Buscar por ID
POST   /api/v1/categorias           - Criar nova
PUT    /api/v1/categorias/{id}      - Atualizar
DELETE /api/v1/categorias/{id}      - Excluir
```

Autenticação: Bearer Token (JWT)
Roles: ROLE_ADMIN para POST/PUT/DELETE

## Comandos Úteis

### Ver logs de todos os serviços
```bash
docker-compose logs -f
```

### Ver logs de um serviço específico
```bash
docker-compose logs -f backend
docker-compose logs -f keycloak
docker-compose logs -f postgres
```

### Reiniciar um serviço
```bash
docker-compose restart backend
```

### Reconstruir e reiniciar o backend
```bash
docker-compose up -d --build backend
```

### Parar todos os serviços
```bash
docker-compose down
```

### Parar e remover volumes (limpa dados)
```bash
docker-compose down -v
```

### Acessar container do banco de dados
```bash
docker exec -it hortifruti-postgres psql -U hortifruti_user -d hortifruti_db
```

## Solução de Problemas

### Backend não inicia
1. Verifique se o PostgreSQL está saudável: `docker-compose ps`
2. Verifique se o Keycloak está saudável
3. Veja os logs: `docker-compose logs backend`

### Keycloak não inicia
1. Aguarde até 2 minutos na primeira inicialização
2. Veja os logs: `docker-compose logs keycloak`
3. Verifique se o postgres-keycloak está rodando

### Erro de autenticação
1. Verifique se o realm foi criado no Keycloak
2. Verifique se o client secret no .env está correto
3. Reconstrua o backend: `docker-compose up -d --build backend`

### Dados não persistem
1. Verifique se os volumes existem: `docker volume ls`
2. Não use `docker-compose down -v` (isso apaga os volumes)
3. Use apenas `docker-compose down` para parar sem perder dados

## Configurações de Segurança

### Roles Configuradas
- **ROLE_ADMIN**: Acesso completo ao sistema
- **ROLE_CLIENTE**: Acesso limitado (visualização e carrinho)

### Endpoints Públicos
- GET /api/v1/categorias
- GET /api/v1/produtos

### Endpoints Protegidos (ROLE_ADMIN)
- POST, PUT, DELETE em /api/v1/categorias
- POST, PUT, DELETE em /api/v1/produtos
- Todos os endpoints de /api/v1/estoque
- Todos os endpoints de /api/v1/fornecedores

## Desenvolvimento

### Adicionar novas entidades
1. Criar Entity em `entity/`
2. Criar Repository em `repository/`
3. Criar DTOs em `dto/`
4. Criar Service em `service/`
5. Criar Controller em `controller/`

### Executar localmente sem Docker
1. Instale PostgreSQL 16 e Keycloak localmente
2. Configure application.yml com localhost
3. Execute: `mvn spring-boot:run`

## Próximos Passos

- Implementar CRUD completo de Produtos
- Implementar CRUD de Fornecedores
- Implementar gestão de Estoque
- Implementar CRUD de Clientes
- Implementar Carrinho de Compras
- Implementar Pedidos e Pagamentos
- Implementar Entregas
- Desenvolver Frontend Angular

## Autores

Projeto desenvolvido como PIM (Projeto Integrado Multidisciplinar) - UNIP

---

**Última atualização:** Outubro 2025

Para mais informações sobre configuração do Keycloak, consulte `CONFIGURACAO_KEYCLOAK.md`
