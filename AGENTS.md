## Objetivo

Este projeto segue uma arquitetura baseada em Feature First, DDD e Clean Architecture.
Este projeto obedece os princípios de Clean Code e SOLID.

Toda alteração deve respeitar as regras definidas neste documento.

---

# Arquitetura

## Princípios

* Utilizar Feature First.
* Utilizar Domain-Driven Design (DDD).
* Utilizar Clean Architecture.
* Priorizar alta coesão e baixo acoplamento.
* Evitar lógica de negócio fora da camada de domínio ou aplicação.

## Estrutura

```text
features/
└── feature-name/
    ├── domain/
    ├── application/
    └── infrastructure/
```

### Domain

Responsável por:

* Entidades
* Value Objects
* Serviços de Domínio
* Eventos de Domínio
* Contratos (Repositories, Gateways)

Não pode depender de:

* Frameworks
* Banco de dados
* Controllers
* APIs externas

### Application

Responsável por:

* Casos de uso
* DTOs de entrada e saída
* Orquestração de regras de negócio

Não pode depender de:

* Frameworks
* Controllers
* Infraestrutura

Pode depender apenas do domínio.

### Infrastructure

Responsável por:

* Persistência
* APIs externas
* Mensageria
* Configurações de framework
* Controllers 
  * Validação de entrada
  * Endpoints
* Presentation
  * Conversão HTTP ↔ Application

Implementa contratos definidos pelo domínio.

---

# Convenções

## Dependências

Fluxo permitido:

```text
Infrastructure (Presentation)
    ↓
Application
    ↓
Domain

Infrastructure → Domain
```

Fluxos proibidos:

```text
Domain → Infrastructure
Domain → Application
Application → Infrastructure
```

## Injeção de Dependências

* Utilizar interfaces definidas no domínio.
* Depender de abstrações e não de implementações.
* Nunca instanciar dependências diretamente dentro dos casos de uso.

---

# Nomenclatura

## Casos de Uso

Todo caso de uso deve terminar com:

```text
UseCase
```

Exemplos:

```text
CreateMatchUseCase
MovePlayerUseCase
UsePowerUseCase
```

## Entradas

Toda entrada de caso de uso deve terminar com:

```text
Input
```

Exemplos:

```text
CreateMatchInput
MovePlayerInput
UsePowerInput
```

## Saídas

Toda saída de caso de uso deve terminar com:

```text
Output
```

Exemplos:

```text
CreateMatchOutput
MovePlayerOutput
```

## Repositórios

* Devem ser interfaces.
* Devem estar na camada Domain.

Exemplos:

```text
PlayerRepository
MatchRepository
```

## Implementações de Repositório

Devem ficar em Infrastructure.

Exemplos:

```text
JpaPlayerRepository
JpaMatchRepository
```

---

# Código

## Regras Gerais

* Preferir composição em vez de herança.
* Evitar código duplicado.
* Evitar métodos excessivamente longos.
* Evitar classes com múltiplas responsabilidades.
* Utilizar nomes explícitos e descritivos.
* Não utilizar comentários para explicar código simples.
* Código deve ser autoexplicativo.

## Tratamento de Erros

* Utilizar exceções específicas de domínio quando necessário.
* Não lançar exceções genéricas.
* Mensagens de erro devem ser claras.

---

# Testes

## Obrigatórios

Toda implementação deve possuir testes.

### Domain

* Testes unitários obrigatórios.

### Application

* Testes unitários obrigatórios.
* Casos de uso devem possuir cobertura dos fluxos principais.

### Infrastructure

* Testar implementações críticas.
* Testes de controller obrigatórios.

## Regras

* Não remover testes existentes.
* Atualizar testes quando regras de negócio forem alteradas.
* Novas funcionalidades devem incluir testes correspondentes.

---

# Ao gerar código

Sempre:

1. Respeitar a estrutura Feature First.
2. Respeitar DDD e Clean Architecture.
3. Respeitar Clean Code e SOLID.
4. Criar testes quando criar funcionalidades.
5. Manter dependências apontando para dentro.
6. Evitar lógica de negócio em Controllers.
7. Evitar dependência de framework no Domain.
8. Evitar dependência de framework no Application.
9. Preferir soluções simples e legíveis.
10. Não criar código morto ou não utilizado.
11. Não modificar arquitetura sem solicitação explícita.
12. Após finalizar tudo tente compilar o projeto.

## Build e Testes

mvnw clean package
```
```
