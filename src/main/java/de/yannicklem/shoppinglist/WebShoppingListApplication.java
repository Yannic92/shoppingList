package de.yannicklem.shoppinglist;

import org.h2.tools.Server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.embedded.ConfigurableEmbeddedServletContainer;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.boot.context.embedded.MimeMappings;

import org.springframework.context.annotation.Bean;

import org.springframework.scheduling.annotation.EnableScheduling;

import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.sql.SQLException;


@SpringBootApplication
@EnableScheduling
@EnableTransactionManagement
public class WebShoppingListApplication {

    public static void main(String[] args) {

        SpringApplication.run(WebShoppingListApplication.class, args);
    }


    @Bean(initMethod = "start", destroyMethod = "stop")
    public Server h2WebServer() throws SQLException {

        return Server.createWebServer("-web", "-webAllowOthers", "-webPort", "8082");
    }


    @Bean
    public EmbeddedServletContainerCustomizer embeddedServletContainerCustomizer() {

        return new EmbeddedServletContainerCustomizer() {

            @Override
            public void customize(ConfigurableEmbeddedServletContainer container) {

                MimeMappings mappings = new MimeMappings(MimeMappings.DEFAULT);
                mappings.add("appcache", "text/cache-manifest");
                container.setMimeMappings(mappings);
            }
        };
    }
}
