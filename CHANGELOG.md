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

### Infrastructure
- `.gitignore` atualizado para ignorar logs de teste da API e o diretório `docs/`.
