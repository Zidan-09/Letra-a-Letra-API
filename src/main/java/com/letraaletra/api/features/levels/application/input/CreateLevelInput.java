package com.letraaletra.api.features.levels.application.input;

import java.util.List;
import java.util.UUID;

public record CreateLevelInput(
        UUID auth,
        int level,
        List<CreateLevelRewardInput> rewards
) {
}
