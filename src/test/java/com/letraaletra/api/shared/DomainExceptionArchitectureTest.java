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
 * Regra arquitetural permanente: exceções de domínio não podem conhecer HTTP,
 * Spring ou infraestrutura. A tradução exceção → HTTP status pertence
 * exclusivamente à camada de infraestrutura/presentation.
 */
@DisplayName("Isolamento arquitetural das Domain Exceptions")
class DomainExceptionArchitectureTest {

    private static final Path MAIN_ROOT =
            Paths.get("src/main/java/com/letraaletra/api");

    @Test
    @DisplayName("exceções de domínio não importam Spring, HTTP ou infraestrutura")
    void domainExceptionsMustNotKnowHttpOrSpring() throws IOException {
        try (Stream<Path> sources = Files.walk(MAIN_ROOT)) {
            List<String> violations = sources
                    .filter(p -> p.toString().endsWith(".java"))
                    .filter(p -> p.toString().contains("domain"))
                    .filter(p -> p.getFileName().toString().endsWith("Exception.java")
                            || p.getFileName().toString().equals("DomainException.java")
                            || p.getFileName().toString().equals("MessageCode.java"))
                    .map(DomainExceptionArchitectureTest::readFully)
                    .flatMap(content -> content.lines()
                            .filter(line -> line.trim().startsWith("import "))
                            .filter(line -> line.contains("org.springframework")
                                    || line.contains("jakarta.servlet")
                                    || line.contains("ResponseEntity")
                                    || line.contains("HttpStatus")
                                    || line.contains("HttpStatusCode")
                                    || line.contains(".infrastructure.")))
                    .toList();

            assertTrue(violations.isEmpty(),
                    "exceções de domínio não podem conhecer HTTP/Spring/infraestrutura, violações: "
                            + violations);
        }
    }

    @Test
    @DisplayName("mapper HTTP conhece apenas categorias, nunca exceções concretas")
    void httpMapperMustNotKnowConcreteExceptions() throws IOException {
        Path mapper = MAIN_ROOT.resolve(
                "shared/infrastructure/presentation/dto/handlers/DomainExceptionHttpMapper.java");

        String content = Files.readString(mapper);

        List<String> concreteImports = content.lines()
                .filter(line -> line.trim().startsWith("import "))
                .filter(line -> line.contains("com.letraaletra.api.features"))
                .toList();

        assertTrue(concreteImports.isEmpty(),
                "o mapper não pode importar exceções concretas de features, violações: "
                        + concreteImports);
    }

    private static String readFully(Path path) {
        try {
            return Files.readString(path);
        } catch (IOException e) {
            throw new IllegalStateException("Falha ao ler " + path, e);
        }
    }
}
