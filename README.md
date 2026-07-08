# Pedidos Service

API REST para gestão de **pedidos**, **produtos** e **usuários**, com autenticação JWT, notificações de e-mail assíncronas via **RabbitMQ** e expiração automática de pedidos. O backend segue **arquitetura hexagonal** (Ports & Adapters) e sobe inteiro com um único `docker compose up`.

> **⚠️ Sobre o frontend:** este repositório contém **apenas o backend**. A pasta `frontend/` existe na estrutura do projeto mas **não tem implementação funcional ainda** — é um espaço reservado para um cliente Angular futuro. Não há nada para rodar ou visualizar ali no momento. Toda a interação com o sistema é feita via API (Swagger UI, Postman, curl, etc).

> **Para quem vai avaliar o projeto:** você não precisa instalar Java, Gradle, Postgres nem RabbitMQ na sua máquina. Só precisa do **Docker Desktop**. Pule direto para [🚀 Como rodar](#-como-rodar-com-docker-compose).

---

## 📑 Índice

- [Visão geral](#-visão-geral)
- [Stack / Tecnologias](#-stack--tecnologias)
- [Arquitetura](#-arquitetura)
- [Pré-requisitos](#-pré-requisitos)
- [🚀 Como rodar com docker-compose](#-como-rodar-com-docker-compose)
- [Acessos após subir](#-acessos-após-subir)
- [Endpoints](#-endpoints)
- [Notificações por e-mail](#-notificações-por-e-mail)
- [Variáveis de ambiente](#-variáveis-de-ambiente)
- [Rodar os testes](#-rodar-os-testes)
- [Rodar sem Docker (opcional)](#-rodar-sem-docker-opcional)
- [Frontend (planejado)](#-frontend-planejado)
- [Estrutura do projeto](#-estrutura-do-projeto)
- [Solução de problemas](#-solução-de-problemas)

---

## 🔎 Visão geral

O sistema modela um fluxo de e-commerce simplificado:

- **Usuários** se cadastram e autenticam. Cada usuário é `CLIENT` ou `ADMIN`.
- **Produtos** compõem um catálogo. Somente `ADMIN` cria/edita/remove; qualquer autenticado consulta.
- **Pedidos** são criados por um usuário, recebem itens (validando estoque), e caminham por status até `ENTREGUE`.
- A cada evento relevante — **criação de um pedido** e **mudança de status para `ENTREGUE`** — um **e-mail é enviado de forma assíncrona** por meio de uma fila RabbitMQ — a API responde na hora, sem esperar o SMTP.
- Pedidos que ficam parados **expiram automaticamente** e são limpos por um agendador(Cron service).

## 🧰 Stack / Tecnologias

| Camada                   | Tecnologia                                                                     |
|--------------------------|--------------------------------------------------------------------------------|
| Linguagem                | **Java 21**                                                                    |
| Framework                | **Spring Boot 3.5.14** (Web, Data JPA, Security, Validation, AMQP, Mail, Actuator) |
| Banco de dados           | **PostgreSQL 16**                                                              |
| Mensageria               | **RabbitMQ 3.13** (com Dead Letter Queues e retry com backoff)                 |
| Autenticação             | **JWT** (jjwt 0.12)                                                            |
| Scheduled                | **Cron**                                                                       |
| Documentação             | **OpenAPI / Swagger UI** (springdoc 2.8)                                       |
| Templates de e-mail      | **Thymeleaf**                                                                  |
| Mapeamento DTO ↔ domínio | **MapStruct** + Lombok                                                         |
| Build                    | **Gradle** (wrapper incluso, projeto multi-módulo)                             |
| Testes                   | JUnit 5, Spring Security Test, Mockito                                         |
| Container                | **Docker** + **Docker Compose**                                                |


## 🏛 Arquitetura

Arquitetura **hexagonal (Ports & Adapters)**. Cada domínio (`user`, `product`, `order`, `notification`) é organizado em:

```
adapters/   → entrada (controllers REST, consumers RabbitMQ, schedulers) e saída (repositórios JPA, e-mail, publisher)
core/       → regras de negócio puras (domain + service), sem dependência de framework
gateways/   → interfaces (portas) que o core usa para falar com o mundo externo
ports/      → repositórios JPA (Spring Data)
dtos/       → contratos de entrada/saída da API + mappers
entities/   → entidades JPA (persistência)
```

O núcleo (`core`) não conhece Spring, banco ou RabbitMQ — depende apenas de interfaces. Isso mantém as regras de negócio testáveis e desacopladas da infraestrutura.

## ✅ Pré-requisitos

- **[Docker Desktop](https://www.docker.com/products/docker-desktop/)** instalado e **em execução** (inclui o Docker Compose v2).
- Portas livres na máquina: **8080** (API), **5432** (Postgres), **5672** e **15672** (RabbitMQ).

Nada além disso. O `Dockerfile` compila o projeto com Gradle **dentro da imagem**, então você não precisa de Java/Gradle instalados.

## 🚀 Como rodar com docker-compose

### 1. Clone o repositório

```bash
git clone <url-do-repositorio>
cd pedidosservice
```

### 2. Crie o arquivo `.env`

O `docker-compose.yml` lê as variáveis de um arquivo **`.env`** na raiz. Já existe um `.env.example` pronto — basta copiá-lo:

```bash
# Linux / macOS / Git Bash
cp .env.example .env
```

```powershell
# Windows PowerShell
Copy-Item .env.example .env
```

> Os valores padrão já funcionam de imediato (inclusive a senha de app de um Gmail de **teste** dedicado, usada só para demonstrar o envio de e-mail). Não é preciso alterar nada para avaliar o projeto.

### 3. Suba tudo

```bash
docker compose up -d --build
```

Isso vai:
1. Baixar as imagens do Postgres e do RabbitMQ.
2. Compilar o backend e montar a imagem da aplicação (**a primeira vez demora alguns minutos** — é normal).
3. Subir os 3 containers: `pedidos-postgres`, `pedidos-rabbitmq` e `pedidos-app`.

> **Nota sobre a ordem de inicialização:** o `depends_on` garante que o Postgres/RabbitMQ **iniciem** antes da API, mas não que estejam totalmente **prontos**. Em máquinas mais lentas a API pode subir antes do banco aceitar conexões e falhar no boot. Se isso acontecer, um `docker compose restart app` resolve.

## 🌐 Acessos após subir

| Recurso | URL | Credenciais |
|---|---|---|
| **API (base URL)** | http://localhost:8080 | — |
| **Swagger UI** (documentação interativa) | http://localhost:8080/swagger-ui.html | — |
| **OpenAPI JSON** | http://localhost:8080/v3/api-docs | — |
| **RabbitMQ Management** | http://localhost:15672 | `guest` / `guest` |
| **Health check** (Actuator) | http://localhost:8080/actuator/health | — |
| **PostgreSQL** | `localhost:5432` (db `db_pedidos`) | `postgres` / `1234` |

> 💡 A forma mais fácil de testar tudo é abrir o **Swagger UI**, clicar em **Authorize** e colar o token JWT (veja o passo a passo abaixo).

## 📚 Endpoints

Base URL: `http://localhost:8080` · Autenticação: `Authorization: Bearer <token>` (exceto onde indicado **Público**).

### Usuários — `/user`

| Método | Rota | Descrição | Acesso |
|---|---|---|---|
| `POST` | `/user` | Cadastrar usuário | **Público** |
| `POST` | `/user/login` | Autenticar e receber JWT | **Público** |
| `GET` | `/user/{id}` | Buscar usuário por id | Autenticado |
| `PATCH` | `/user/{id}?status=` | Alterar status (`CLIENT`/`ADMIN`) | Autenticado |
| `DELETE` | `/user/{id}` | Remover usuário | **ADMIN** |

### Produtos — `/product`

| Método | Rota | Descrição | Acesso |
|---|---|---|---|
| `GET` | `/product` | Listar catálogo | Autenticado |
| `GET` | `/product/{id}` | Buscar produto por id | Autenticado |
| `POST` | `/product` | Criar produto | **ADMIN** |
| `PUT` | `/product/{id}` | Atualizar produto | **ADMIN** |
| `DELETE` | `/product/{id}` | Remover produto | **ADMIN** |

### Pedidos — `/order`

| Método | Rota | Descrição | Acesso | Dispara e-mail? |
|---|---|---|---|---|
| `POST` | `/order` | Criar pedido para o usuário autenticado | Autenticado | ✅ sim |
| `POST` | `/order/{id}/items` | Adicionar itens (lista) ao pedido | Autenticado | não |
| `DELETE` | `/order/{idOrder}/items/{idOrderItem}` | Remover um item do pedido | Autenticado | não |
| `PATCH` | `/order/{id}?status=` | Atualizar status do pedido | Autenticado | ✅ sim, **apenas se `status=ENTREGUE`** |
| `GET` | `/order/{id}` | Buscar pedido por id | Autenticado | não |
| `DELETE` | `/order/{id}` | Remover pedido | Autenticado | não |

**Status de pedido:** `PENDENTE` · `CONFIRMADO` · `ENTREGUE` · `CANCELADO` · `EXPIRADO`

## 📧 Notificações por e-mail

O envio de e-mail é **desacoplado** da requisição HTTP através do RabbitMQ e ocorre em **exatamente dois momentos** do ciclo de vida do pedido:

1. **Criação do pedido** — `POST /order` publica o evento `order.created`.
2. **Entrega do pedido** — `PATCH /order/{id}?status=ENTREGUE` publica o evento `order.finished`. Mudar o status para qualquer outro valor (`CONFIRMADO`, `CANCELADO`, etc.) **não** dispara e-mail.

Fluxo técnico:

1. O `OrderService` publica o evento correspondente numa **exchange** (`pedidos.exchange`).
2. Um **consumer** (`@RabbitListener`) processa o evento e envia o e-mail via SMTP (Gmail) usando templates Thymeleaf.
3. **Idempotência:** cada evento tem um `eventId`; eventos já processados são ignorados para não enviar e-mail duplicado.
4. **Resiliência:** se o SMTP falhar, o listener tenta novamente com **backoff exponencial** (até 3 tentativas). Esgotadas as tentativas, a mensagem vai para uma **Dead Letter Queue** (`*.dlq`) em vez de se perder. Dá para inspecionar filas e DLQs no painel do RabbitMQ (http://localhost:15672).

O envio usa uma conta Gmail de **teste** (`andre.teste.notificacao@gmail.com`) configurada via `EMAIL_APP_PASSWORD`. Para receber o e-mail de verdade, cadastre o usuário com um e-mail seu no passo 1.

### Expiração automática de pedidos

Um agendador (`@Scheduled`, cron `0 0/10 * * * ?`) roda **a cada 10 minutos** para marcar pedidos vencidos como `EXPIRADO` e limpar os expirados. A validade é renovada em +6h sempre que o pedido é alterado. Todos os horários usam o fuso `America/Sao_Paulo`.

> A expiração automática **não** dispara e-mail — apenas os dois eventos listados acima o fazem.

## 🔐 Variáveis de ambiente

Definidas no arquivo `.env` (a partir do `.env.example`) e injetadas pelo `docker-compose.yml`:

| Variável | Descrição | Padrão |
|---|---|---|
| `POSTGRES_DB` | Nome do banco | `db_pedidos` |
| `POSTGRES_USER` | Usuário do Postgres | `postgres` |
| `POSTGRES_PASSWORD` | Senha do Postgres | `1234` |
| `RABBITMQ_USER` | Usuário do RabbitMQ | `guest` |
| `RABBITMQ_PASSWORD` | Senha do RabbitMQ | `guest` |
| `EMAIL_APP_PASSWORD` | Senha de app do Gmail (envio de e-mail) | conta de teste |

> O `.env` real é **ignorado pelo Git** (não é versionado). Apenas o `.env.example` fica no repositório.

## 🧪 Rodar os testes

O projeto tem cobertura de testes unitários e de integração (services, controllers, mappers, JWT, consumers RabbitMQ). Para rodá-los sem Docker, é preciso ter o **JDK 21**:

```bash
cd backend
./gradlew test        # Linux/macOS/Git Bash
gradlew.bat test      # Windows
```

Os testes usam **H2** em memória — não exigem Postgres nem RabbitMQ no ar.

## 🖥 Rodar sem Docker (opcional)

Requer **JDK 21**, além de um Postgres e um RabbitMQ acessíveis localmente. A aplicação lê `application.properties`, que aponta para `localhost:5432` e `localhost:5672` por padrão. Defina a senha de e-mail e suba:

```bash
cd backend
export EMAIL_APP_PASSWORD="sua-senha-de-app"   # PowerShell: $env:EMAIL_APP_PASSWORD="..."
./gradlew :pedidos-service:bootRun
```

A tabela é criada automaticamente (`spring.jpa.hibernate.ddl-auto=update`).

## 🖼 Frontend (planejado)

**Status: não implementado.** A pasta `frontend/` na estrutura do repositório é um espaço reservado para um futuro cliente Angular — não contém código funcional, tela ou build no momento. Nada existe para rodar, testar ou avaliar ali.

Toda a funcionalidade do sistema hoje é acessada via API:
- **Swagger UI** (http://localhost:8080/swagger-ui.html) para explorar e testar endpoints interativamente.
- Ferramentas como Postman/Insomnia ou `curl` para chamadas manuais.

Quando o frontend for implementado, esta seção será atualizada com instruções de build e execução.

## 📁 Estrutura do projeto

```
pedidosservice/
├── docker-compose.yml          # Orquestra app + postgres + rabbitmq
├── .env.example                # Template das variáveis de ambiente
├── backend/
│   ├── Dockerfile              # Build multi-stage (compila e roda o jar)
│   └── pedidos-service/        # Módulo Spring Boot (arquitetura hexagonal)
│       └── src/main/java/com/andre/pedidosservice/
│           ├── user/           # Domínio de usuários + autenticação
│           ├── product/        # Domínio de produtos (catálogo)
│           ├── order/          # Domínio de pedidos + scheduler de expiração
│           ├── notification/   # RabbitMQ (publisher/consumer) + envio de e-mail
│           ├── security/       # JWT, filtros e configuração do Spring Security
│           └── exception/      # Tratamento global de erros
└── frontend/                   # Pasta reservada — sem implementação ainda (ver seção "Frontend (planejado)")
```

## 🛠 Solução de problemas

| Sintoma | Causa provável | Solução |
|---|---|---|
| `docker daemon is not running` | Docker Desktop fechado | Abra o Docker Desktop e aguarde iniciar |
| API reinicia/falha logo no boot | Banco ainda não estava pronto | `docker compose restart app` |
| `port is already allocated` | Porta 8080/5432/5672 ocupada | Libere a porta ou ajuste o mapeamento no `docker-compose.yml` |
| `401 Unauthorized` nas chamadas | Token ausente/expirado (vale 1h) | Faça login de novo e reenvie o header `Authorization` |
| `403 Forbidden` ao criar produto | Usuário não é `ADMIN` | Promova via `PATCH /user/{id}?status=ADMIN` e refaça o login |
| E-mail não chega | Cadastro com e-mail inexistente ou senha de app revogada, ou ação realizada não é criação/entrega de pedido | Cadastre com um e-mail real; gere nova senha de app se necessário; confira se a ação foi `POST /order` ou `PATCH .../order/{id}?status=ENTREGUE` |
| Alterei o código e nada muda | Imagem antiga em cache | `docker compose up -d --build` |
