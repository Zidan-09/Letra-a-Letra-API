# Changelog

Todas as mudanças relevantes deste projeto serão documentadas neste arquivo.

O formato segue as convenções do [Keep a Changelog](https://keepachangelog.com/). O projeto ainda
**não possui releases tagueadas** no Git; por isso, abaixo existe apenas a seção `[Unreleased]`,
que consolida as mudanças da branch de release atual (`release/1.0`) em relação à `main`
(`origin/main..HEAD`). Quando a primeira versão for tagueada, esta seção deverá ser renomeada com
a versão e a data reais.

## [Unreleased]

### Added
- Cobertura de testes unitários para as funcionalidades de tickets e de auditoria.
- Testes arquiteturais automatizados garantindo que o núcleo `shared` não dependa de nenhuma feature.
- Testes do handler global de exceções, fixando os payloads de erro HTTP.

### Fixed
- Corrigidos os percentuais de raridade dos poderes no modo Cataclysm.
- Corrigida a seleção do arquivo `.env` incorreta no script de start.

### Refactored
- Concluída a reorganização arquitetural do núcleo `shared`: componentes específicos de features
  (bootstrap de admin, verificação de permissões, exceções e DTOs de reward) foram movidos para suas
  features proprietárias, ports invertidos para as camadas de aplicação corretas e o filtro de
  autenticação JWT passou a delegar resolução de principal às features via SPI — eliminando todas as
  dependências de `shared` para `features.*`.
- Atualização mecânica de imports nos testes após a movimentação das permissões e dos actors.
- Remoção de imports não utilizados.
- Inventário genérico de itens (`features/inventory/`, em paralelo ao legado): catálogo
  `ItemDefinition` (`kind`/`category`/`applicability`), posse `UserItem` com quantidade, agregado
  `Inventory` com policies, UseCases (`grant`/`consume`/`equip`/`revoke`/consultas), API
  (`GET /user/items`, `POST …/consume`, `POST …/equip`, catálogo em `/admin/items`), `Reward`
  genérico (`ItemGrantReward`, `RewardType.ITEM`) e eventos de auditoria `ITEM_*` — com
  compatibilidade total das rotas/respostas legadas, dual-write JPA + procedures (`09`, `10` e
  `11_*` validados em Postgres 16) e remoção do fallback silencioso `AVATAR`. Remoção integral
  do legado pendente de `User` migrado + janela estável (ver `docs/plan.md`).
- **Remoção integral do inventário legado (Etapa 8b, ver `docs/step18.md`):** removidas as
  tabelas `user_inventory`/`cosmetic` (script `docker/postgres/12_drop_legacy_inventory.sql`,
  com migração `COSMETIC → ITEM` em `offer_reward`/`level_reward`), as procedures
  `sp_grant_cosmetic`/`sp_revoke_cosmetic`, o ramo `COSMETIC` de `sp_buy_offer` e a coluna
  `inventory` de `sp_user_save`/`sp_user_find_*`; removida a feature `features/cosmetic/`
  (catálogo passa a ser gerido em `/admin/items`), o pacote `user/domain/inventory/`, os
  UseCases/controllers/DTOs legados (`ChangeCosmetic`, `RevokeUserCosmetic`, `GetMy/GetUserInventory`,
  rotas `/user/inventory`, `/user/cosmetic/*`), `CosmeticReward`/`RewardType.COSMETIC` e os
  aliases `cosmeticId`/`type`/`unlockedAt` (payloads passam a usar `itemId`/`category`/`quantity`/`context`,
  eventos `ITEM_*`). **Breaking changes:** rotas `/cosmetic/*`, `/user/inventory`,
  `/user/{id}/inventory`, `PATCH /user/cosmetic/*` removidas; `rewardType=COSMETIC` não é mais
  aceito (usar `ITEM`); `ParticipantResponse`/`PlayerResponse` expõem `EquippedCosmetic`
  (`itemId`, `name`, `category`, `equipped`, `assetPath`).

### Infrastructure
- `.gitignore` atualizado para ignorar logs de teste da API e o diretório `docs/`.
