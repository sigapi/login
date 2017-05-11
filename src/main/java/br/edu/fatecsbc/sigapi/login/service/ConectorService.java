package br.edu.fatecsbc.sigapi.login.service;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import org.apache.commons.lang3.StringUtils;

import br.edu.fatecsbc.sigapi.conector.Conector;
import br.edu.fatecsbc.sigapi.conector.CredenciaisBuilder;
import br.edu.fatecsbc.sigapi.conector.dto.Credenciais;
import br.edu.fatecsbc.sigapi.conector.selenium.CredenciaisSeleniumBuilder;

@Component
public class ConectorService {

    @Autowired
    private Collection<Conector<? extends Credenciais>> conectores;

    @SuppressWarnings("unchecked")
    public Conector<Credenciais> getConector() {

        if (conectores == null || conectores.isEmpty()) {
            throw new IllegalStateException("Nenhum conector definido");
        }

        return (Conector<Credenciais>) conectores.iterator().next();

    }

    @Cacheable("autenticar")
    public boolean autenticar(final String usuario, final String senha) {

        // Verifica o preenchimento dos dados
        if (StringUtils.isNoneBlank(usuario, senha)) {

            // Cria as credenciais
            final Credenciais credenciais = getCredenciais(usuario, senha);

            // Tenta autenticar
            return getConector().autenticar(credenciais);

        }

        return false;

    }

    /**
     * Cria as credenciais de acesso ao conectar
     *
     * @param usuario
     *            Usuário
     * @param senha
     *            Senha
     * @return {@link Credenciais} preenchidas
     */
    private Credenciais getCredenciais(final String usuario, final String senha) {

        final CredenciaisBuilder<?> builder = getConector().getCredenciaisBuilder();

        if (builder instanceof CredenciaisSeleniumBuilder) {

            // Preenchimento específico para o conector selenium
            final CredenciaisSeleniumBuilder seleniumBuilder = (CredenciaisSeleniumBuilder) builder;
            seleniumBuilder.credenciais(usuario, senha);

        }

        return builder.build();

    }

}
