package com.letraaletra.api.shared;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Regra arquitetural permanente: nenhum arquivo sob shared pode importar features.
 */
@DisplayName("Isolamento arquitetural de shared")
class SharedArchitectureIsolationTest {

    private static final Path SHARED_ROOT =
            Paths.get("src/main/java/com/letraaletra/api/shared");

    @Test
    @DisplayName("shared não importa nenhum pacote features.*")
    void sharedMustNotImportFeatures() throws IOException {
        try (Stream<Path> sources = Files.walk(SHARED_ROOT)) {
            List<String> violations = sources
                    .filter(p -> p.toString().endsWith(".java"))
                    .map(SharedArchitectureIsolationTest::readFully)
                    .flatMap(content -> content.lines()
                            .filter(line -> line.trim().startsWith("import "))
                            .filter(line -> line.contains("com.letraaletra.api.features")))
                    .toList();

            assertTrue(violations.isEmpty(),
                    "shared não pode conhecer features, violações: " + violations);
        }
    }

    private static String readFully(Path path) {
        try {
            return Files.readString(path);
        } catch (IOException e) {
            throw new IllegalStateException("Falha ao ler " + path, e);
        }
    }
}
