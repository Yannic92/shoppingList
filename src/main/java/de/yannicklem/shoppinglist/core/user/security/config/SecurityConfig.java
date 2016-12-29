package de.yannicklem.shoppinglist.core.user.security.config;

import de.yannicklem.shoppinglist.core.ShoppingListSecurityProperties;
import de.yannicklem.shoppinglist.core.user.entity.SLAuthority;
import de.yannicklem.shoppinglist.core.user.entity.SLUser;
import de.yannicklem.shoppinglist.core.user.persistence.SLUserService;

import org.apache.log4j.Logger;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.context.properties.EnableConfigurationProperties;

import org.springframework.context.annotation.Configuration;

import org.springframework.http.HttpMethod;

import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;

import java.util.Set;
import java.util.stream.Collectors;

import javax.sql.DataSource;

import static org.apache.log4j.Logger.getLogger;

import static java.lang.invoke.MethodHandles.lookup;


@Configuration
@EnableConfigurationProperties(ShoppingListSecurityProperties.class)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private static final Logger LOGGER = getLogger(lookup().lookupClass());

    private final DataSource datasource;

    private final SLUserService slUserService;

    private final ShoppingListSecurityProperties securityProperties;

    @Autowired
    public SecurityConfig(DataSource datasource, SLUserService slUserService,
        ShoppingListSecurityProperties securityProperties) {

        this.slUserService = slUserService;
        this.datasource = datasource;
        this.securityProperties = securityProperties;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http.httpBasic()
            .and()
            .authorizeRequests()
            .antMatchers(HttpMethod.POST, "/sLUsers")
            .permitAll()
            .antMatchers(HttpMethod.PUT, "/sLUsers/**")
            .permitAll()
            .antMatchers(HttpMethod.POST, "/logout")
            .permitAll()
            .antMatchers("/templates/**", "/lib/**", "/style/**", "/img/**", "/index.html", "/index-debug.html", "/",
                    "/**sw.js", "/manifest.json")
            .permitAll()
            .anyRequest()
            .authenticated()
            .and()
            .logout()
            .logoutSuccessUrl("/#logout")
            .deleteCookies("JSESSIONID", "XSRF-TOKEN")
            .and()
            .csrf()
            .csrfTokenRepository(csrfTokenRepository())
            .and()
            .addFilterAfter(new SlCsrfHeaderFilter(), CsrfFilter.class);
    }


    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {

        auth.userDetailsService(slUserService).passwordEncoder(new BCryptPasswordEncoder());
        auth.jdbcAuthentication().dataSource(datasource);

        SLUser currentAdmin = slUserService.findById("admin");

        if (currentAdmin != null) {
            slUserService.delete(currentAdmin);
        }

        createDefaultAdminUser();
    }


    private void createDefaultAdminUser() {

        Set<SLAuthority> authorities = securityProperties.getAdmin()
                .getRole()
                .stream()
                .map(SLAuthority::new)
                .collect(Collectors.toSet());

        String password = securityProperties.getAdmin().getPassword();

        LOGGER.info("\n\nCreated initial admin password: \"" + password + "\"\n");

        SLUser slUser = new SLUser("admin", "Yannic", "Klem", password, securityProperties.getAdmin().getMail(), true,
                null, authorities);

        slUserService.create(slUser);
    }


    private CsrfTokenRepository csrfTokenRepository() {

        HttpSessionCsrfTokenRepository repository = new HttpSessionCsrfTokenRepository();
        repository.setHeaderName("X-XSRF-TOKEN");

        return repository;
    }
}
