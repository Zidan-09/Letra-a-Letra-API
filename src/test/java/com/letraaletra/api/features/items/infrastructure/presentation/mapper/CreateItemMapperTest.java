package com.letraaletra.api.features.items.infrastructure.presentation.mapper;

import com.letraaletra.api.features.items.application.input.CreateItemInput;
import com.letraaletra.api.features.items.domain.equippable.EquippableContext;
import com.letraaletra.api.features.items.domain.equippable.EquippableCategory;
import com.letraaletra.api.features.items.domain.ItemKind;
import com.letraaletra.api.features.items.infrastructure.presentation.dto.request.CreateItemRequest;
import com.letraaletra.api.shared.domain.AuthenticatedUser;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class CreateItemMapperTest {

    private final AuthenticatedUser principal = new AuthenticatedUser(UUID.randomUUID(), "admin", true, false);

    private CreateItemRequest request() {
        MockMultipartFile asset = new MockMultipartFile(
                "asset", "avatar.png", "image/png", new byte[]{1, 2, 3});

        return new CreateItemRequest(
                "Blue Avatar",
                ItemKind.EQUIPPABLE,
                EquippableCategory.AVATAR,
                EquippableContext.PROFILE,
                null,
                null,
                null,
                null,
                asset
        );
    }

    @Test
    @DisplayName("asset enviado deve virar upload no input sem assetPath manual")
    void assetShouldBecomeUploadWithoutManualAssetPath() {
        CreateItemInput input = CreateItemMapper.toInput(principal, request());

        assertNotNull(input.asset());
        assertArrayEquals(new byte[]{1, 2, 3}, input.asset().content());
        assertEquals("image/png", input.asset().contentType());
    }

    @Test
    @DisplayName("ausencia de asset deve gerar upload nulo")
    void missingAssetShouldMapToNull() {
        CreateItemRequest withoutAsset = new CreateItemRequest(
                "Blue Avatar",
                ItemKind.EQUIPPABLE,
                EquippableCategory.AVATAR,
                EquippableContext.PROFILE,
                null,
                null,
                null,
                null,
                null
        );

        CreateItemInput input = CreateItemMapper.toInput(principal, withoutAsset);

        assertNull(input.asset());
    }
}
