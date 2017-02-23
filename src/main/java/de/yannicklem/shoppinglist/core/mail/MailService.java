package de.yannicklem.shoppinglist.core.mail;

/**
 * @author  David Schilling - davejs92@gmail.com
 */
public interface MailService {

    void sendMail(String mailTo, String message);
}
