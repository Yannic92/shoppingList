package de.yannicklem.shoppinglist.core.user.security.config;

import coffee.synyx.autoconfigure.security.config.IntegrationCoffeeNetWebSecurityConfigurerAdapter;
import coffee.synyx.autoconfigure.security.service.CoffeeNetUserDetails;
import de.yannicklem.shoppinglist.core.ShoppingListSecurityProperties;
import de.yannicklem.shoppinglist.core.user.entity.SLAuthority;
import de.yannicklem.shoppinglist.core.user.entity.SLUser;
import de.yannicklem.shoppinglist.core.user.persistence.SLUserService;

import org.apache.log4j.Logger;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.context.properties.EnableConfigurationProperties;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.http.HttpMethod;

import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.filter.OAuth2ClientAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;

import java.io.IOException;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import static org.apache.log4j.Logger.getLogger;

import static java.lang.invoke.MethodHandles.lookup;


@Configuration
@EnableConfigurationProperties(ShoppingListSecurityProperties.class)
public class SecurityConfig extends IntegrationCoffeeNetWebSecurityConfigurerAdapter {

    private static final Logger LOGGER = getLogger(lookup().lookupClass());

    private final DataSource datasource;

    private final UserDetailsService slUserDetailsService;

    private final SLUserService slUserService;

    private final ShoppingListSecurityProperties securityProperties;

    @Autowired
    public SecurityConfig(DataSource datasource, UserDetailsService slUserDetailsService, SLUserService slUserService,
                          ShoppingListSecurityProperties securityProperties) {

        this.slUserService = slUserService;
        this.datasource = datasource;
        this.slUserDetailsService = slUserDetailsService;
        this.securityProperties = securityProperties;
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {

        enableSso(http).httpBasic()
                .and()
                .authorizeRequests()
                .antMatchers(HttpMethod.POST, "/api/sLUsers")
                .permitAll()
                .antMatchers(HttpMethod.PUT, "/api/sLUsers/**")
                .permitAll()
                .antMatchers(HttpMethod.POST, "/logout")
                .permitAll()
                .antMatchers("/templates/**", "/lib/**", "/style/**", "/img/**", "/index.html", "/index-debug.html",
                        "/index-appcache.html", "/shoppinglist.appcache", "/",
                        "/**sw.js", "/ServiceWorkers.js", "/manifest.json")
                .permitAll()
                .anyRequest()
                .authenticated()
                .and()
                .logout()
                .logoutSuccessUrl("/#logout")
                .deleteCookies("JSESSIONID", "XSRF-TOKEN")
                .and()
                .csrf()
                .disable();
    }


    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {

        auth.userDetailsService(slUserDetailsService).passwordEncoder(new BCryptPasswordEncoder());

        createDefaultAdminUser();
    }


    private void createDefaultAdminUser() {
        slUserService.findById("admin").ifPresent(slUserService::delete);

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

    @Override
    @Autowired
    public void setOAuth2ClientAuthenticationProcessingFilter(OAuth2ClientAuthenticationProcessingFilter
                                                                      oAuth2ClientAuthenticationProcessingFilter) {
        oAuth2ClientAuthenticationProcessingFilter.setAuthenticationSuccessHandler(new AuthenticationSuccessHandler() {
            @Override
            public void onAuthenticationSuccess(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Authentication authentication) throws IOException, ServletException {

                Object principal = authentication.getPrincipal();

                if (principal instanceof CoffeeNetUserDetails) {
                    Optional<SLUser> byName = slUserService.findByName("CoffeeNet-" + ((CoffeeNetUserDetails) principal).getUsername());
                    if (!byName.isPresent()) {
                        slUserService.create(SLUser.fromCoffeeNetUserDetails((CoffeeNetUserDetails) principal));
                    }
                }
            }
        });
        super.setOAuth2ClientAuthenticationProcessingFilter(oAuth2ClientAuthenticationProcessingFilter);
    }
}
