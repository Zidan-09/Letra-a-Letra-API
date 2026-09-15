package com.letraaletra.api.features.offers.infrastructure.persistence.postgres.adapter;

import com.letraaletra.api.features.offers.domain.OffersPage;
import com.letraaletra.api.features.offers.infrastructure.persistence.postgres.jpa.SpringDataOfferRepository;
import com.letraaletra.api.features.offers.infrastructure.persistence.postgres.jpa.SpringDataOfferRewardRepository;
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
class JpaOfferRepositoryFindPageTest {

    @Mock
    private SpringDataOfferRepository repository;

    @Mock
    private SpringDataOfferRewardRepository offerRewardRepository;

    @Mock
    private JdbcTemplate jdbcTemplate;

    private JpaOfferRepository adapter() {
        return new JpaOfferRepository(repository, offerRewardRepository, jdbcTemplate);
    }

    @Test
    @DisplayName("get deve chamar a procedure com 4 args e sort default")
    void getShouldCallProcedureWithDefaultSort() {
        when(jdbcTemplate.query(
                eq("SELECT * FROM sp_offer_find_page(?, ?, ?, ?)"),
                any(RowMapper.class),
                eq(20), eq(0), eq("created_at"), eq("ASC")))
                .thenReturn(List.of());

        Page<?> page = adapter().get(new OffersPage(0, 20, Sort.unsorted()));

        assertEquals(0, page.getTotalElements());
        verify(jdbcTemplate).query(
                eq("SELECT * FROM sp_offer_find_page(?, ?, ?, ?)"),
                any(RowMapper.class),
                eq(20), eq(0), eq("created_at"), eq("ASC"));
    }

    @Test
    @DisplayName("get deve traduzir sort camelCase para coluna snake_case com direcao")
    void getShouldMapSortToColumnAndDirection() {
        when(jdbcTemplate.query(
                eq("SELECT * FROM sp_offer_find_page(?, ?, ?, ?)"),
                any(RowMapper.class),
                eq(10), eq(10), eq("price"), eq("DESC")))
                .thenReturn(List.of());

        adapter().get(new OffersPage(1, 10, Sort.by(Sort.Order.desc("price"))));

        verify(jdbcTemplate).query(
                eq("SELECT * FROM sp_offer_find_page(?, ?, ?, ?)"),
                any(RowMapper.class),
                eq(10), eq(10), eq("price"), eq("DESC"));
    }

    @Test
    @DisplayName("get deve mapear createdAt para created_at")
    void getShouldMapCreatedAt() {
        when(jdbcTemplate.query(
                eq("SELECT * FROM sp_offer_find_page(?, ?, ?, ?)"),
                any(RowMapper.class),
                eq(10), eq(0), eq("created_at"), eq("ASC")))
                .thenReturn(List.of());

        adapter().get(new OffersPage(0, 10, Sort.by("createdAt")));

        verify(jdbcTemplate).query(
                eq("SELECT * FROM sp_offer_find_page(?, ?, ?, ?)"),
                any(RowMapper.class),
                eq(10), eq(0), eq("created_at"), eq("ASC"));
    }
}
