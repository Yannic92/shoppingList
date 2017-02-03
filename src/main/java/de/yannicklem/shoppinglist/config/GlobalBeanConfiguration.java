package de.yannicklem.shoppinglist.config;

import de.yannicklem.restutils.entity.owned.service.OwnedRestEntityPermissionEvaluator;
import org.h2.tools.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.sql.SQLException;

/**
 * @author Yannic Klem - yann.klem@gmail.com
 */
@Configuration
public class GlobalBeanConfiguration {

    @Bean(initMethod = "start", destroyMethod = "stop")
    public Server h2WebServer() throws SQLException {

        return Server.createWebServer("-web", "-webAllowOthers", "-webPort", "8082");
    }


    @Bean
    public OwnedRestEntityPermissionEvaluator ownedRestEntityPermissionEvaluator() {

        return new OwnedRestEntityPermissionEvaluator();
    }
}
