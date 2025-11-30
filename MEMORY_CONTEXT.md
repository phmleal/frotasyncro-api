# Memory Context - FrotaSyncro Application

## Visão Geral

A aplicação FrotaSyncro é uma API REST desenvolvida em Spring Boot para
gerenciamento de frotas de veículos, focada em
caminhões, reboques e pneus. O sistema permite o controle de máquinas (
veículos), pneus, contratos de entrega,
empregadores, despesas e relatórios, com autenticação baseada em JWT e
armazenamento em nuvem via AWS S3. Desenvolvido
para a empresa TL Transportadora.

## Versão Atual: 1.0.0 (Novembro 2025)

### Últimas Correções e Melhorias

#### Bugs Corrigidos (21/11/2025)

1. **Relatório de Pneus**: Corrigido erro "Query did not return a unique result"
   na consulta de posições ativas
2. **URLs de Anexos**: Corrigido retorno de presigned URLs ao invés de S3 keys
   para o frontend

#### Documentação Adicionada

- README.md completo com arquitetura, instalação e API
- QUICKSTART.md para início rápido do desenvolvimento
- CHANGELOG.md com histórico de versões
- docker-compose.yml para ambiente completo de desenvolvimento
- .env.example com template de configuração

## Aspectos Técnicos

### Stack Tecnológico

- **Linguagem**: Java 21
- **Framework**: Spring Boot 3.5.4
- **Banco de Dados**: PostgreSQL
- **Autenticação**: JWT (JJWT 0.12.6)
- **Armazenamento**: AWS S3 SDK 2.32.29
- **Relatórios**: Apache POI 5.2.5 (para geração de Excel)
- **Mapeamento**: MapStruct 1.6.3
- **Serialização**: Jackson Datatype JSR310 2.19.2
- **Outros**: Lombok, Thymeleaf (para templates de email), Spring Security,
  Spring Data JPA, Spring Web, Spring
  Actuator, Spring Validation, Spring Mail, Swagger/OpenAPI

### Arquitetura

A aplicação segue uma arquitetura limpa (Clean Architecture) com as seguintes
camadas:

- **Controller**: Exposição de endpoints REST, validação de entrada, autorização
  via Spring Security. Inclui
  documentação com Swagger.
- **Application**: Serviços de aplicação, orquestração de lógica de negócio,
  mapeamento DTO.
- **Domain**: Regras de negócio puras, serviços de domínio, entidades de
  negócio. Inclui serviços específicos como
  TireService, TruckService, etc.
- **Infrastructure**: Persistência (JPA/Repositories), configuração, segurança (
  JWT filters), mensageria, etc.

### Funcionalidades Principais

- **Autenticação**:
    - Login/logout (/auth POST).
    - Recuperação de senha via OTP por email (/auth/passwords-forget/otp POST,
      /auth/passwords-forget/otp/validation
      POST, /auth/passwords-forget/update PATCH).
    - Gerenciamento de usuários: criar (/auth/users POST), atualizar (
      /auth/users/{id} PATCH), detalhes (
      /auth/users/{id} GET).
    - Listagem de roles (/auth/roles GET).
- **Gerenciamento de Máquinas**:
    - Máquinas gerais: listagem (/machines/list POST).
    - Caminhões: CRUD completo (/trucks POST, GET /{id}, /list POST, PATCH
      /{id}, DELETE /{id}).
    - Reboques: CRUD completo (/trailers POST, GET /{id}, /list POST, PATCH
      /{id}, DELETE /{id}).
    - Combinações caminhão-reboque: CRUD completo (/trucks-trailers-combinations
      POST, GET /{id}, /list POST, etc.).
- **Gerenciamento de Pneus**:
    - CRUD de pneus (/tires POST, GET /{id}, /list POST, PATCH /{id}, DELETE
      /{id}).
    - Posições de pneus: adicionar posição a máquina (/tires/tires-positions
      POST), obter posições por máquina (
      /tires/tires-positions/machines/{machineId} GET), inativar posição (
      /tires/tires-positions/{tirePositionId}/inactivate PATCH).
- **Contratos e Entregas**: Criação de contratos de entrega (/deliveries POST).
- **Empregadores**: CRUD completo (/employers POST, GET /{id}, /list POST, PATCH
  /{id}, DELETE /{id}).
- **Despesas**: Gestão interna de despesas (sem endpoints públicos).
- **Relatórios**: Geração de relatórios em Excel via POI (integrado em endpoints
  como listagens de pneus).
- **Home/Dashboard**: Informações de painel de controle (/home GET).
- **Anexos**: Possibilidade de upload de arquivos via S3 (não exposto
  diretamente, usado internamente).

### Segurança

- Autenticação baseada em JWT com filtros customizados (JwtRequestFilter).
- Autorização baseada em roles (ex: ROLE_ADMIN).
- Validação de entrada com Bean Validation.
- Configuração de CORS e segurança web.

### Configuração

- Perfis: local (desenvolvimento), prod (produção).
- Propriedades: application.properties (base), application-local.properties (
  banco local, mail local),
  application-prod.properties (variáveis de ambiente).
- Templates de email: forget-password-email.html, welcome-email.html.
- Configurações adicionais: Async (processamento assíncrono), Thymeleaf para
  emails, Swagger para documentação.

### Build e Deploy

- Build: Maven (mvnw, mvnw.cmd).
- Containerização: Dockerfile presente.
- JAR: drivecore-1.0.0.jar.
- Monitoramento: Spring Actuator com endpoints de health, info, metrics,
  prometheus.

## Aspectos de Negócio

### Domínio

O FrotaSyncro é um sistema para empresas de transporte/logística,
especificamente a TL Transportadora, que gerenciam
frotas de caminhões. O foco é em:

- **Manutenção de Veículos**: Controle detalhado de caminhões, reboques,
  combinações e pneus para garantir segurança,
  eficiência e conformidade.
- **Gestão de Contratos**: Contratos de entrega para rastreamento e
  gerenciamento de cargas.
- **Recursos Humanos**: Empregadores (motoristas, gestores) com CRUD completo.
- **Despesas**: Controle interno de custos associados às operações.
- **Relatórios**: Análise de dados para tomada de decisões, exportação em Excel.
- **Anexos**: Armazenamento de documentos relacionados em S3.

### Usuários

- **Admins**: Acesso total para gerenciamento de usuários, máquinas, pneus,
  empregadores, etc.
- Possivelmente outros roles para motoristas ou gestores (não implementados
  ainda).

### Fluxos Principais

1. **Autenticação e Recuperação**: Login, reset de senha via OTP.
2. **Cadastro de Veículos**: Adicionar caminhões, reboques, associar pneus e
   combinações.
3. **Manutenção de Pneus**: Monitorar estado dos pneus, substituições,
   relatórios.
4. **Gestão de Empregadores**: CRUD de motoristas/gestores.
5. **Gestão de Entregas**: Criar contratos de entrega.
6. **Relatórios e Análises**: Gerar relatórios em Excel para insights.
7. **Dashboard**: Visualizar informações gerais no endpoint /home.

### Integrações

- **Email**: Envio de emails para recuperação de senha e welcome.
- **Armazenamento em Nuvem**: AWS S3 para anexos e arquivos.
- **Banco de Dados**: PostgreSQL com JPA/Hibernate.

Este contexto deve ser usado para aprendizagem contínua, garantindo que agentes
mantenham consistência nas respostas,
qualidade e eficiência no desenvolvimento e manutenção do projeto.
