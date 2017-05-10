package br.edu.fatecsbc.sigapi.loginserver.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

import br.edu.fatecsbc.sigapi.loginserver.security.authentication.CustomAuthenticationProvider;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig // NO_UCD (unused code)
    extends WebSecurityConfigurerAdapter {

    @Autowired
    private CustomAuthenticationProvider provider;

    @Override
    protected void configure(final HttpSecurity http) throws Exception {
        // @formatter:off
        http
            .formLogin()
            .and()
                .httpBasic().disable()
                .anonymous().disable()
                .csrf().disable()
                .authorizeRequests().anyRequest().authenticated();
        // @formatter:on
    }

    @Override
    @Autowired
    protected void configure(final AuthenticationManagerBuilder auth) throws Exception {
        super.configure(auth);
        auth.authenticationProvider(provider);
        auth.eraseCredentials(false);
    }

}
