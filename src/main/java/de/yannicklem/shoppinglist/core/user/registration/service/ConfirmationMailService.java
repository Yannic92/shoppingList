package de.yannicklem.shoppinglist.core.user.registration.service;

import de.yannicklem.shoppinglist.core.ShoppingListConfigurationProperties;
import de.yannicklem.shoppinglist.core.mail.MailService;
import de.yannicklem.shoppinglist.core.user.entity.SLUser;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;


@Service
public class ConfirmationMailService {

    private final String confirmationUri;
    private final MailService mailService;

    @Autowired
    public ConfirmationMailService(ShoppingListConfigurationProperties configurationProperties,
        MailService mailService) {

        this.confirmationUri = configurationProperties.getUrl() + "/#/register/confirmation/{username}/{code}";
        this.mailService = mailService;
    }

    public void sendConfirmationMailTo(SLUser slUser) {

        String userConfirmationUri;
        userConfirmationUri = confirmationUri.replace("{username}", slUser.getUsername())
                .replace("{code}", slUser.getConfirmation().getCode());

        String confirmationMessage = String.format("Hallo %s,\n\n"
                + "es freut mich, dass du dich für die Verwendung der Einkaufsliste entschieden hast.\n"
                + "Es fehlt noch ein letzter Schritt, damit du die Einkaufsliste verwenden kannst.\n"
                + "Folge einfach diesem Link zur Aktivierung deines Kontos: \n\n%s\n\n"
                + "Solltest du dich nicht registriert haben, ignoriere diese E-Mail einfach.\n\n"
                + "Viel Erfolg mit deiner Einkaufsliste",
                slUser.getFirstName() != null ? slUser.getFirstName() : slUser.getUsername(), userConfirmationUri);

        mailService.sendMail(slUser.getEmail(), confirmationMessage);
    }
}
