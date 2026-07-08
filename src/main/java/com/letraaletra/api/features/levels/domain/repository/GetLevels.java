package com.letraaletra.api.features.levels.domain.repository;

import com.letraaletra.api.features.levels.application.input.GetLevelsInput;
import com.letraaletra.api.features.levels.domain.Level;

import java.util.List;

public interface GetLevels {
    List<Level> get(GetLevelsInput input);
}
