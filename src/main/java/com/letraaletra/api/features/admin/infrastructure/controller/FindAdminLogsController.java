package com.letraaletra.api.features.admin.infrastructure.controller;

import com.letraaletra.api.features.admin.domain.permission.PermissionAction;
import com.letraaletra.api.features.admin.domain.permission.PermissionKey;
import com.letraaletra.api.shared.application.port.AdminChecker;
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
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

@RestController
@RequestMapping("/admin/logs/admin")
@Tag(name = "Admin", description = "Rotas relacionadas aos logs administrativos")
public class FindAdminLogsController {

    private final Path root = Paths.get("logs", "admin");
    private final AdminChecker adminChecker;

    public FindAdminLogsController(
            AdminChecker adminChecker
    ) {
        this.adminChecker = adminChecker;
    }

    @GetMapping
    public List<String> findLogs(
            @AuthenticationPrincipal AuthenticatedUser principal
    ) throws IOException {

        adminChecker.check(principal, PermissionKey.LOGS, PermissionAction.VIEW);

        if (!Files.exists(root)) {
            return List.of();
        }

        try (Stream<Path> stream = Files.list(root)) {
            return stream
                    .filter(Files::isRegularFile)
                    .map(path -> path.getFileName().toString())
                    .sorted(Comparator.naturalOrder())
                    .toList();
        }
    }

    @GetMapping("/{file}")
    public ResponseEntity<Resource> download(
            @AuthenticationPrincipal AuthenticatedUser principal,
            @PathVariable @NotBlank String file
    ) {

        adminChecker.check(principal, PermissionKey.LOGS, PermissionAction.VIEW);

        Path filePath = root
                .resolve(file)
                .normalize();

        validatePath(filePath);

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

    private void validatePath(Path path) {
        Path normalizedRoot = root.toAbsolutePath().normalize();

        if (!path.toAbsolutePath().normalize().startsWith(normalizedRoot)) {
            throw new IllegalArgumentException("Invalid path.");
        }
    }
}