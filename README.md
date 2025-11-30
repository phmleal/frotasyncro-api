# FrotaSyncro - Sistema de Gestão de Frotas

[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.4-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16+-blue.svg)](https://www.postgresql.org/)
[![License](https://img.shields.io/badge/License-Proprietary-red.svg)]()

Sistema completo de gestão de frotas desenvolvido para a TL Transportadora,
focado no gerenciamento de caminhões, reboques, pneus, motoristas, contratos de
entrega e relatórios operacionais.

## 📋 Índice

- [Visão Geral](#visão-geral)
- [Funcionalidades](#funcionalidades)
- [Arquitetura](#arquitetura)
- [Tecnologias](#tecnologias)
- [Pré-requisitos](#pré-requisitos)
- [Instalação e Configuração](#instalação-e-configuração)
- [Execução](#execução)
- [API Documentation](#api-documentation)
- [Estrutura do Projeto](#estrutura-do-projeto)
- [Módulos do Sistema](#módulos-do-sistema)
- [Segurança](#segurança)
- [Deploy](#deploy)
- [Monitoramento](#monitoramento)
- [Contribuindo](#contribuindo)

## 🎯 Visão Geral

O FrotaSyncro é uma API REST empresarial desenvolvida em Spring Boot para
gerenciamento completo de operações de transporte. O sistema permite controle
detalhado de:

- **Veículos**: Caminhões, reboques e combinações
- **Pneus**: Controle de vida útil, posicionamento e manutenção
- **Motoristas**: Cadastro completo com validação de documentos
- **Contratos**: Gestão de entregas e ordens de trabalho
- **Relatórios**: Geração de relatórios analíticos em Excel
- **Despesas**: Controle financeiro das operações

### 🏢 Contexto de Negócio

Desenvolvido especificamente para a TL Transportadora, o sistema atende às
necessidades de:

- Gestores de frota que precisam monitorar o estado e localização de veículos
- Equipes de manutenção que controlam a vida útil de pneus
- Departamento financeiro que acompanha despesas operacionais
- RH que gerencia documentação de motoristas
- Operadores que precisam gerar relatórios gerenciais

## ✨ Funcionalidades

### 🔐 Autenticação e Autorização

- Login/Logout com JWT
- Recuperação de senha via OTP por email
- Gerenciamento de usuários e roles
- Controle de acesso baseado em permissões

### 🚛 Gestão de Máquinas (Veículos)

- **Caminhões**: CRUD completo com rastreamento de status
- **Reboques**: Gerenciamento independente
- **Combinações**: Acoplamento dinâmico de caminhões e reboques
- Listagem com filtros avançados e paginação
- Histórico de acoplamentos

### 🛞 Gestão de Pneus

- Cadastro completo com código de fogo
- Controle de vida útil e quilometragem
- Posicionamento em veículos (eixo e lado)
- Histórico de movimentações
- Relatório de estado dos pneus
- Alertas de manutenção

### 👨‍💼 Gestão de Motoristas (Empregadores)

- Cadastro completo de motoristas
- Controle de CNH e validade
- Gestão de exames médicos e toxicológicos
- Comissionamento
- Relatórios de status da equipe

### 📦 Gestão de Contratos e Entregas

- Criação de contratos de entrega
- Anexos de documentos no S3
- Controle de despesas por contrato
- Rastreamento de status de entregas
- Relatórios de ordens de trabalho

### 📊 Sistema de Relatórios

- **Histórico de Acoplamento**: Combinações de veículos ao longo do tempo
- **Ordens de Trabalho**: Contratos,despesas e entregas
- **Relatório de Pneus**: Estado completo da frota de pneus
- **Relatório de Motoristas**: Dados completos e status de documentação
- Exportação em Excel com formatação profissional
- Upload automático no S3 com URLs assinadas

### 🏠 Dashboard

- Visão geral das operações
- Indicadores chave de desempenho
- Alertas e notificações

## 🏗 Arquitetura

O projeto segue os princípios de **Clean Architecture** e **Domain-Driven
Design (DDD)**:

```
┌─────────────────────────────────────────────────────┐
│                   Controller Layer                   │
│  (REST Endpoints, DTOs, Validação, Documentação)    │
└─────────────────────┬───────────────────────────────┘
                      │
┌─────────────────────▼───────────────────────────────┐
│                Application Layer                     │
│     (Casos de Uso, Orquestração, Mapeamento)       │
└─────────────────────┬───────────────────────────────┘
                      │
┌─────────────────────▼───────────────────────────────┐
│                  Domain Layer                        │
│   (Regras de Negócio, Serviços, Entidades Core)    │
└─────────────────────┬───────────────────────────────┘
                      │
┌─────────────────────▼───────────────────────────────┐
│              Infrastructure Layer                    │
│   (JPA, Repositories, S3, Email, Security, etc)     │
└─────────────────────────────────────────────────────┘
```

### Camadas

#### 1. **Controller Layer**

- Exposição de endpoints REST
- Validação de entrada via Bean Validation
- Documentação Swagger/OpenAPI
- Conversão de exceções em respostas HTTP

#### 2. **Application Layer**

- Orquestração de casos de uso
- Coordenação entre serviços de domínio
- Mapeamento entre DTOs e entidades de domínio

#### 3. **Domain Layer**

- Lógica de negócio pura
- Serviços de domínio (TireService, TruckService, etc.)
- Regras de validação de negócio
- Enumerações e modelos de domínio

#### 4. **Infrastructure Layer**

- Persistência com JPA/Hibernate
- Integração com AWS S3
- Envio de emails
- Segurança e autenticação JWT
- Configurações e factories

## 🛠 Tecnologias

### Core

- **Java 21**: Linguagem principal com features modernas
- **Spring Boot 3.5.4**: Framework de aplicação
- **Spring Data JPA**: Persistência de dados
- **PostgreSQL**: Banco de dados relacional
- **Hibernate**: ORM

### Segurança

- **Spring Security**: Autenticação e autorização
- **JWT (JJWT 0.12.6)**: Tokens de autenticação
- **BCrypt**: Hash de senhas

### Cloud & Storage

- **AWS S3 SDK 2.32.29**: Armazenamento de arquivos
- **AWS S3 Presigned URLs**: URLs temporárias para downloads

### Relatórios

- **Apache POI 5.2.5**: Geração de arquivos Excel
- **SXSSFWorkbook**: Streaming para grandes volumes de dados

### Mapeamento e Validação

- **MapStruct 1.6.3**: Mapeamento objeto-objeto
- **Jakarta Validation**: Validação de beans
- **Lombok**: Redução de boilerplate

### Comunicação

- **Spring Mail**: Envio de emails
- **Thymeleaf**: Templates de email

### Documentação e Monitoramento

- **Swagger/OpenAPI 2.8.3**: Documentação interativa da API
- **Spring Actuator**: Health checks e métricas
- **Micrometer + Prometheus**: Métricas e observabilidade

### Build e Deploy

- **Maven**: Gerenciamento de dependências e build
- **Docker**: Containerização
- **Amazon Corretto 21**: JDK para produção

## 📋 Pré-requisitos

- Java 21 ou superior
- PostgreSQL 16 ou superior
- Maven 3.9+
- Docker e Docker Compose (opcional)
- Conta AWS com acesso ao S3 (para produção)
- Servidor SMTP para envio de emails

## 🚀 Instalação e Configuração

### 1. Clone o Repositório

```bash
git clone <repository-url>
cd driveco
```

### 2. Configuração do Banco de Dados

Crie um banco de dados PostgreSQL:

```sql
CREATE DATABASE drivecore;
CREATE USER drivecore_user WITH ENCRYPTED PASSWORD 'sua_senha';
GRANT ALL PRIVILEGES ON DATABASE drivecore TO drivecore_user;
```

### 3. Configuração Local

Crie o arquivo `src/main/resources/application-local.properties`:

```properties
# Database
spring.datasource.url=jdbc:postgresql://localhost:5432/drivecore
spring.datasource.username=drivecore_user
spring.datasource.password=sua_senha
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true

# JWT
API.SECRET.KEY=seu_secret_key_muito_seguro_aqui_com_no_minimo_256_bits

# Email (desenvolvimento - usar MailHog ou similar)
spring.mail.host=localhost
spring.mail.port=1025
spring.mail.username=
spring.mail.password=
API.MAIL.FROM=noreply@drivecore.local

# AWS S3 (desenvolvimento - usar LocalStack ou MinIO)
AWS.ACCESS.KEY=test
AWS.SECRET.ACCESS=test
API.BUCKET.NAME=drivecore-dev
```

### 4. Configuração de Produção

Configure as variáveis de ambiente para produção:

```bash
# Database
export DATASOURCE_URL=jdbc:postgresql://seu-host:5432/drivecore
export DATASOURCE_USERNAME=drivecore_user
export DATASOURCE_PASSWORD=senha_segura
export DATASOURCE_SHOW_SQL=false

# Security
export API.SECRET.KEY=chave_jwt_super_segura_256_bits

# Email
export spring.mail.host=smtp.gmail.com
export spring.mail.port=587
export spring.mail.username=seu-email@gmail.com
export spring.mail.password=sua-senha-de-app
export API.MAIL.FROM=noreply@seudominio.com

# AWS
export AWS.ACCESS.KEY=sua_access_key
export AWS.SECRET.ACCESS=sua_secret_key
export API.BUCKET.NAME=drivecore-production
```

## 🏃 Execução

### Desenvolvimento Local

```bash
# Compilar o projeto
./mvnw clean package -DskipTests

# Executar com profile local
./mvnw spring-boot:run -Dspring-boot.run.profiles=local

# Ou executar o JAR
java -jar target/drivecore-1.0.0.jar --spring.profiles.active=local
```

### Docker

```bash
# Build da imagem
docker build -t drivecore:latest .

# Executar container
docker run -d \
  --name drivecore \
  -p 8080:8080 \
  -e SPRING_PROFILES_ACTIVE=prod \
  -e DATASOURCE_URL=jdbc:postgresql://host.docker.internal:5432/drivecore \
  -e DATASOURCE_USERNAME=drivecore_user \
  -e DATASOURCE_PASSWORD=senha \
  # ... outras variáveis de ambiente
  drivecore:latest
```

### Docker Compose (exemplo)

```yaml
version: '3.8'

services:
  postgres:
    image: postgres:16-alpine
    environment:
      POSTGRES_DB: drivecore
      POSTGRES_USER: drivecore_user
      POSTGRES_PASSWORD: senha
    ports:
      - "5432:5432"
    volumes:
      - postgres_data:/var/lib/postgresql/data

  drivecore:
    build: .
    ports:
      - "8080:8080"
    environment:
      SPRING_PROFILES_ACTIVE: prod
      DATASOURCE_URL: jdbc:postgresql://postgres:5432/drivecore
      DATASOURCE_USERNAME: drivecore_user
      DATASOURCE_PASSWORD: senha
      # ... outras variáveis
    depends_on:
      - postgres

volumes:
  postgres_data:
```

## 📚 API Documentation

A documentação interativa da API está disponível via Swagger UI:

```
http://localhost:8080/swagger-ui.html
```

### Principais Endpoints

#### Autenticação

```
POST   /auth                          - Login
POST   /auth/users                    - Criar usuário
GET    /auth/users/{id}               - Detalhes do usuário
PATCH  /auth/users/{id}               - Atualizar usuário
POST   /auth/passwords-forget/otp     - Solicitar OTP
POST   /auth/passwords-forget/otp/validation - Validar OTP
PATCH  /auth/passwords-forget/update  - Atualizar senha
GET    /auth/roles                    - Listar roles
```

#### Máquinas (Veículos)

```
POST   /machines/list                 - Listar máquinas
POST   /trucks                        - Criar caminhão
GET    /trucks/{id}                   - Detalhes do caminhão
POST   /trucks/list                   - Listar caminhões
PATCH  /trucks/{id}                   - Atualizar caminhão
DELETE /trucks/{id}                   - Deletar caminhão
POST   /trailers                      - Criar reboque
POST   /trucks-trailers-combinations  - Criar combinação
```

#### Pneus

```
POST   /tires                                        - Criar pneu
GET    /tires/{id}                                   - Detalhes do pneu
POST   /tires/list                                   - Listar pneus
PATCH  /tires/{id}                                   - Atualizar pneu
DELETE /tires/{id}                                   - Deletar pneu
POST   /tires/tires-positions                        - Adicionar posição
GET    /tires/tires-positions/machines/{machineId}  - Posições por máquina
PATCH  /tires/tires-positions/{id}/inactivate       - Inativar posição
```

#### Motoristas

```
POST   /employers                     - Criar motorista
GET    /employers/{id}                - Detalhes do motorista
POST   /employers/list                - Listar motoristas
PATCH  /employers/{id}                - Atualizar motorista
DELETE /employers/{id}                - Deletar motorista
```

#### Contratos e Entregas

```
POST   /deliveries                    - Criar contrato de entrega
POST   /contracts/{id}/expenses       - Adicionar despesa
POST   /contracts/{id}/attachments    - Adicionar anexo
GET    /contracts/attachments/{id}/download - Download de anexo
```

#### Relatórios

```
POST   /reports/generate              - Gerar relatório
```

Tipos de relatórios disponíveis:

- `coupling_history`: Histórico de Acoplamento
- `work_orders`: Ordens de Trabalho
- `tires`: Relatório de Pneus
- `drivers`: Relatório de Motoristas

#### Dashboard

```
GET    /home                          - Dados do dashboard
```

## 📁 Estrutura do Projeto

```
src/main/java/br/com/drivecore/
├── DriveCoreApplication.java          # Classe principal
├── application/                        # Camada de aplicação
│   ├── authentication/                # Casos de uso de autenticação
│   ├── contract/                      # Casos de uso de contratos
│   ├── employer/                      # Casos de uso de motoristas
│   ├── home/                          # Casos de uso do dashboard
│   ├── machine/                       # Casos de uso de máquinas
│   ├── report/                        # Casos de uso de relatórios
│   └── tire/                          # Casos de uso de pneus
├── controller/                         # Camada de controladores REST
│   ├── authentication/                # Endpoints de autenticação
│   ├── contract/                      # Endpoints de contratos
│   ├── employer/                      # Endpoints de motoristas
│   ├── home/                          # Endpoints do dashboard
│   ├── machine/                       # Endpoints de máquinas
│   ├── model/                         # DTOs compartilhados
│   ├── report/                        # Endpoints de relatórios
│   └── tire/                          # Endpoints de pneus
├── core/                              # Configurações e componentes core
│   ├── configuration/                 # Configurações do Spring
│   ├── exception/                     # Tratamento global de exceções
│   ├── factory/                       # Factories (S3, ObjectMapper)
│   ├── security/                      # Configuração de segurança JWT
│   └── specification/                 # Specifications para filtros dinâmicos
├── domain/                            # Camada de domínio
│   ├── attachment/                    # Domínio de anexos
│   ├── authentication/                # Domínio de autenticação
│   ├── contract/                      # Domínio de contratos
│   ├── employer/                      # Domínio de motoristas
│   ├── expense/                       # Domínio de despesas
│   ├── machine/                       # Domínio de máquinas
│   ├── report/                        # Domínio de relatórios
│   │   ├── enums/                     # Enumerações
│   │   ├── generator/                 # Geradores de relatório
│   │   ├── strategy/                  # Estratégias de geração
│   │   │   ├── factory/               # Factory de estratégias
│   │   │   └── impl/                  # Implementações
│   │   └── model/                     # Modelos de domínio
│   └── tire/                          # Domínio de pneus
└── infrastructure/                    # Camada de infraestrutura
    ├── attachment/                    # Providers de anexo (S3)
    ├── authentication/                # Providers de autenticação
    ├── messaging/                     # Envio de emails
    └── persistence/                   # JPA repositories e entidades
        ├── attachment/
        ├── authentication/
        ├── contract/
        ├── employer/
        ├── expense/
        ├── generic/                   # BaseEntity
        ├── machine/
        └── tire/
```

## 🔒 Segurança

### Autenticação

- JWT com tempo de expiração configurável
- Tokens são gerados no login e devem ser incluídos no header
  `Authorization: Bearer <token>`
- Refresh tokens não implementados (tokens devem ser renovados via novo login)

### Autorização

- Controle baseado em roles (ROLE_ADMIN, etc.)
- Endpoints protegidos via `@PreAuthorize`
- Senha criptografada com BCrypt

### Recuperação de Senha

- OTP de 6 dígitos enviado por email
- Validade de 10 minutos
- Máximo de 3 tentativas de validação
- Expiração automática após uso

### CORS

- Configurado para permitir origens específicas em produção
- Headers customizados permitidos

### Boas Práticas Implementadas

- Validação de entrada em todos os endpoints
- Sanitização de dados sensíveis nos logs
- SQL Injection prevenido via JPA/Prepared Statements
- Senha nunca retornada em respostas de API
- Tokens armazenados apenas em memória (não em cookies)

## 🚀 Deploy

### Build de Produção

```bash
./mvnw clean package -DskipTests
```

O artefato gerado estará em `target/drivecore-1.0.0.jar`

### Docker Multi-stage Build

O Dockerfile fornecido utiliza multi-stage build para otimização:

1. **Stage Build**: Compila a aplicação com Maven
2. **Stage Runtime**: Executa apenas o JAR com JRE mínimo

```bash
docker build -t drivecore:1.0.0 .
docker tag drivecore:1.0.0 seu-registry/drivecore:1.0.0
docker push seu-registry/drivecore:1.0.0
```

### Deploy em Cloud

#### AWS Elastic Beanstalk

```bash
eb init -p docker drivecore
eb create drivecore-production
eb deploy
```

#### Kubernetes

```yaml
# Exemplo de deployment
apiVersion: apps/v1
kind: Deployment
metadata:
  name: drivecore
spec:
  replicas: 3
  selector:
    matchLabels:
      app: drivecore
  template:
    metadata:
      labels:
        app: drivecore
    spec:
      containers:
      - name: drivecore
        image: seu-registry/drivecore:1.0.0
        ports:
        - containerPort: 8080
        env:
        - name: SPRING_PROFILES_ACTIVE
          value: "prod"
        # ... outras variáveis via ConfigMap/Secrets
```

## 📊 Monitoramento

### Health Checks

```bash
# Health geral
curl http://localhost:8080/actuator/health

# Métricas
curl http://localhost:8080/actuator/metrics

# Prometheus
curl http://localhost:8080/actuator/prometheus
```

### Endpoints Actuator Disponíveis

- `/actuator/health`: Status da aplicação e dependências
- `/actuator/info`: Informações da aplicação
- `/actuator/metrics`: Métricas da JVM e aplicação
- `/actuator/prometheus`: Métricas no formato Prometheus

### Integração com Prometheus/Grafana

1. Configure o Prometheus para scraping:

```yaml
scrape_configs:
  - job_name: 'drivecore'
    metrics_path: '/actuator/prometheus'
    static_configs:
      - targets: [ 'localhost:8080' ]
```

2. Importe dashboards do Grafana para Spring Boot

### Logs

A aplicação utiliza SLF4J com Logback. Configuração em `logback-spring.xml` (
criar se necessário):

```xml
<?xml version="1.0" encoding="UTF-8"?>
<configuration>
    <include resource="org/springframework/boot/logging/logback/base.xml"/>
    <logger name="br.com.frotasyncro" level="INFO"/>
    <logger name="org.springframework.web" level="INFO"/>
    <logger name="org.hibernate" level="WARN"/>
</configuration>
```

## 🧪 Testes

### Executar Testes

```bash
# Todos os testes
./mvnw test

# Testes específicos
./mvnw test -Dtest=TireServiceTest

# Com cobertura
./mvnw test jacoco:report
```

### Estrutura de Testes

```
src/test/java/br/com/drivecore/
├── domain/           # Testes unitários da camada de domínio
├── application/      # Testes de casos de uso
├── controller/       # Testes de integração dos controllers
└── infrastructure/   # Testes de repositórios e integrações
```

## 📈 Performance

### Otimizações Implementadas

1. **Connection Pool (HikariCP)**
    - Máximo 7 conexões em produção
    - Mínimo 3 conexões idle
    - Timeout configurado para evitar travamentos

2. **Relatórios**
    - Uso de SXWorkbook
