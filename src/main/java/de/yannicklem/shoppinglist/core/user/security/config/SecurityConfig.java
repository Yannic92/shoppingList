package de.yannicklem.shoppinglist.core.user.security.config;

import de.yannicklem.shoppinglist.core.user.entity.SLAuthority;
import de.yannicklem.shoppinglist.core.user.entity.SLUser;
import de.yannicklem.shoppinglist.core.user.persistence.SLUserService;
import de.yannicklem.shoppinglist.core.user.security.service.PasswordGenerator;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;

import javax.sql.DataSource;
import java.util.HashSet;
import java.util.Set;

import static java.lang.invoke.MethodHandles.lookup;
import static org.apache.log4j.Logger.getLogger;


@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private static final Logger LOGGER = getLogger(lookup().lookupClass());

    @Autowired
    private DataSource datasource;

    @Autowired
    private SLUserService slUserService;

    @Value("${security.user.password:}")
    private String password;

    @Value("${adminMail}")
    private String adminMail;

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
            .antMatchers("/app/**", "/lib/**", "/style/**", "/img/**", "/index.html", "/index-debug.html", "/",
                    "/shoppinglist.appcache")
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

        Set<SLAuthority> authorities = new HashSet<>();
        authorities.add(new SLAuthority(SLAuthority.ADMIN));
        authorities.add(new SLAuthority(SLAuthority.USER));

        String password;

        if (this.password != null && !this.password.equals("")) {
            password = this.password;
        } else {
            password = PasswordGenerator.generatePassword();
        }

        LOGGER.info("\n\nCreated initial admin password: \"" + password + "\"\n");

        SLUser slUser = new SLUser("admin", "Yannic", "Klem", password, adminMail, true, null, authorities);

        slUserService.create(slUser);
    }


    private CsrfTokenRepository csrfTokenRepository() {

        HttpSessionCsrfTokenRepository repository = new HttpSessionCsrfTokenRepository();
        repository.setHeaderName("X-XSRF-TOKEN");

        return repository;
    }
}
