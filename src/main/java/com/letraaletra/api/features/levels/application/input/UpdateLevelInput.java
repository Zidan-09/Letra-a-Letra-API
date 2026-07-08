package com.letraaletra.api.features.levels.application.input;

import java.util.List;
import java.util.UUID;

public record UpdateLevelInput(
        UUID auth,
        UUID levelId,
        int level,
        List<CreateLevelRewardInput> rewards
) {
}
