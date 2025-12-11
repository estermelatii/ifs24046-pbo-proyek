package org.delcom.app.services;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.delcom.app.entities.AuthToken;
import org.delcom.app.repositories.AuthTokenRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AuthTokenServiceTest {

    @Mock
    private AuthTokenRepository authTokenRepository;

    @InjectMocks
    private AuthTokenService authTokenService;

    @Test
    void testSaveToken() {
        authTokenService.saveToken(new AuthToken());
        verify(authTokenRepository, times(1)).save(any(AuthToken.class));
    }

    @Test
    void testRemoveToken() {
        authTokenService.removeTokenByToken("token-123");
        verify(authTokenRepository, times(1)).deleteByToken("token-123");
    }
}