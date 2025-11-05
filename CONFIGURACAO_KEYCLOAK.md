# CONFIGURAÇÃO INICIAL DO KEYCLOAK - HORTIFRUTI

## IMPORTANTE: Execute estes passos após subir os containers

### 1. Acessar Console Administrativo do Keycloak
- URL: http://localhost:8180
- Usuario: admin
- Senha: admin

### 2. Criar Realm
1. Clique no dropdown "master" no canto superior esquerdo
2. Clique em "Create realm"
3. Nome do realm: hortifruti-realm
4. Enabled: ON
5. Clique em "Create"

### 3. Criar Client
1. No menu lateral, clique em "Clients"
2. Clique em "Create client"
3. Configure:
   - Client type: OpenID Connect
   - Client ID: hortifruti-backend
   - Clique em "Next"
4. Configure Capability config:
   - Client authentication: ON
   - Authorization: OFF
   - Standard flow: ON
   - Direct access grants: ON
   - Clique em "Next"
5. Configure Login settings:
   - Root URL: http://localhost:8080
   - Valid redirect URIs: http://localhost:8080/*
   - Web origins: http://localhost:8080
   - Clique em "Save"

### 4. Obter Client Secret
1. Ainda na tela do client "hortifruti-backend"
2. Vá para a aba "Credentials"
3. Copie o "Client secret"
4. Atualize o valor de KEYCLOAK_CLIENT_SECRET no arquivo .env com este secret

### 5. Criar Roles
1. No menu lateral, clique em "Realm roles"
2. Clique em "Create role"
3. Crie as seguintes roles:
   - ROLE_ADMIN (Description: Administrador do sistema)
   - ROLE_CLIENTE (Description: Cliente do sistema)
4. Clique em "Save" para cada role

### 6. Criar Client Roles
1. No menu lateral, clique em "Clients"
2. Selecione "hortifruti-backend"
3. Vá para a aba "Roles"
4. Clique em "Create role"
5. Crie as mesmas roles:
   - ROLE_ADMIN
   - ROLE_CLIENTE

### 7. Criar Usuario Administrador
1. No menu lateral, clique em "Users"
2. Clique em "Add user"
3. Configure:
   - Username: admin
   - Email: admin@hortifruti.com
   - First name: Administrador
   - Last name: Sistema
   - Email verified: ON
   - Enabled: ON
   - Clique em "Create"
4. Após criar, vá para a aba "Credentials"
5. Clique em "Set password"
6. Digite a senha: admin123
7. Temporary: OFF
8. Clique em "Save"
9. Vá para a aba "Role mappings"
10. Em "Realm roles", adicione: ROLE_ADMIN
11. Em "Client roles", selecione "hortifruti-backend" e adicione: ROLE_ADMIN

### 8. Criar Usuario Cliente de Teste
1. No menu lateral, clique em "Users"
2. Clique em "Add user"
3. Configure:
   - Username: cliente
   - Email: cliente@hortifruti.com
   - First name: Cliente
   - Last name: Teste
   - Email verified: ON
   - Enabled: ON
   - Clique em "Create"
4. Após criar, vá para a aba "Credentials"
5. Clique em "Set password"
6. Digite a senha: cliente123
7. Temporary: OFF
8. Clique em "Save"
9. Vá para a aba "Role mappings"
10. Em "Realm roles", adicione: ROLE_CLIENTE
11. Em "Client roles", selecione "hortifruti-backend" e adicione: ROLE_CLIENTE

### 9. Configurar Token Settings
1. No menu lateral, clique em "Clients"
2. Selecione "hortifruti-backend"
3. Vá para a aba "Advanced"
4. Configure:
   - Access Token Lifespan: 30 minutes
   - Clique em "Save"

### 10. Verificar Configuração
1. Teste o endpoint de token:
```bash
curl -X POST "http://localhost:8180/realms/hortifruti-realm/protocol/openid-connect/token" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "client_id=hortifruti-backend" \
  -d "client_secret=SEU_CLIENT_SECRET_AQUI" \
  -d "grant_type=password" \
  -d "username=admin" \
  -d "password=admin123"
```

## VERIFICAÇÃO DE PERSISTÊNCIA

Após configurar tudo acima:

1. Pare os containers:
```bash
docker-compose down
```

2. Suba novamente:
```bash
docker-compose up -d
```

3. Acesse o Keycloak novamente e verifique se:
   - O realm "hortifruti-realm" ainda existe
   - O client "hortifruti-backend" está configurado
   - As roles estão criadas
   - Os usuarios existem

Se tudo estiver preservado, a persistência está funcionando corretamente.

## TROUBLESHOOTING

### Problema: Dados não persistem após reiniciar
- Verifique se os volumes estão criados: `docker volume ls`
- Devem existir:
  - hortifruti-postgres-data
  - hortifruti-postgres-keycloak-data
  - hortifruti-keycloak-data

### Problema: Keycloak não inicia
- Verifique logs: `docker logs hortifruti-keycloak`
- Aguarde até 2 minutos na primeira inicialização

### Problema: Backend não autentica
- Verifique se o KEYCLOAK_CLIENT_SECRET no .env está correto
- Reconstrua o backend: `docker-compose up -d --build backend`
