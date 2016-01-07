package de.yannicklem.shoppinglist;

import org.h2.tools.Server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.context.annotation.Bean;

import org.springframework.scheduling.annotation.EnableScheduling;

import java.sql.SQLException;


@SpringBootApplication
@EnableScheduling
public class WebShoppingListApplication {

    public static void main(String[] args) {

        SpringApplication.run(WebShoppingListApplication.class, args);
    }


    @Bean(initMethod = "start", destroyMethod = "stop")
    public Server h2WebServer() throws SQLException {

        return Server.createWebServer("-web", "-webAllowOthers", "-webPort", "8082");
    }
}
