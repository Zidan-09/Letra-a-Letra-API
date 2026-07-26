package com.letraaletra.api.features.ranking.infrastructure.presentation.mapper;

import com.letraaletra.api.features.participant.domain.Participant;
import com.letraaletra.api.features.player.domain.Player;
import com.letraaletra.api.features.player.infrastructure.presentation.mapper.PlayerDTOMapper;
import com.letraaletra.api.features.ranking.domain.UpdateRankingPoints;
import com.letraaletra.api.features.ranking.infrastructure.presentation.dto.response.RankedMatchResult;

public class RankingMatchResultMapper {
    public static RankedMatchResult toDto(Player player, Participant participant, UpdateRankingPoints points) {
        return new RankedMatchResult(
                PlayerDTOMapper.toDTO(player, participant),
                points.before(),
                points.changed(),
                points.after()
        );
    }
}
