package br.edu.fatecsbc.sigapi.login.security;

import java.util.Collections;

import org.springframework.security.core.userdetails.User;

public class CustomUser
    extends User {

    private static final long serialVersionUID = 1L;

    private final String usuario;

    public CustomUser(final String username, final String password) {
        super(generateUserId(username), password, true, true, true, true, Collections.emptyList());
        usuario = username;
    }

    private static final String generateUserId(final String username) {
        return String.valueOf(username.hashCode());
    }

    public String getUsuario() {
        return usuario;
    }

}