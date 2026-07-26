package com.letraaletra.api.features.transaction.infrastructure.presentation.mapper;

import com.letraaletra.api.features.transaction.application.input.GetTransactionsInput;
import com.letraaletra.api.features.transaction.application.output.GetTransactionsOutput;
import com.letraaletra.api.features.transaction.domain.Transaction;
import com.letraaletra.api.shared.infrastructure.presentation.dto.response.PageResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.UUID;

public class GetTransactionsMapper {
    public static GetTransactionsInput toInput(UUID auth, Pageable pageable) {
        Pageable pages = pageable == null ?
                PageRequest.of(0, 20, Sort.Direction.ASC) :
                pageable;

        return new GetTransactionsInput(
                auth,
                pages.getPageNumber(),
                pages.getPageSize(),
                pages.getSort()
        );
    }

    public static PageResponse<Transaction> toResponse(GetTransactionsOutput output) {
        Page<Transaction> page = output.transactions();

        return new PageResponse<>(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isFirst(),
                page.isLast()
        );
    }
}
