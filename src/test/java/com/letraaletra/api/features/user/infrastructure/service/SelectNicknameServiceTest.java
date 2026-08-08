package com.letraaletra.api.features.user.infrastructure.service;

import com.letraaletra.api.features.user.domain.repository.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("SelectNicknameService Unit Tests")
class SelectNicknameServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private SelectNicknameService service;

    @BeforeEach
    void setUp() {
        // Inicialização padrão antes de cada teste se necessário
    }

    @Nested
    @DisplayName("Sucesso na Geração de Nickname")
    class SuccessFlows {

        @Test
        @DisplayName("Deve gerar um nickname único válido que respeite as regras de tamanho e não exista no repositório")
        void get_WhenNicknameIsUnique_ShouldReturnValidNickname() {
            when(userRepository.existsByNickname(anyString())).thenReturn(false);

            String nickname = service.get();

            assertNotNull(nickname);
            assertTrue(nickname.length() <= 15, "O nickname não deve exceder 15 caracteres");
            assertTrue(nickname.length() >= 4, "O nickname deve possuir tamanho suficiente");
            verify(userRepository, times(1)).existsByNickname(nickname);
        }

        @Test
        @DisplayName("Deve tentar novamente até encontrar um nickname único caso ocorra colisão no repositório")
        void get_WhenCollisionOccursInRepository_ShouldRetryUntilUnique() {
            when(userRepository.existsByNickname(anyString()))
                    .thenReturn(true)  // Primeira tentativa colide
                    .thenReturn(true)  // Segunda tentativa colide
                    .thenReturn(false); // Terceira tentativa é única

            String nickname = service.get();

            assertNotNull(nickname);
            assertTrue(nickname.length() <= 15);
            verify(userRepository, times(3)).existsByNickname(anyString());
        }
    }

    @Nested
    @DisplayName("Falhas na Camada de Repositório")
    class RepositoryFailures {

        @Test
        @DisplayName("Deve propagar exceção caso o UserRepository falhe ao verificar existência do nickname")
        void get_WhenUserRepositoryFails_ShouldPropagateException() {
            when(userRepository.existsByNickname(anyString()))
                    .thenThrow(new RuntimeException("Erro de conexão com o banco de dados"));

            RuntimeException exception = assertThrows(
                    RuntimeException.class,
                    () -> service.get()
            );

            assertEquals("Erro de conexão com o banco de dados", exception.getMessage());
            verify(userRepository, atLeast(1)).existsByNickname(anyString());
        }
    }
}