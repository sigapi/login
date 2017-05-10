package br.edu.fatecsbc.sigapi.loginserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Import;

import br.edu.fatecsbc.sigapi.conector.selenium.ConectorSeleniumConfig;

@SpringBootApplication
@Import({ ConectorSeleniumConfig.class })
@EnableCaching
public class Application // NO_UCD (unused code)
    extends SpringBootServletInitializer {

    public static void main(final String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Override
    protected SpringApplicationBuilder configure(final SpringApplicationBuilder application) {
        return application.sources(Application.class);
    }

}
