package br.edu.fatecsbc.sigapi.loginserver.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.config.annotation.builders.InMemoryClientDetailsServiceBuilder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;

import br.edu.fatecsbc.sigapi.loginserver.config.PropertiesConfig.Client;
import br.edu.fatecsbc.sigapi.loginserver.security.oauth2.provider.token.CustomTokenStore;

@Configuration
@EnableAuthorizationServer
public class AuthorizationServerConfig // NO_UCD (unused code)
    extends AuthorizationServerConfigurerAdapter {

    @Autowired
    private PropertiesConfig config;

    @Autowired
    private CustomTokenStore tokenStore;

    @Override
    public void configure(final AuthorizationServerSecurityConfigurer oauthServer) throws Exception {
        oauthServer.checkTokenAccess("hasAuthority('server')");
    }

    @Override
    public void configure(final AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        endpoints.tokenStore(tokenStore);
        endpoints.accessTokenConverter(tokenStore.getTokenConverter());
    }

    @Override
    public void configure(final ClientDetailsServiceConfigurer configurer) throws Exception {

        final InMemoryClientDetailsServiceBuilder builder = configurer.inMemory();

        final List<Client> clients = config.getClients();
        for (final Client client : clients) {

            final String secret = config.getSecret(client);

            // @formatter:off
            builder
                .withClient(client.getId())
                    .secret(secret)
                    .authorities(client.getAuthority())
                    .scopes("read")
                    .authorizedGrantTypes(client.getGrantTypes().stream().toArray(String[]::new));
            // @formatter:on

        }

    }

}