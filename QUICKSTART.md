# Guia de Início Rápido - FrotaSyncro

## 🚀 Desenvolvimento em 5 Minutos

### Opção 1: Usando Docker Compose (Recomendado)

1. **Clone e prepare o ambiente:**

```bash
git clone <repository-url>
cd driveco
cp .env.example src/main/resources/application-local.properties
```

2. **Inicie todos os serviços:**

```bash
docker-compose up -d
```

Isso iniciará:

- PostgreSQL (porta 5432)
- LocalStack S3 (porta 4566)
- MailHog (porta 8025 para interface web)
- FrotaSyncro (porta 8080)

3. **Acesse a aplicação:**

- API: http://localhost:8080
- Swagger: http://localhost:8080/swagger-ui.html
- MailHog (emails): http://localhost:8025
- Health Check: http://localhost:8080/actuator/health

### Opção 2: Execução Local (Sem Docker)

1. **Pré-requisitos:**

```bash
# Instalar PostgreSQL 16+
sudo apt-get install postgresql-16  # Ubuntu/Debian
brew install postgresql@16          # macOS

# Criar banco de dados
createdb frotasyncro
```

2. **Configurar aplicação:**

```bash
cp .env.example src/main/resources/application-local.properties
# Edite application-local.properties com suas credenciais
```

3. **Executar aplicação:**

```bash
./mvnw spring-boot:run
```

## 📝 Primeiro Uso

### 1. Criar Usuário Admin

```bash
curl -X POST http://localhost:8080/auth/users \
  -H "Content-Type: application/json" \
  -d '{
    "email": "admin@frotasyncro.com",
    "password": "Admin@123",
    "fullName": "Administrador",
    "roleId": 1
  }'
```

### 2. Fazer Login

```bash
curl -X POST http://localhost:8080/auth \
  -H "Content-Type: application/json" \
  -d '{
    "email": "admin@frotasyncro.com",
    "password": "Admin@123"
  }'
```

Resposta:

```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "expiresIn": 86400000
}
```

### 3. Usar o Token

Todas as requisições subsequentes devem incluir o header:

```
Authorization: Bearer <seu-token>
```

## 🔧 Comandos Úteis

### Maven

```bash
# Compilar
./mvnw clean compile

# Executar testes
./mvnw test

# Gerar JAR
./mvnw clean package -DskipTests

# Executar aplicação
./mvnw spring-boot:run
```

### Docker

```bash
# Build da imagem
docker build -t frotasyncro:dev .

# Executar container
docker run -p 8080:8080 frotasyncro:dev

# Ver logs
docker-compose logs -f frotasyncro

# Parar todos os serviços
docker-compose down

# Parar e remover volumes
docker-compose down -v
```

### Database

```bash
# Conectar ao PostgreSQL
docker-compose exec postgres psql -U frotasyncro_user -d frotasyncro

# Backup
docker-compose exec postgres pg_dump -U frotasyncro_user frotasyncro > backup.sql

# Restore
docker-compose exec -T postgres psql -U frotasyncro_user frotasyncro < backup.sql
```

## 🧪 Testando a API

### Criar um Caminhão

```bash
curl -X POST http://localhost:8080/trucks \
  -H "Authorization: Bearer <seu-token>" \
  -H "Content-Type: application/json" \
  -d '{
    "licensePlate": "ABC-1234",
    "model": "Scania R450",
    "manufactureYear": 2020,
    "chassisNumber": "9BSR4X2V0KR123456",
    "renavam": "12345678901",
    "axles": 3
  }'
```

### Criar um Pneu

```bash
curl -X POST http://localhost:8080/tires \
  -H "Authorization: Bearer <seu-token>" \
  -H "Content-Type: application/json" \
  -d '{
    "fireCode": "TIRE-001",
    "manufacturer": "Michelin",
    "manufactureYear": 2023,
    "purchaseDate": "2023-01-15",
    "price": 1500.00,
    "mileage": 0,
    "tireCondition": "NEW",
    "tireStatus": "IN_STOCK"
  }'
```

### Criar um Motorista

```bash
curl -X POST http://localhost:8080/employers \
  -H "Authorization: Bearer <seu-token>" \
  -H "Content-Type: application/json" \
  -d '{
    "fullName": "João da Silva",
    "socialNumber": "123.456.789-00",
    "birthDate": "1985-05-20",
    "admissionDate": "2023-01-10",
    "driverLicense": "12345678901",
    "driverLicenseExpiryDate": "2028-05-20",
    "commissionPercentage": 5.0,
    "user": {
      "email": "joao.silva@frotasyncro.com",
      "password": "Driver@123"
    }
  }'
```

### Gerar Relatório

```bash
curl -X POST http://localhost:8080/reports/generate \
  -H "Authorization: Bearer <seu-token>" \
  -H "Content-Type: application/json" \
  -d '{
    "reportName": "tires",
    "filters": {
      "tireStatus": "IN_USE"
    }
  }'
```

Resposta:

```json
{
  "presignedUrl": "https://s3.amazonaws.com/bucket/reports/tires/tires_2025-11-21.xlsx?...",
  "fileName": "tires_2025-11-21T15:30:00.000Z.xlsx"
}
```

## 🐛 Troubleshooting

### Erro de Conexão com Banco de Dados

```bash
# Verificar se o PostgreSQL está rodando
docker-compose ps postgres

# Ver logs do PostgreSQL
docker-compose logs postgres

# Reiniciar serviço
docker-compose restart postgres
```

### Erro de Compilação

```bash
# Limpar cache do Maven
./mvnw clean

# Recompilar
./mvnw clean install -DskipTests
```

### Porta 8080 em Uso

```bash
# Encontrar processo
lsof -i :8080

# Ou mudar a porta no application.properties
server.port=8081
```

## 📚 Recursos Adicionais

- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **API Docs (JSON)**: http://localhost:8080/v3/api-docs
- **Health Check**: http://localhost:8080/actuator/health
- **Metrics**: http://localhost:8080/actuator/metrics
- **Prometheus**: http://localhost:8080/actuator/prometheus

## 🔍 Monitoramento (Opcional)

Para habilitar Prometheus e Grafana:

```bash
docker-compose --profile monitoring up -d
```

Acesse:

- Prometheus: http://localhost:9090
- Grafana: http://localhost:3000 (admin/admin)

## 💡 Dicas

1. **Use o Swagger UI** para explorar os endpoints interativamente
2. **Verifique o MailHog** para ver emails enviados durante testes
3. **Use o LocalStack** para testes com S3 sem custos
4. **Configure um IDE** (IntelliJ IDEA ou VS Code) para autocompletar
5. **Ative o profile correto**: sempre use `-Dspring.profiles.active=local` em
   dev

## 🆘 Suporte

Se encontrar problemas:

1. Verifique os logs: `docker-compose logs drivecore`
2. Consulte a documentação completa no README.md
3. Verifique as issues conhecidas
4. Entre em contato com a equipe

---

**Happy Coding! 🚀**
