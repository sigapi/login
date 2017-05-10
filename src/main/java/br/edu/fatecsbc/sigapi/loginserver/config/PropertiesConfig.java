package br.edu.fatecsbc.sigapi.loginserver.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@ConfigurationProperties(prefix = "sigapi")
@PropertySource(value = "file://${sigapi.diretorio}/secrets.properties", ignoreResourceNotFound = true)
public class PropertiesConfig {

    private List<Client> clients = new ArrayList<>();
    private Map<String, Object> secrets = new HashMap<>();

    public List<Client> getClients() {
        return clients;
    }

    public void setClients(final List<Client> clients) {
        this.clients = clients;
    }

    public Map<String, Object> getSecrets() {
        return secrets;
    }

    public void setSecrets(final Map<String, Object> secrets) {
        this.secrets = secrets;
    }

    public String getSecret(final Client client) {

        if (client == null) {
            return null;
        }

        final String id = client.getId();
        final Object secretFromMap = secrets.get(id);

        if (secretFromMap != null) {
            return secretFromMap.toString();
        }

        return client.getSecret();

    }

    public static class Client {

        private String id;
        private String secret;
        private String authority;
        private final List<String> grantTypes = new ArrayList<>();
        private int tokenValidity;

        public String getId() {
            return id;
        }

        public void setId(final String id) {
            this.id = id;
        }

        public String getSecret() {
            return secret;
        }

        public void setSecret(final String secret) {
            this.secret = secret;
        }

        public String getAuthority() {
            return authority;
        }

        public void setAuthority(final String authority) {
            this.authority = authority;
        }

        public List<String> getGrantTypes() {
            return grantTypes;
        }

        public int getTokenValidity() {
            return tokenValidity;
        }

        public void setTokenValidity(final int tokenValidity) {
            this.tokenValidity = tokenValidity;
        }

    }

}
