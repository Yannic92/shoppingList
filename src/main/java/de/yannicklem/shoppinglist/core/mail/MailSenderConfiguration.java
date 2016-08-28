package de.yannicklem.shoppinglist.core.mail;

import de.yannicklem.shoppinglist.core.ShoppingListMailProperties;

import org.springframework.beans.factory.ObjectProvider;

import org.springframework.boot.autoconfigure.mail.MailSenderAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import org.springframework.context.annotation.Configuration;

import javax.mail.Session;


/**
 * @author  Yannic Klem - klem@synyx.de
 */
@Configuration
@EnableConfigurationProperties(ShoppingListMailProperties.class)
public class MailSenderConfiguration extends MailSenderAutoConfiguration {

    public MailSenderConfiguration(ShoppingListMailProperties properties, ObjectProvider<Session> sessionProvider) {

        super(properties, sessionProvider);
    }
}
