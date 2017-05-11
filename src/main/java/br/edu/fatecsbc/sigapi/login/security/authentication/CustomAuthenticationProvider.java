package br.edu.fatecsbc.sigapi.login.security.authentication;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import br.edu.fatecsbc.sigapi.login.security.CustomUser;
import br.edu.fatecsbc.sigapi.login.service.ConectorService;

@Component
public class CustomAuthenticationProvider
    extends AbstractUserDetailsAuthenticationProvider {

    @Autowired
    private ConectorService service;

    @Override
    protected void additionalAuthenticationChecks(final UserDetails userDetails,
        final UsernamePasswordAuthenticationToken authentication) {

        // Extrai as informações
        final String usuario = authentication.getName();
        final String senha = (String) authentication.getCredentials();

        // Tenta autenticar
        if (!service.autenticar(usuario, senha)) {
            throw new BadCredentialsException("Usuário ou Senha inválidos");
        }

    }

    @Override
    protected UserDetails retrieveUser(final String username,
        final UsernamePasswordAuthenticationToken authentication) {

        final String password = authentication.getCredentials().toString();
        return new CustomUser(username, password);

    }

}