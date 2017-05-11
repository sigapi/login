package br.edu.fatecsbc.sigapi.login.security.oauth2.provider.token;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.stereotype.Component;

import br.edu.fatecsbc.sigapi.login.security.CustomUser;
import br.edu.fatecsbc.sigapi.login.service.CriptographyService;

@Component
public class CustomTokenConverter // NO_UCD (use default)
    extends JwtAccessTokenConverter {

    private static final String ATRIBUTO_SENHA = "senha";
    private static final String ATRIBUTO_USUARIO = "usuario";

    @Autowired
    private CriptographyService criptographyService;

    @PostConstruct
    private void init() {

        final String publicKey = criptographyService.getPublicKey();
        final String privateKey = criptographyService.getPrivateKey();

        setSigningKey(privateKey);
        setVerifierKey(publicKey);

    }

    @Override
    public OAuth2AccessToken enhance(final OAuth2AccessToken accessToken, final OAuth2Authentication authentication) {

        final CustomUser sigaUser = (CustomUser) authentication.getPrincipal();

        // Obtém as credenciais
        final String usuario = sigaUser.getUsuario();
        final String senha = sigaUser.getPassword();

        // Realiza a criptografia
        final String usuarioEncrypted = criptographyService.encrypt(usuario);
        final String senhaEncrypted = criptographyService.encrypt(senha);

        // Cria o mapa com as informações para o token
        final Map<String, Object> information = new HashMap<>();
        information.put(ATRIBUTO_USUARIO, usuarioEncrypted);
        information.put(ATRIBUTO_SENHA, senhaEncrypted);

        // Adiciona as informações ao token
        ((DefaultOAuth2AccessToken) accessToken).setAdditionalInformation(information);

        return super.enhance(accessToken, authentication);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Map<String, ?> convertAccessToken(final OAuth2AccessToken token, final OAuth2Authentication authentication) {

        // Obtém as informações do token
        final Map<String, Object> information = ((DefaultOAuth2AccessToken) token).getAdditionalInformation();

        final String usuarioEncrypted = (String) information.get(ATRIBUTO_USUARIO);
        final String senhaEncrypted = (String) information.get(ATRIBUTO_SENHA);

        // Realiza a descriptografia
        final String usuario = criptographyService.decrypt(usuarioEncrypted);
        final String senha = criptographyService.decrypt(senhaEncrypted);

        // Adiciona as informações ao mapa de retorno
        final Map<String, Object> result = (Map<String, Object>) super.convertAccessToken(token, authentication);
        result.put(ATRIBUTO_USUARIO, usuario);
        result.put(ATRIBUTO_SENHA, senha);

        return result;

    }

}