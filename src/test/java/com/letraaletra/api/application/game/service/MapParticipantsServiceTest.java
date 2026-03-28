package com.letraaletra.api.application.game.service;

import com.letraaletra.api.domain.Game;
import com.letraaletra.api.domain.game.RoomSettings;
import com.letraaletra.api.domain.participant.Participant;
import com.letraaletra.api.domain.participant.ParticipantRole;
import com.letraaletra.api.presentation.dto.mappers.ParticipantDTOMapper;
import com.letraaletra.api.presentation.dto.response.participant.ParticipantDTO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

@ExtendWith(MockitoExtension.class)
class MapParticipantsServiceTest {

    @Mock
    private ParticipantDTOMapper participantDTOMapper;

    @InjectMocks
    private MapParticipantsService mapParticipants;

    @Test
    @DisplayName("Should return a List of Participants DTO")
    void execute() {
        Participant participant = new Participant("id", "sId", "test", "avatar1", ParticipantRole.PLAYER);
        RoomSettings settings = new RoomSettings(false, false);
        Game game = new Game("id", "name", settings, participant);

        ParticipantDTO expectedDTO = new ParticipantDTO("id", "test", "avatar1", ParticipantRole.PLAYER);

        Mockito.when(participantDTOMapper.toDTO(participant))
                .thenReturn(expectedDTO);

        List<ParticipantDTO> expectedParticipantListDTO = List.of(expectedDTO);

        List<ParticipantDTO> participantDTOS = mapParticipants.execute(game);

        Assertions.assertArrayEquals(expectedParticipantListDTO.toArray(), participantDTOS.toArray());
    }
}