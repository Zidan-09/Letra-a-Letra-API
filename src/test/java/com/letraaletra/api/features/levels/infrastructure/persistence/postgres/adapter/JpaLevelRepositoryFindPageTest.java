package com.letraaletra.api.features.levels.infrastructure.persistence.postgres.adapter;

import com.letraaletra.api.features.levels.domain.LevelsPage;
import com.letraaletra.api.features.levels.infrastructure.persistence.postgres.jpa.SpringDataLevelRepository;
import com.letraaletra.api.features.levels.infrastructure.persistence.postgres.jpa.SpringDataLevelRewardRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JpaLevelRepositoryFindPageTest {

    @Mock
    private SpringDataLevelRepository repository;

    @Mock
    private SpringDataLevelRewardRepository levelRewardRepository;

    @Mock
    private JdbcTemplate jdbcTemplate;

    private JpaLevelRepository adapter() {
        return new JpaLevelRepository(repository, levelRewardRepository, jdbcTemplate);
    }

    @Test
    @DisplayName("get deve chamar a procedure com 4 args e sort default")
    void getShouldCallProcedureWithDefaultSort() {
        when(jdbcTemplate.query(
                eq("SELECT * FROM sp_level_find_page(?, ?, ?, ?)"),
                any(RowMapper.class),
                eq(20), eq(0), eq("level"), eq("ASC")))
                .thenReturn(List.of());

        Page<?> page = adapter().get(new LevelsPage(0, 20, Sort.unsorted()));

        assertEquals(0, page.getTotalElements());
        verify(jdbcTemplate).query(
                eq("SELECT * FROM sp_level_find_page(?, ?, ?, ?)"),
                any(RowMapper.class),
                eq(20), eq(0), eq("level"), eq("ASC"));
    }

    @Test
    @DisplayName("get deve repassar direcao DESC")
    void getShouldPassDescendingDirection() {
        when(jdbcTemplate.query(
                eq("SELECT * FROM sp_level_find_page(?, ?, ?, ?)"),
                any(RowMapper.class),
                eq(10), eq(0), eq("level"), eq("DESC")))
                .thenReturn(List.of());

        adapter().get(new LevelsPage(0, 10, Sort.by(Sort.Order.desc("level"))));

        verify(jdbcTemplate).query(
                eq("SELECT * FROM sp_level_find_page(?, ?, ?, ?)"),
                any(RowMapper.class),
                eq(10), eq(0), eq("level"), eq("DESC"));
    }
}
