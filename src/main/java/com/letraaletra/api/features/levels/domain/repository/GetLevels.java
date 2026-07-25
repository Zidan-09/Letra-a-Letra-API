package com.letraaletra.api.features.levels.domain.repository;

import com.letraaletra.api.features.levels.application.input.GetLevelsInput;
import com.letraaletra.api.features.levels.domain.Level;
import org.springframework.data.domain.Page;

public interface GetLevels {
    Page<Level> get(GetLevelsInput input);
}
