package de.yannicklem.shoppinglist.core;

import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;


/**
 * @author  Yannic Klem - klem@synyx.de
 */
@ConfigurationProperties(prefix = "shopping-list.mail")
public class ShoppingListMailProperties extends MailProperties {

    private String confirmationMailAddress;
    private String confirmationMailPassword;

    public String getConfirmationMailAddress() {

        return confirmationMailAddress;
    }


    public void setConfirmationMailAddress(String confirmationMailAddress) {

        this.confirmationMailAddress = confirmationMailAddress;
        super.setUsername(this.confirmationMailAddress);
    }


    public String getConfirmationMailPassword() {

        return confirmationMailPassword;
    }


    public void setConfirmationMailPassword(String confirmationMailPassword) {

        this.confirmationMailPassword = confirmationMailPassword;
        super.setPassword(this.confirmationMailPassword);
    }


    /**
     * Does nothing because {@link ShoppingListMailProperties#setConfirmationMailAddress(String)} sets username.
     *
     * @param  username  not used.
     */
    @Override
    public void setUsername(String username) {
    }


    /**
     * Does nothing because {@link ShoppingListMailProperties#setConfirmationMailPassword(String)} sets password.
     *
     * @param  password  not used.
     */
    @Override
    public void setPassword(String password) {
    }
}
