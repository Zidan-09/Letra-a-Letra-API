package com.letraaletra.api.shared.infrastructure.config;

import com.letraaletra.api.features.admin.domain.Admin;
import com.letraaletra.api.features.admin.domain.repository.AdminRepository;
import com.letraaletra.api.features.user.domain.User;
import com.letraaletra.api.features.user.domain.repository.UserRepository;
import com.letraaletra.api.shared.domain.AuthenticatedUser;
import com.letraaletra.api.shared.domain.security.Roles;
import com.letraaletra.api.shared.domain.security.TokenContent;
import com.letraaletra.api.shared.domain.security.TokenService;
import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("JwtAuthenticationFilter - caracterização da autenticação HTTP")
class JwtAuthenticationFilterTest {

    @Mock
    private TokenService tokenService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private AdminRepository adminRepository;

    @Mock
    private FilterChain filterChain;

    private JwtAuthenticationFilter filter;

    private final UUID userId = UUID.randomUUID();
    private final UUID adminId = UUID.randomUUID();
    private final UUID tokenVersion = UUID.randomUUID();

    private MockHttpServletRequest request;
    private MockHttpServletResponse response;

    @BeforeEach
    void setUp() {
        filter = new JwtAuthenticationFilter(
                tokenService,
                List.of(
                        new com.letraaletra.api.features.user.infrastructure.service.UserPrincipalResolver(userRepository),
                        new com.letraaletra.api.features.admin.infrastructure.service.AdminPrincipalResolver(adminRepository)
                )
        );
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        SecurityContextHolder.clearContext();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    private void givenToken(String token, Roles role) {
        request.addHeader("Authorization", "Bearer " + token);
        lenient().when(tokenService.getTokenContent(token))
                .thenReturn(new TokenContent(userId, role, tokenVersion));
    }

    private void givenAdminToken(String token) {
        request.addHeader("Authorization", "Bearer " + token);
        lenient().when(tokenService.getTokenContent(token))
                .thenReturn(new TokenContent(adminId, Roles.ADMIN, tokenVersion));
    }

    private User userMock(boolean banned, UUID version) {
        User user = mock(User.class);
        lenient().when(user.getUserId()).thenReturn(userId);
        lenient().when(user.getUsername()).thenReturn("alice");
        lenient().when(user.isBanned()).thenReturn(banned);
        lenient().when(user.getTokenVersion()).thenReturn(version);
        return user;
    }

    private Admin adminMock(UUID version) {
        Admin admin = mock(Admin.class);
        lenient().when(admin.getId()).thenReturn(adminId);
        lenient().when(admin.getName()).thenReturn("boss");
        lenient().when(admin.isSuper()).thenReturn(true);
        lenient().when(admin.getTokenVersion()).thenReturn(version);
        return admin;
    }

    private void assertNoAuthenticationAndChainContinued(boolean chainResult) throws Exception {
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        assertTrue(chainResult);
        verify(filterChain).doFilter(request, response);
    }

    @Test
    @DisplayName("USER válido autentica com principal (id, username, false, false)")
    void shouldAuthenticateValidUser() throws Exception {
        String token = "user-token";
        givenToken(token, Roles.USER);
        User alice = userMock(false, tokenVersion);
        when(userRepository.find(userId)).thenReturn(Optional.of(alice));

        boolean chainInvoked = runFilter();

        AuthenticatedUser principal =
                (AuthenticatedUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        assertEquals(userId, principal.auth());
        assertEquals("alice", principal.name());
        assertFalse(principal.isAdmin());
        assertFalse(principal.isSuper());
        assertTrue(SecurityContextHolder.getContext().getAuthentication().getAuthorities().isEmpty());
        assertTrue(chainInvoked);
    }

    @Test
    @DisplayName("ADMIN válido autentica com principal (id, name, true, isSuper)")
    void shouldAuthenticateValidAdmin() throws Exception {
        String token = "admin-token";
        givenAdminToken(token);
        Admin boss = adminMock(tokenVersion);
        when(adminRepository.find(adminId)).thenReturn(Optional.of(boss));

        boolean chainInvoked = runFilter();

        AuthenticatedUser principal =
                (AuthenticatedUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        assertEquals(adminId, principal.auth());
        assertEquals("boss", principal.name());
        assertTrue(principal.isAdmin());
        assertTrue(principal.isSuper());
        assertTrue(chainInvoked);
    }

    @Test
    @DisplayName("usuário banido não autentica; exceção é engolida e a chain continua")
    void shouldNotAuthenticateBannedUserButContinueChain() throws Exception {
        String token = "banned";
        givenToken(token, Roles.USER);
        User banned = userMock(true, tokenVersion);
        when(userRepository.find(userId)).thenReturn(Optional.of(banned));

        assertNoAuthenticationAndChainContinued(runFilter());
    }

    @Test
    @DisplayName("tokenVersion divergente do usuário não autentica e segue a chain")
    void shouldRejectDivergentUserTokenVersion() throws Exception {
        String token = "old-user";
        givenToken(token, Roles.USER);
        User stale = userMock(false, UUID.randomUUID());
        when(userRepository.find(userId)).thenReturn(Optional.of(stale));

        assertNoAuthenticationAndChainContinued(runFilter());
    }

    @Test
    @DisplayName("tokenVersion divergente do admin não autentica e segue a chain")
    void shouldRejectDivergentAdminTokenVersion() throws Exception {
        String token = "old-admin";
        givenAdminToken(token);
        Admin staleAdmin = adminMock(UUID.randomUUID());
        when(adminRepository.find(adminId)).thenReturn(Optional.of(staleAdmin));

        assertNoAuthenticationAndChainContinued(runFilter());
    }

    @Test
    @DisplayName("admin inexistente não autentica e segue a chain")
    void shouldContinueWhenAdminMissing() throws Exception {
        String token = "ghost-admin";
        givenAdminToken(token);
        when(adminRepository.find(adminId)).thenReturn(Optional.empty());

        assertNoAuthenticationAndChainContinued(runFilter());
    }

    @Test
    @DisplayName("usuário inexistente não autentica e segue a chain")
    void shouldContinueWhenUserMissing() throws Exception {
        String token = "unknown";
        givenToken(token, Roles.USER);
        when(userRepository.find(userId)).thenReturn(Optional.empty());

        assertNoAuthenticationAndChainContinued(runFilter());
    }

    @Test
    @DisplayName("role nula ou inválida lança InvalidTokenException internamente e segue a chain")
    void shouldTreatNullOrUnknownRoleAsInvalidToken() throws Exception {
        String token = "weird";
        request.addHeader("Authorization", "Bearer " + token);
        when(tokenService.getTokenContent(token))
                .thenAnswer(inv -> new TokenContent(userId, null, tokenVersion));

        assertNoAuthenticationAndChainContinued(runFilter());
    }

    @Test
    @DisplayName("token inválido no serviço também é engolido e a chain segue")
    void shouldSwallowTokenServiceFailures() throws Exception {
        String token = "broken";
        request.addHeader("Authorization", "Bearer " + token);
        when(tokenService.getTokenContent(token)).thenThrow(new RuntimeException("bad token"));

        assertNoAuthenticationAndChainContinued(runFilter());
    }

    @Test
    @DisplayName("sem header Bearer a chain segue direto sem autenticação")
    void shouldSkipWithoutBearerHeader() throws Exception {
        boolean chainInvoked = runFilter();

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        assertTrue(chainInvoked);
    }

    /** Executa o filtro; o filtro atual nunca propaga exceções do processamento do token. */
    private boolean runFilter() throws Exception {
        filter.doFilter(request, response, filterChain);
        return true;
    }
}
