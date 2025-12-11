package org.delcom.app.services;

import org.delcom.app.entities.AuthToken;
import org.delcom.app.repositories.AuthTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthTokenService {

    @Autowired
    private AuthTokenRepository authTokenRepository;

    // Method yang dicari oleh Test
    public void saveToken(AuthToken token) {
        authTokenRepository.save(token);
    }

    // Method yang dicari oleh Test
    public void removeTokenByToken(String token) {
        authTokenRepository.deleteByToken(token);
    }
    
    // Method validasi (mungkin sudah ada sebelumnya)
    public boolean isTokenValid(String token) {
        return authTokenRepository.findByToken(token) != null;
    }
}