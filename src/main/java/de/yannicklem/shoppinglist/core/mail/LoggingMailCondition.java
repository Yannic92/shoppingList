package de.yannicklem.shoppinglist.core.mail;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;

import org.springframework.core.type.AnnotatedTypeMetadata;


/**
 * Condition applies if the config shopping-list.mail.host is not set.
 *
 * @author  David Schilling - schilling@synyx.de
 */
public class LoggingMailCondition implements Condition {

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {

        return null == context.getEnvironment().getProperty("shopping-list.mail.host");
    }
}
