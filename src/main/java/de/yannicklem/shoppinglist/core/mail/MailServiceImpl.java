package de.yannicklem.shoppinglist.core.mail;

import de.yannicklem.shoppinglist.core.ShoppingListMailProperties;

import org.apache.log4j.Logger;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

import org.springframework.mail.MailAuthenticationException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import static org.apache.log4j.Logger.getLogger;


@Service
@ConditionalOnProperty("shopping-list.mail.host")
public class MailServiceImpl implements MailService {

    private static final Logger LOGGER = getLogger(MethodHandles.lookup().lookupClass());
    private static final String SUBJECT = "Registrierung bestaetigen";
    private final String from;

    private final JavaMailSender mailSender;

    @Autowired
    public MailServiceImpl(JavaMailSender mailSender, ShoppingListMailProperties mailProperties) {

        this.mailSender = mailSender;
        this.from = mailProperties.getConfirmationMailAddress();
    }

    @Override
    public void sendMail(String mailTo, String message) {

        LOGGER.debug(String.format("Sending mail to '%s' with message '%s'", mailTo, message));

        try {
            mailSender.send(initMimeMessage(message, mailTo));
            LOGGER.info("Sent mail to " + mailTo);
        } catch (MessagingException | MailAuthenticationException e) {
            LOGGER.warn("Sending confirmation code email failed", e);
        }
    }


    private MimeMessage initMimeMessage(String message, String to) throws MessagingException {

        MimeMessage mimeMessage = mailSender.createMimeMessage();

        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
        helper.setFrom(from);
        helper.setTo(to);
        helper.setSubject(SUBJECT);
        helper.setText(message);

        return mimeMessage;
    }
}
