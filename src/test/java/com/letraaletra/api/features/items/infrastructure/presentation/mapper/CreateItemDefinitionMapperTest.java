package com.letraaletra.api.features.items.infrastructure.presentation.mapper;

import com.letraaletra.api.features.items.application.input.CreateItemDefinitionInput;
import com.letraaletra.api.features.items.domain.ItemCategory;
import com.letraaletra.api.features.items.domain.ItemContext;
import com.letraaletra.api.features.items.domain.ItemKind;
import com.letraaletra.api.features.items.infrastructure.presentation.dto.request.CreateItemDefinitionRequest;
import com.letraaletra.api.shared.domain.AuthenticatedUser;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class CreateItemDefinitionMapperTest {

    private final AuthenticatedUser principal = new AuthenticatedUser(UUID.randomUUID(), "admin", true, false);

    private CreateItemDefinitionRequest request() {
        return new CreateItemDefinitionRequest(
                "Blue Avatar",
                ItemKind.COSMETIC,
                ItemCategory.AVATAR,
                Set.of(ItemContext.PROFILE),
                false,
                null,
                false,
                null
        );
    }

    @Test
    @DisplayName("asset enviado deve virar upload no input sem assetPath manual")
    void assetShouldBecomeUploadWithoutManualAssetPath() {
        MockMultipartFile asset = new MockMultipartFile(
                "asset", "avatar.png", "image/png", new byte[]{1, 2, 3});

        CreateItemDefinitionInput input = CreateItemDefinitionMapper.toInput(principal, request(), asset);

        assertNotNull(input.asset());
        assertArrayEquals(new byte[]{1, 2, 3}, input.asset().content());
        assertEquals("image/png", input.asset().contentType());
    }

    @Test
    @DisplayName("ausencia de asset deve gerar upload nulo")
    void missingAssetShouldMapToNull() {
        CreateItemDefinitionInput input = CreateItemDefinitionMapper.toInput(principal, request(), null);

        assertNull(input.asset());
    }
}
