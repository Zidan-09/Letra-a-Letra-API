package com.letraaletra.api.features.user.application.usecase;

import com.letraaletra.api.features.transaction.domain.TransactionDetails;
import com.letraaletra.api.features.transaction.domain.TransactionsPage;
import com.letraaletra.api.features.transaction.domain.repository.TransactionRepository;
import com.letraaletra.api.features.user.application.input.GetMyTransactionsInput;
import com.letraaletra.api.features.user.application.output.GetMyTransactionsOutput;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("GetMyTransactionsUseCase Unit Tests")
class GetMyTransactionsUseCaseTest {

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private GetMyTransactionsUseCase useCase;

    @Captor
    private ArgumentCaptor<TransactionsPage> transactionsPageCaptor;

    private UUID userId;
    private Page<TransactionDetails> mockPage;

    @BeforeEach
    @SuppressWarnings("unchecked")
    void setUp() {
        userId = UUID.randomUUID();
        mockPage = mock(Page.class);
    }

    @Nested
    @DisplayName("Sucesso no Fluxo Principal")
    class SuccessFlows {

        @Test
        @DisplayName("Deve buscar e retornar transações paginadas construindo o TransactionsPage corretamente")
        void execute_WhenUserHasTransactions_ShouldReturnOutputWithPagedTransactions() {
            int page = 0;
            int size = 10;
            Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
            GetMyTransactionsInput input = new GetMyTransactionsInput(userId, page, size, sort);

            when(transactionRepository.getByUserId(eq(userId), any(TransactionsPage.class)))
                    .thenReturn(mockPage);

            GetMyTransactionsOutput output = useCase.execute(input);

            assertNotNull(output);
            assertEquals(mockPage, output.transactions());

            verify(transactionRepository, times(1)).getByUserId(eq(userId), transactionsPageCaptor.capture());

            TransactionsPage capturedPage = transactionsPageCaptor.getValue();
            assertNotNull(capturedPage);
        }
    }

    @Nested
    @DisplayName("Falhas e Exceções do Repositório")
    class RepositoryFailures {

        @Test
        @DisplayName("Deve propagar exceção quando o repositório falhar na consulta")
        void execute_WhenRepositoryThrowsException_ShouldPropagateException() {
            GetMyTransactionsInput input = new GetMyTransactionsInput(userId, 0, 10, Sort.unsorted());

            when(transactionRepository.getByUserId(eq(userId), any(TransactionsPage.class)))
                    .thenThrow(new RuntimeException("Erro ao consultar repositório de transações"));

            RuntimeException exception = assertThrows(
                    RuntimeException.class,
                    () -> useCase.execute(input)
            );

            assertEquals("Erro ao consultar repositório de transações", exception.getMessage());
            verify(transactionRepository, times(1)).getByUserId(eq(userId), any(TransactionsPage.class));
        }
    }

    @Nested
    @DisplayName("Casos de Borda e Entradas Nulas")
    class NullAndEdgeCases {

        @Test
        @DisplayName("Deve permitir repassar ID nulo para o repositório caso venha nulo no input")
        void execute_WhenUserIdIsNull_ShouldQueryRepositoryWithNullId() {
            GetMyTransactionsInput input = new GetMyTransactionsInput(null, 0, 10, Sort.unsorted());

            when(transactionRepository.getByUserId(eq(null), any(TransactionsPage.class)))
                    .thenReturn(mockPage);

            GetMyTransactionsOutput output = useCase.execute(input);

            assertNotNull(output);
            assertEquals(mockPage, output.transactions());
            verify(transactionRepository, times(1)).getByUserId(eq(null), any(TransactionsPage.class));
        }

        @Test
        @DisplayName("Deve aceitar Sort nulo no input e repassar para o TransactionsPage")
        void execute_WhenSortIsNull_ShouldPassNullSortToTransactionsPage() {
            GetMyTransactionsInput input = new GetMyTransactionsInput(userId, 1, 20, null);

            when(transactionRepository.getByUserId(eq(userId), any(TransactionsPage.class)))
                    .thenReturn(mockPage);

            GetMyTransactionsOutput output = useCase.execute(input);

            assertNotNull(output);
            verify(transactionRepository, times(1)).getByUserId(eq(userId), transactionsPageCaptor.capture());
        }
    }
}