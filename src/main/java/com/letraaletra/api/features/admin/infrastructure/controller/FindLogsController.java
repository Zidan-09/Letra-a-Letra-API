package com.letraaletra.api.features.admin.infrastructure.controller;

import com.letraaletra.api.shared.domain.AuthenticatedUser;
import com.letraaletra.api.shared.domain.security.exceptions.UserIsNotAdminException;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping(path = "/admin/logs")
@Tag(name = "Admin", description = "Rotas relacionadas a parte de administração")
public class FindLogsController {
    private final Path logDirectory = Paths.get("logs");

    @GetMapping("/{type}/{file}")
    public ResponseEntity<Resource> handle(
            @AuthenticationPrincipal AuthenticatedUser principal,
            @PathVariable @NotBlank String type,
            @PathVariable @NotBlank String file
    ) throws IOException {
        if (!principal.isAdmin()) {
            throw new UserIsNotAdminException();
        }

        Path filePath = logDirectory
                .resolve("audit")
                .resolve(type)
                .resolve(file)
                .normalize();

        if (!Files.exists(filePath) || !Files.isRegularFile(filePath)) {
            return ResponseEntity.notFound().build();
        }

        Resource resource = new FileSystemResource(filePath);

        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_PLAIN)
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + file + "\""
                )
                .body(resource);
    }
}
