package com.letraaletra.api.shared.infrastructure.presentation;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Collections;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Pageables Unit Tests")
class PageablesTest {

    private static final Set<String> ALLOWED_SORTS = Set.of("createdAt", "name");

    @Nested
    @DisplayName("sanitize")
    class SanitizeTests {

        @Test
        @DisplayName("Deve aplicar padrões de paginação e ordenação quando Pageable for nulo")
        void sanitize_WhenNullPageable_ShouldReturnDefaults() {
            Sort defaultSort = Sort.by(Sort.Direction.DESC, "createdAt");
            Pageable result = Pageables.sanitize(null, ALLOWED_SORTS, defaultSort);

            assertEquals(0, result.getPageNumber());
            assertEquals(Pageables.DEFAULT_SIZE, result.getPageSize());
            assertEquals(defaultSort, result.getSort());
        }

        @Test
        @DisplayName("Deve retornar Sort.unsorted() quando Pageable for nulo e defaultSort for nulo")
        void sanitize_WhenNullPageableAndNullDefaultSort_ShouldReturnUnsorted() {
            Pageable result = Pageables.sanitize(null, ALLOWED_SORTS, null);

            assertEquals(0, result.getPageNumber());
            assertEquals(Pageables.DEFAULT_SIZE, result.getPageSize());
            assertTrue(result.getSort().isUnsorted());
        }

        @Test
        @DisplayName("Deve limitar o tamanho máximo e corrigir número de página negativo")
        void sanitize_WhenOversizedAndNegativePage_ShouldCapSizeAndFloorPage() {
            Pageable invalidPageable = new Pageable() {
                @Override public int getPageNumber() { return -3; }
                @Override public int getPageSize() { return 10_000; }
                @Override public long getOffset() { return 0; }
                @Override public Sort getSort() { return Sort.by("createdAt"); }
                @Override public Pageable next() { return null; }
                @Override public Pageable previousOrFirst() { return null; }
                @Override public Pageable first() { return null; }
                @Override public Pageable withPage(int pageNumber) { return null; }
                @Override public boolean hasPrevious() { return false; }
            };

            Pageable result = Pageables.sanitize(
                    invalidPageable,
                    ALLOWED_SORTS,
                    Sort.unsorted()
            );

            assertEquals(0, result.getPageNumber());
            assertEquals(Pageables.MAX_SIZE, result.getPageSize());
            assertEquals(Sort.by("createdAt"), result.getSort());
        }

        @Test
        @DisplayName("Deve manter o tamanho válido quando dentro do limite mínimo e máximo")
        void sanitize_WhenValidSize_ShouldKeepIt() {
            Pageable result = Pageables.sanitize(PageRequest.of(1, 10), ALLOWED_SORTS, Sort.unsorted());

            assertEquals(1, result.getPageNumber());
            assertEquals(10, result.getPageSize());
        }

        @Test
        @DisplayName("Deve aplicar defaultSort quando Pageable fornecido não possuir ordenação")
        void sanitize_WhenUnsortedPageable_ShouldApplyDefaultSort() {
            Sort defaultSort = Sort.by(Sort.Direction.ASC, "name");
            Pageable result = Pageables.sanitize(
                    PageRequest.of(0, 15, Sort.unsorted()),
                    ALLOWED_SORTS,
                    defaultSort
            );

            assertEquals(defaultSort, result.getSort());
        }

        @Test
        @DisplayName("Deve filtrar ordenações fora da whitelist e manter apenas as permitidas")
        void sanitize_WhenMixedSortProperties_ShouldKeepOnlyAllowed() {
            Sort sort = Sort.by(Sort.Order.desc("createdAt"), Sort.Order.asc("passwordHash"));
            Pageable result = Pageables.sanitize(PageRequest.of(0, 20, sort), ALLOWED_SORTS, Sort.unsorted());

            assertEquals(Sort.by(Sort.Order.desc("createdAt")), result.getSort());
        }

        @Test
        @DisplayName("Deve aplicar defaultSort quando nenhuma propriedade de ordenação for permitida")
        void sanitize_WhenNoAllowedSortProperties_ShouldFallbackToDefaultSort() {
            Sort defaultSort = Sort.by(Sort.Direction.DESC, "createdAt");
            Pageable result = Pageables.sanitize(
                    PageRequest.of(0, 20, Sort.by("passwordHash", "email")),
                    ALLOWED_SORTS,
                    defaultSort
            );

            assertEquals(defaultSort, result.getSort());
        }
    }

    @Nested
    @DisplayName("sanitizeSort")
    class SanitizeSortTests {

        @Test
        @DisplayName("Deve retornar Sort.unsorted() quando Sort for nulo ou sem ordenação")
        void sanitizeSort_WhenNullOrUnsorted_ShouldReturnUnsorted() {
            assertTrue(Pageables.sanitizeSort(null, ALLOWED_SORTS).isUnsorted());
            assertTrue(Pageables.sanitizeSort(Sort.unsorted(), ALLOWED_SORTS).isUnsorted());
        }

        @Test
        @DisplayName("Deve retornar Sort.unsorted() quando o conjunto de campos permitidos for nulo ou vazio")
        void sanitizeSort_WhenAllowedSortsNullOrEmpty_ShouldReturnUnsorted() {
            Sort inputSort = Sort.by("createdAt");

            assertTrue(Pageables.sanitizeSort(inputSort, null).isUnsorted());
            assertTrue(Pageables.sanitizeSort(inputSort, Collections.emptySet()).isUnsorted());
        }

        @Test
        @DisplayName("Deve preservar a direção da ordenação para campos permitidos")
        void sanitizeSort_WhenValidProperty_ShouldPreserveDirection() {
            Sort inputSort = Sort.by(Sort.Order.desc("name"));
            Sort result = Pageables.sanitizeSort(inputSort, ALLOWED_SORTS);

            assertEquals(inputSort, result);
        }
    }

    @Nested
    @DisplayName("clampPage / clampSize")
    class ClampTests {

        @Test
        @DisplayName("Deve restringir o índice da página para no mínimo zero")
        void clampPage_ShouldEnforceMinimumZero() {
            assertEquals(0, Pageables.clampPage(-10));
            assertEquals(0, Pageables.clampPage(-1));
            assertEquals(0, Pageables.clampPage(0));
            assertEquals(5, Pageables.clampPage(5));
        }

        @Test
        @DisplayName("Deve restringir o tamanho da página dentro dos limites válidos")
        void clampSize_ShouldEnforceBoundsAndDefault() {
            assertEquals(Pageables.DEFAULT_SIZE, Pageables.clampSize(-5));
            assertEquals(Pageables.DEFAULT_SIZE, Pageables.clampSize(0));
            assertEquals(10, Pageables.clampSize(10));
            assertEquals(Pageables.MAX_SIZE, Pageables.clampSize(50));
            assertEquals(Pageables.MAX_SIZE, Pageables.clampSize(5000));
        }
    }
}