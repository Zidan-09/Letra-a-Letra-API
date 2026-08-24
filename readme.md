# Letra a Letra — API

Backend do jogo **Letra a Letra**: um jogo de palavras multiplayer em tempo real, com salas customizadas, matchmaking casual e ranqueado, sistema de turnos com poderes, economia (carteira, loja, transações), níveis, cosméticos, amigos, tickets de suporte, auditoria e console administrativo.

- API HTTP: `http://<host>:8080`
- WebSocket de jogo: `ws://<host>:8080/ws/game?token=<USER_JWT>`
- Documentação OpenAPI (perfis `dev`/`test`): `http://localhost:8080/docs` (JSON) e `http://localhost:8080/index.html` (Swagger UI) — desabilitadas em produção

---

## Visão geral

| | |
|---|---|
| Linguagem | Java 21 |
| Framework | Spring Boot 4.0.5 (Maven) |
| Banco de dados | PostgreSQL 16 (dev/prod) · H2 em memória (testes) |
| Tempo real | WebSocket puro (`spring-boot-starter-websocket`) |
| Autenticação | JWT próprio (HS256, expiração de 6h) + login com Google |
| Armazenamento de assets | Cloudflare R2 (cosméticos) |

O projeto expõe uma API HTTP pública e dois canais WebSocket para ações em tempo real (salas/gameplay e console admin). A autenticação HTTP usa `Authorization: Bearer <JWT>`; o WebSocket recebe o JWT via query param `token`.

---

## Funcionalidades

Organizadas por feature (pacotes em `features/`):

### Usuário (`user`)
- Criação de conta local (email/senha) e autenticação por Google
- Login (`POST /user/auth`) retornando JWT; perfil próprio (`GET /user/me`)
- Alteração de nickname; busca por username; listagem de usuários (admin)
- Inventário de cosméticos (equipar/remover), carteira (`soft_coins`, `hard_gems`)
- Banimento/desbanimento, concessão e revogação de recompensas e itens (admin)
- Recuperação de senha por código enviado por email

### Jogo (`game`)
- Salas customizadas com código, host e configurações (permitir espectadores)
- Ciclo de estados `WAITING → RUNNING → CLOSED/CANCELED`; sala CUSTOM volta a `WAITING` após partida (reuso)
- Board gerado por temas (temas carregados de `resources/data/themes.json`)
- Fim de partida com pontuação e integração com ranking/estatísticas

### Matchmaking / fila (`matchmaking`, `queue`, `ranking`)
- Filas separadas casual e ranqueada; pareamento automático 2 a 2
- Partida criada e iniciada automaticamente no pareamento; notificação via WebSocket

### Participantes (`participant`)
- Entrada em sala (2 primeiros = jogadores, demais = espectadores; máximo 7)
- Moderação: kick, ban/unban (blacklist), troca de posição
- Reconexão automática e remoção por inatividade após desconexão (60s)

### Gameplay (`player`)
- Ações de turno durante partidas `RUNNING`: REVEAL, BLOCK, UNBLOCK, TRAP, DETECT_TRAPS, SPY, FREEZE, UNFREEZE, BLIND, LANTERN, IMMUNITY
- Descarte de poder (`DISCARD_POWER`)
- Turno inicial de 45s; 3 timeouts seguidos removem o jogador por inatividade

### Amigos (`friend`)
- Envio/aceite/rejeição de solicitações, listagem e remoção de amigos; notificação em tempo real

### Níveis (`levels`), Cosméticos (`cosmetic`), Recompensas (`reward`)
- CRUD de níveis com recompensas associadas
- Cadastro/gestão de cosméticos com upload para Cloudflare R2 (conversão WebP)
- Modelo de recompensas (moedas, gems ou cosmético) reapresentado por levels/offers/user

### Loja e ofertas (`shop`, `offers`)
- Catálogo de ofertas ativas, compra com débito em carteira
- Gestão de ofertas (CRUD, enable/disable) e expiração automática por scheduler

### Transações (`transaction`)
- Registro e consulta de transações da carteira (por usuário, username ou id)

### Tickets (`ticket`)
- Abertura de tickets de suporte pelo jogador; listagem/resolução pelo admin

### Auditoria (`audit`)
- Eventos estruturados de negócio (partidas, moderação, economia) persistidos em PostgreSQL
- Consulta por filtros, histórico por recurso e por usuário

### Administração (`admin`)
- Gestão de admins com permissões granulares por módulo (`VIEW/CREATE/EDIT/DELETE/TOGGLE`)
- Bootstrap inicial (seed do primeiro super admin) e migração de permissões
- Console em tempo real (`/ws/admin`): streaming de logs e métricas do sistema (1s)
- Consulta de logs técnicos de jogo por data/partida

---

## Arquitetura

O projeto segue **Feature First + Domain-Driven Design + Clean Architecture** (convenções detalhadas em [`AGENTS.md`](AGENTS.md)).

```
src/main/java/com/letraaletra/api/
├── features/
│   └── <feature>/
│       ├── domain/            # entidades, value objects, ports de repositório
│       ├── application/       # use cases (UseCase<I,O>), inputs/outputs, ports de aplicação
│       └── infrastructure/    # controllers, DTOs/mappers, persistência (JPA/memory),
│                             # websocket handlers, schedulers, configs, services
└── shared/
    ├── application/port/      # UseCase, OperationContext, AdminChecker, AuditService...
    ├── domain/                # AuthenticatedUser, SecurityMessages, security (JWT/Roles),
    │                          # PermissionKey/Action, MessageCode/DomainException
    └── infrastructure/
        ├── websocket/         # kernel de transporte (endpoint, router, registry, sender)
        ├── config/            # Security, CORS, Jackson, filtro JWT, etc.
        ├── audit/             # MDC, interceptor e log técnico (AUDIT_ADMIN/AUDIT_GAME)
        └── presentation/      # envelope HTTP (ErrorResponse/PageResponse/...)
```

**Regras de dependência:** `infrastructure → application → domain`. O núcleo `shared` fornece apenas
abstrações transversais e **não importa nada de `features.*`** — isso é garantido por teste
arquitetural (`SharedArchitectureIsolationTest`). Integrações entre features ocorrem por ports de
aplicação da feature dona (ex.: `audit.application.port.BusinessAuditRecorder`) ou por SPIs do kernel.

**Padrões relevantes:**
- **Ports & Adapters**: casos de uso dependem de interfaces; implementações ficam na infraestrutura.
- **Actor model** para concorrência de sala: cada `Game` é protegido por um actor com mailbox serializada (`GameActorManager`).
- **Kernel WebSocket** genérico: roteamento por tipo de mensagem (`RoomRequestHandler`), registro de conexões com lock por usuário, envio com envelope `eventId`; features plugam handlers/listeners/notificadores via contratos do kernel.
- **Schedulers**: pareamento de matchmaking (10ms), expiração de turno (10ms), fechamento de sala ociosa (5 min), expiração de ofertas (60s), publicação de métricas admin (1s).

---

## Stack tecnológica

| Tecnologia | Versão | Uso |
|---|---|---|
| Java | 21 | linguagem |
| Spring Boot | 4.0.5 | parent/starter (webmvc, websocket, security, validation, mail, actuator, data-jpa) |
| PostgreSQL | 16 | banco principal |
| H2 | — | banco em memória nos testes |
| JJWT | 0.12.6 | geração/validação de JWT |
| springdoc-openapi | 3.0.3 | Swagger UI/OpenAPI |
| Lombok | 1.18.42 | redução de boilerplate |
| Google API Client | 2.0.0 | login com Google |
| Cloudflare R2 (AWS SDK S3) | 2.31.74 | storage de cosméticos |
| webp-imageio | 0.1.6 | conversão de imagens de cosméticos |
| Node.js + ws | 22 / ^8.21.0 | testes de integração E2E |

---

## API REST

A especificação OpenAPI é gerada pelo springdoc: JSON em `/docs` e Swagger UI em `/index.html`
(perfis `dev`/`test`). Principais grupos de rotas:

| Domínio | Rotas |
|---|---|
| Usuário | `POST /user` · `POST /user/auth` · `POST /user/auth/google` · `GET /user/me` · `PATCH /user/nickname` · `GET /user` · `GET /user/username/{username}` · `GET /user/inventory` · `PATCH /user/cosmetic/{cosmeticId}` · `GET /user/transactions` · `POST /user/auth/forgot-password` · `POST /user/auth/verify-reset-code` · `POST /user/auth/reset-password` |
| Usuário (admin) | `PATCH /user/{userId}/ban` · `PATCH /user/{userId}/unban` · `PATCH /user/{userId}/grant-reward` · `DELETE /user/{userId}/inventory/{cosmeticId}` · `PATCH /user/{userId}/wallet/revoke` · `GET /user/{userId}/inventory` |
| Jogo | `GET /game` · `GET /game/public` · `GET /game/active` · `GET /game/code/{code}` |
| Níveis | `GET /level` · `GET /level/{levelId}` · `GET /level/value/{value}` · `POST /level` · `PUT /level/{levelId}` |
| Cosméticos | `GET /cosmetic` · `GET /cosmetic/name/{name}` · `GET /cosmetic/search` · `POST /cosmetic` · `PUT /cosmetic/{cosmeticId}` · `DELETE /cosmetic/{cosmeticId}` · `PATCH /cosmetic/enable/{id}` · `PATCH /cosmetic/disable/{id}` |
| Ofertas | `GET /offer` · `GET /offer/{offerId}` · `POST /offer` · `DELETE /offer/{offerId}` · `PATCH /offer/enable/{id}` · `PATCH /offer/disable/{id}` |
| Loja | `GET /shop/offers` · `POST /shop/offers/{offerId}/buy` |
| Amigos | `GET /friend` · `GET /friend/pending` · `POST /friend/request` · `PATCH /friend/accept` · `PATCH /friend/reject` · `PATCH /friend/remove` |
| Transações | `GET /transaction` · `GET /transaction/{id}` · `GET /transaction/user/{userId}` · `GET /transaction/user/username/{username}` |
| Tickets | `POST /ticket` · `GET /ticket/my` · `GET /ticket/{ticketId}` |
| Tickets (admin) | `GET /admin/ticket` · `PATCH /admin/ticket/{ticketId}/resolve` |
| Admin | `POST /admin` · `PATCH /admin/activate` · `POST /admin/auth` · `GET /admin/me` · `GET /admin` · `GET /admin/email/{email}` · `PUT /admin/{adminId}` · `DELETE /admin/{adminId}` · `POST /admin/auth/forgot-password` · `POST /admin/auth/verify-reset-token` · `POST /admin/auth/reset-password` |
| Auditoria / Logs | `GET /admin/audit` · `GET /admin/audit/resource/{type}/{id}` · `GET /admin/audit/user/{userId}` · `GET /admin/logs/game/{date}[...]` · `GET /admin/logs/admin/{file}` |

Rotas públicas (sem token): `/user`, `/user/auth/**`, `/admin/auth/**`, `/admin/activate`, `/ws/**`,
`/index.html`, `/swagger-ui/**`, `/docs`, `/docs/**`, `/actuator/health`. Demais rotas exigem
`Authorization: Bearer <JWT>`.

Formato padrão de resposta:

```json
{ "success": true, "message": "...", "data": { } }
```

```json
{ "success": false, "message": "error_code" }
```

---

## WebSocket

### Canal de jogo — `/ws/game?token=<USER_JWT>`

- Autenticação no handshake: query param `token` com JWT de usuário válido; caso contrário a conexão é recusada.
- Mensagens são JSON com campo `type` (requests) e recebidas como JSON com campo `event` + `eventId` (responses).
- Erros chegam como evento `ERROR` com a mensagem/código do domínio.

Requests suportados:

| `type` | Finalidade |
|---|---|
| `CREATE_GAME` | cria sala (host = criador); timeout de inatividade de 5 min |
| `JOIN_GAME` | entra na sala (posições 0–1 jogadores, 2–6 espectadores; máx. 7) |
| `LEFT_GAME` | sai da sala; pode encerrar a partida (`GAME_OVER`) |
| `START_GAME` | inicia partida (apenas host, mínimo 2 jogadores) |
| `SWAP_POSITION` | troca posição antes do início |
| `BAN_PARTICIPANT` / `KICK_PARTICIPANT` / `UNBAN_PARTICIPANT` | moderação |
| `MATCHMAKING_GAME` / `EXIT_MATCHMAKING` | entrar/sair da fila casual |
| `RANKING_GAME` / `EXIT_RANKING` | entrar/sair da fila ranqueada |
| `PLAYER_ACTION` | ação de gameplay (payload `action.type`: REVEAL, BLOCK, UNBLOCK, TRAP, DETECT_TRAPS, SPY, FREEZE, UNFREEZE, BLIND, LANTERN, IMMUNITY) |
| `DISCARD_POWER` | descarta poder do inventário |

Principais eventos: `GAME_CREATED`, `PARTICIPANT_JOIN`, `PARTICIPANT_LEAVE`, `GAME_STARTED`,
`PLAYER_ACTION_RESULT`, `POWER_DISCARDED`, `GAME_OVER`, `TURN_EXPIRED`, `ROOM_CLOSED`,
`PARTICIPANT_RECONNECTED`, `PARTICIPANT_DISCONNECTED`, `MODERATION_MESSAGE`, `POSITIONS_UPDATED`,
`MATCHMAKING_GAME`, `EXIT_MATCHMAKING`, `RANKING_GAME`, `EXIT_RANKING`, `ERROR`.

Comportamentos relevantes:
- Desconexão dispara `PARTICIPANT_DISCONNECTED`; reconexão dentro de 60s restaura o participante (`PARTICIPANT_RECONNECTED`); sem retorno, o jogador é removido.
- Sala ociosa sem início é fechada após 5 minutos (`ROOM_CLOSED`).
- Turno inicial de 45s; 3 turnos perdidos removem o jogador por inatividade.
- Sala CUSTOM retorna ao estado `WAITING` ao fim da partida, podendo ser reutilizada.

### Canal administrativo — `/ws/admin?token=<ADMIN_JWT>`

- Handshake exige JWT com role `ADMIN`.
- Streaming de logs da aplicação e métricas do sistema publicadas a cada 1 segundo.

---

## Banco de dados

- **PostgreSQL 16** nos ambientes dev/prod, provisionado pelo `docker-compose.yml` com volume persistente.
- Scripts de inicialização ficam em `docker/postgres/` e são montados em `/docker-entrypoint-initdb.d`.
- Aplicação valida o schema com Hibernate (`ddl-auto=validate`) e executa inicialização de dados SQL
  (`spring.sql.init.mode=always`); temas de palavras vêm de `resources/data/themes.json`.
- Nos testes unitários é usado **H2 em memória** (`create-drop`), sem dependências externas além do MailHog nos testes de integração.

---

## Execução local

### Pré-requisitos

- JDK 21 (Temurin recomendado)
- Docker e Docker Compose
- (opcional, para testes de integração) Node.js 22

### Variáveis de ambiente

Arquivo `.env` é usado pelo container de produção (`scripts/start`) e `.env.dev` pelo Compose.
Variáveis esperadas:

| Variável | Uso |
|---|---|
| `SPRING_PROFILES_ACTIVE` | perfil Spring (`dev`, `prod`) |
| `PORT` | porta HTTP (padrão 8080) |
| `DB_NAME` / `DB_URL` / `DB_USER` / `DB_PASSWORD` | conexão PostgreSQL |
| `JWT_SECRET` | segredo HS256 dos tokens (mínimo 32 caracteres) |
| `CLIENT_ID` | client id do login Google |
| `CLOUDFLARE_TOKEN` / `CLOUDFLARE_ACCESS_KEY_ID` / `CLOUDFLARE_SECRET_ACCESS_KEY` | credenciais R2 |
| `CLOUDFLARE_BUCKET_NAME` / `CLOUDFLARE_PUBLIC_URL` | bucket e URL pública dos cosméticos |
| `MAIL_USERNAME` / `MAIL_PASSWORD` | SMTP em produção |

### Subindo o ambiente de desenvolvimento

```bash
# sobe postgres + mailhog + api (rebuild incluso)
docker compose --env-file .env.dev up --build

# equivalente via script
./scripts/dev.sh        # ou scripts\dev.bat no Windows
```

Serviços: API em `http://localhost:8080`, UI do MailHog em `http://localhost:8025`, Postgres em `5432`.

### Produção (container único)

```bash
./scripts/start.sh      # builda imagem letra-a-letra-api e sobe com .env   (start.bat)
./scripts/restart.sh    # reinicia                                              (restart.bat)
./scripts/stop.sh       # para                                                  (stop.bat)
```

### Executando apenas a aplicação (sem Docker)

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

---

## Testes

```bash
# testes unitários (H2 em memória, sem serviços externos)
./mvnw test

# build completo com testes
./mvnw clean package

# suíte completa: unitários + integração E2E (sobe MailHog e a API no perfil test)
./scripts/test.sh       # ou scripts\test.bat no Windows
```

- **Unitários:** JUnit 5 + Mockito; cobrem domínio, casos de uso, controllers (mock MVC), adaptadores JPA e infraestrutura de WebSocket.
- **Integração/E2E:** scripts Node.js em `tools/` (biblioteca `ws`) exercendo os fluxos reais — auth/profile, friends, matchmaking, ranking, casual, room, turnos e admin — contra a API rodando no perfil `test` (`node tools/runner.js`).
- **CI:** GitHub Actions (`.github/workflows/ci.yml`) roda unitários e depois os testes de integração (JDK 21, Node 22, serviço MailHog) em PRs para `develop` e `main`.

---

## Desenvolvimento

- Convenções completas em [`AGENTS.md`](AGENTS.md): Feature First, DDD, Clean Architecture,
  Clean Code/SOLID, testes obrigatórios por camada e build final com `mvnw clean package`.
- Toda funcionalidade vive em `features/<nome>/` com as três camadas; código genuinamente
  transversal vai para `shared` — que **não pode depender de features** (regra verificada por teste).
- Nomenclatura: casos de uso terminam com `UseCase`, entradas com `Input`, saídas com `Output`;
  portas de repositório são interfaces no `domain` e implementações ficam em `infrastructure`.
- Para criar uma feature nova com WebSocket basta adicionar DTOs (`@JsonTypeName`) e handlers
  (`RoomRequestHandler`) dentro da própria feature — o kernel descobre tudo automaticamente.

## Releases

O repositório trabalha com as branches `main`, `develop` e branches de release (ex.: `release/1.0`,
branch atual). **Ainda não existem tags de versão publicadas** — o histórico consolidado de mudanças
está no [`CHANGELOG.md`](CHANGELOG.md), iniciando pela seção `[Unreleased]`, baseada no delta entre
`origin/main` e a branch de release atual.
