package com.letraaletra.api.features.participant.infrastructure.websocket.handlers;

import com.letraaletra.api.features.participant.application.port.ParticipantNotifier;
import com.letraaletra.api.features.participant.domain.Participant;
import com.letraaletra.api.features.participant.application.input.ReconnectParticipantInput;
import com.letraaletra.api.features.participant.application.output.ReconnectParticipantOutput;
import com.letraaletra.api.features.participant.infrastructure.presentation.dto.response.ReconnectParticipantResponse;
import com.letraaletra.api.features.participant.infrastructure.presentation.mapper.ReconnectParticipantMapper;
import com.letraaletra.api.shared.application.usecase.UseCase;
import com.letraaletra.api.shared.domain.DomainException;
import com.letraaletra.api.shared.infrastructure.websocket.WsLifecycleListener;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.socket.WebSocketSession;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class ParticipantReconnectListener implements WsLifecycleListener {
    private final UseCase<ReconnectParticipantInput, Optional<ReconnectParticipantOutput>> useCase;
    private final ParticipantNotifier participantNotifier;

    @Transactional
    @Override
    public void onConnected(WebSocketSession session) {
        String userId = (String) session.getAttributes().get("userId");

        if (userId == null) {
            log.warn("ParticipantReconnectListener.onConnected without userId, session={}", session.getId());
            return;
        }

        try {
            ReconnectParticipantInput command = ReconnectParticipantMapper.toInput(userId, session.getId());

            Optional<ReconnectParticipantOutput> output = useCase.execute(command);

            output.ifPresent(out -> {
                try {
                    ReconnectParticipantResponse dto = ReconnectParticipantMapper.toResponse(out);

                    List<String> socketIds = out.game().getParticipants().getParticipants().stream()
                            .map(Participant::getSocketId)
                            .toList();

                    participantNotifier.notifyAll(socketIds, dto);
                    log.info("participant reconnected user={} game={} status={}", userId, out.game().getId(), out.game().getGameStatus());
                } catch (DomainException e) {
                    log.warn("failed to map reconnect response user={} game={}: {}", userId, out.game().getId(), e.getMessage());
                } catch (Exception e) {
                    log.error("unexpected error mapping reconnect user={} game={}", userId, out.game().getId(), e);
                }
            });
        } catch (Exception e) {
            log.error("reconnect useCase failed user={} session={}", userId, session.getId(), e);
        }
    }

    @Override
    public void onDisconnected(WebSocketSession session) {
    }
}
