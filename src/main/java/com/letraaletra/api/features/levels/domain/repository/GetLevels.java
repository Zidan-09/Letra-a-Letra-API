package com.letraaletra.api.features.levels.domain.repository;

import com.letraaletra.api.features.levels.domain.Level;
import com.letraaletra.api.features.levels.domain.LevelsPage;
import org.springframework.data.domain.Page;

public interface GetLevels {
    Page<Level> get(LevelsPage page);
}
