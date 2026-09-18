# TODO LIST

### Items Feature

1. Remover EquippableContext de ConsumableItem
2. Remover ItemCategory de ConsumableItem
3. Manter apenas { AVATAR, BANNER, FRAME, EMOTE, BOARD, CELL } em ItemCategory
4. Renomear ItemCategory para EquippableCategory
5. Remover validações de null de ConsumableItem
6. Reescrever fluxo de troca de nome a partir de `ChangeNicknameController.java` para utilizar os novos tipos de itens (Item de trocar nome)
7. Adicionar uso de efeitos ativos do usuário ao atualizar os status após uma partida
8. Implementar todos os efeitos em `com/letraaletra/api/features/user/domain/effect/effects`
9. Corrigir fluxo de atualizar item para permitir a edição de todos os campos relevantes
10. Corrigir requests DTOs para que os controllers recebam formDatas e não Jsons
11. Organizar domínio com pastas (mesma organização de `User`)
12. Refazer a parte de Efeito de item para não depender do Jackson (Clean Architecture violada) mantendo o domínio limpo