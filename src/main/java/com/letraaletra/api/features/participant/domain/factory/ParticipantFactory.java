package com.letraaletra.api.features.participant.domain.factory;

import com.letraaletra.api.features.participant.domain.Participant;
import com.letraaletra.api.features.user.domain.User;
import com.letraaletra.api.features.user.domain.inventory.InventoryItem;

public class ParticipantFactory {
    public static Participant fromUser(User user, String sessionId) {
        return new Participant(
                user.getId(),
                sessionId,
                user.getNickname(),
                user.getInventory().stream().filter(InventoryItem::equipped).toList()
        );
    }
}
