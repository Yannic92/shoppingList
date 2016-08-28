package de.yannicklem.shoppinglist.core;

import org.springframework.boot.context.properties.ConfigurationProperties;


/**
 * @author  Yannic Klem - klem@synyx.de
 */
@ConfigurationProperties(prefix = "shopping-list")
public class ShoppingListConfigurationProperties {

    private String url;

    public String getUrl() {

        return url;
    }


    public void setUrl(String url) {

        this.url = url;
    }
}
