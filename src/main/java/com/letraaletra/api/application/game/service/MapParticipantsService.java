package com.letraaletra.api.application.game.service;

import com.letraaletra.api.domain.Game;
import com.letraaletra.api.presentation.mappers.game.ParticipantDTOMapper;
import com.letraaletra.api.presentation.dto.response.participant.ParticipantDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MapParticipantsService {
    @Autowired
    private ParticipantDTOMapper participantDTOMapper;

    public List<ParticipantDTO> execute(Game game) {
        return game.getParticipants().stream()
                .map(participantDTOMapper::toDTO)
                .toList();
    }
}
