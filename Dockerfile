# Estágio 1: Build da aplicação
FROM maven:3.9.5-eclipse-temurin-21-alpine AS build

# Diretório de trabalho
WORKDIR /app

# Copiar arquivos de configuração do Maven
COPY pom.xml .

# Baixar dependências (camada cacheável)
RUN mvn dependency:go-offline -B

# Copiar código fonte
COPY src ./src

# Compilar a aplicação
RUN mvn clean package -DskipTests

# Estágio 2: Imagem de produção
FROM eclipse-temurin:21-jre-alpine

# Metadados da imagem
LABEL maintainer="Sistema Hortifruti"
LABEL description="Backend do Sistema de Gerenciamento Hortifruti"
LABEL version="1.0.0"

# Criar usuário não-root para segurança
RUN addgroup -S spring && adduser -S spring -G spring

# Diretório de trabalho
WORKDIR /app

# Copiar JAR do estágio de build
COPY --from=build /app/target/*.jar app.jar

# Alterar propriedade do arquivo
RUN chown spring:spring app.jar

# Mudar para usuário não-root
USER spring:spring

# Expor porta da aplicação
EXPOSE 8080

# Configurar Java options
ENV JAVA_OPTS="-Xmx512m -Xms256m"

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8080/api/actuator/health || exit 1

# Comando de inicialização
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
