package br.edu.fatecsbc.sigapi.loginserver.security.oauth2.provider.token;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.stereotype.Component;

@Component
public class CustomTokenStore
    extends JwtTokenStore {

    @Autowired
    private CustomTokenConverter tokenConverter;

    public CustomTokenStore() {
        super(null);
    }

    @PostConstruct
    private void init() {
        setTokenEnhancer(tokenConverter);
    }

    public CustomTokenConverter getTokenConverter() {
        return tokenConverter;
    }

}