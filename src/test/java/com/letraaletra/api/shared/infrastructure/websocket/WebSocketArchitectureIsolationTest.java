package com.letraaletra.api.shared.infrastructure.websocket;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Isolamento arquitetural do kernel de WebSocket")
class WebSocketArchitectureIsolationTest {

    private static final String MAIN_ROOT = "src/main/java/com/letraaletra/api";

    @Test
    @DisplayName("o kernel shared.infrastructure.websocket não importa nenhuma feature")
    void kernelMustNotImportFeatures() throws IOException {
        Path kernelRoot = Paths.get(MAIN_ROOT, "shared/infrastructure/websocket");

        try (Stream<Path> sources = Files.walk(kernelRoot)) {
            List<String> violations = sources
                    .filter(p -> p.toString().endsWith(".java"))
                    .map(WebSocketArchitectureIsolationTest::readFully)
                    .flatMap(content -> content.lines()
                            .filter(line -> line.trim().startsWith("import "))
                            .filter(line -> line.contains("com.letraaletra.api.features")))
                    .toList();

            assertTrue(violations.isEmpty(),
                    "kernel não pode conhecer features, violações: " + violations);
        }
    }

    @Test
    @DisplayName("apenas a feature game utiliza o port GameNotifier")
    void onlyGameMayUseGameNotifier() throws IOException {
        Path featuresRoot = Paths.get(MAIN_ROOT, "features");
        String forbiddenImport =
                "import com.letraaletra.api.features.game.application.port.GameNotifier;";

        try (Stream<Path> sources = Files.walk(featuresRoot)) {
            List<String> violations = sources
                    .filter(p -> p.toString().endsWith(".java"))
                    .filter(p -> !p.toString().contains("features" + java.io.File.separator + "game" + java.io.File.separator))
                    .map(WebSocketArchitectureIsolationTest::readFully)
                    .filter(content -> content.contains(forbiddenImport))
                    .toList();

            assertTrue(violations.isEmpty(),
                    "somente game pode depender de GameNotifier");
        }
    }

    @Test
    @DisplayName("SessionRepository foi removido e não pode ser reintroduzido")
    void sessionRepositoryMustStayRemoved() throws IOException {
        try (Stream<Path> sources = Files.walk(Paths.get(MAIN_ROOT))) {
            List<String> violations = sources
                    .filter(p -> p.toString().endsWith(".java"))
                    .map(WebSocketArchitectureIsolationTest::readFully)
                    .filter(content -> content.contains("class SessionRepository")
                            || content.contains("interface SessionRepository"))
                    .toList();

            assertTrue(violations.isEmpty(),
                    "registro de sessões pertence ao WsConnectionRegistry do kernel");
        }
    }

    @Test
    @DisplayName("configuração Jackson do protocolo WS não registra subtipos estaticamente por feature")
    void jacksonConfigMustNotImportFeatureDtos() throws IOException {
        String config = readFully(
                Paths.get(MAIN_ROOT, "shared/infrastructure/config/JacksonConfig.java"));

        assertTrue(!config.contains("com.letraaletra.api.features"),
                "subtipos devem ser descobertos pelos handlers do kernel");
    }

    private static String readFully(Path path) {
        try {
            return Files.readString(path);
        } catch (IOException e) {
            throw new IllegalStateException("Falha ao ler " + path, e);
        }
    }
}
