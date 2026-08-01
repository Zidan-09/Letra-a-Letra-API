package com.letraaletra.api.features.admin.infrastructure.controller;

import com.letraaletra.api.features.admin.domain.permission.PermissionAction;
import com.letraaletra.api.features.admin.domain.permission.PermissionKey;
import com.letraaletra.api.shared.application.port.AdminChecker;
import com.letraaletra.api.shared.domain.AuthenticatedUser;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

@RestController
@RequestMapping("/admin/logs/game")
@Tag(name = "Admin", description = "Logs das partidas")
public class FindGameLogsController {

    private final Path root = Paths.get("logs", "game");
    private final AdminChecker adminChecker;

    public FindGameLogsController(
            AdminChecker adminChecker
    ) {
        this.adminChecker = adminChecker;
    }

    @GetMapping
    public List<String> findDates(
            @AuthenticationPrincipal AuthenticatedUser principal
    ) throws IOException {

        adminChecker.check(principal, PermissionKey.LOGS, PermissionAction.VIEW);

        if (!Files.exists(root)) {
            return List.of();
        }

        try (Stream<Path> stream = Files.list(root)) {
            return stream
                    .filter(Files::isDirectory)
                    .map(path -> path.getFileName().toString())
                    .filter(name -> !name.equals("untracked"))
                    .sorted(Comparator.reverseOrder())
                    .toList();
        }
    }

    @GetMapping("/{date}")
    public List<String> findGames(
            @AuthenticationPrincipal AuthenticatedUser principal,
            @PathVariable @NotBlank String date
    ) throws IOException {

        adminChecker.check(principal, PermissionKey.LOGS, PermissionAction.VIEW);

        Path directory = root.resolve(date).normalize();

        validatePath(directory);

        if (!Files.exists(directory)) {
            return List.of();
        }

        try (Stream<Path> stream = Files.list(directory)) {
            return stream
                    .filter(Files::isDirectory)
                    .map(path -> path.getFileName().toString())
                    .sorted()
                    .toList();
        }
    }

    @GetMapping("/{date}/{gameId}")
    public List<String> findFiles(
            @AuthenticationPrincipal AuthenticatedUser principal,
            @PathVariable @NotBlank String date,
            @PathVariable @NotBlank String gameId
    ) throws IOException {

        adminChecker.check(principal, PermissionKey.LOGS, PermissionAction.VIEW);

        Path directory = root
                .resolve(date)
                .resolve(gameId)
                .normalize();

        validatePath(directory);

        if (!Files.exists(directory)) {
            return List.of();
        }

        try (Stream<Path> stream = Files.list(directory)) {
            return stream
                    .filter(Files::isRegularFile)
                    .map(path -> path.getFileName().toString())
                    .sorted()
                    .toList();
        }
    }

    @GetMapping("/{date}/{gameId}/{file}")
    public ResponseEntity<Resource> download(
            @AuthenticationPrincipal AuthenticatedUser principal,
            @PathVariable @NotBlank String date,
            @PathVariable @NotBlank String gameId,
            @PathVariable @NotBlank String file
    ) {

        adminChecker.check(principal, PermissionKey.LOGS, PermissionAction.VIEW);

        Path filePath = root
                .resolve(date)
                .resolve(gameId)
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

    @GetMapping("/untracked")
    public List<String> findLogs(
            @AuthenticationPrincipal AuthenticatedUser principal
    ) throws IOException {

        adminChecker.check(principal, PermissionKey.LOGS, PermissionAction.VIEW);

        if (!Files.exists(root.resolve("untracked"))) {
            return List.of();
        }

        Path directory = root
                .resolve("untracked")
                .normalize();

        validatePath(directory);

        if (!Files.exists(directory)) {
            return List.of();
        }

        try (Stream<Path> stream = Files.list(directory)) {
            return stream
                    .filter(Files::isRegularFile)
                    .map(path -> path.getFileName().toString())
                    .sorted()
                    .toList();
        }
    }

    @GetMapping(path = "/untracked/{file}")
    public ResponseEntity<Resource> downloadUntracked(
            @AuthenticationPrincipal AuthenticatedUser principal,
            @PathVariable @NotBlank String file
    ) {
        adminChecker.check(principal, PermissionKey.LOGS, PermissionAction.VIEW);

        Path filePath = root
                .resolve("untracked")
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