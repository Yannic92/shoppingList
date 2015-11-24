package de.yannicklem.shoppinglist.core.user.registration.service;

import de.yannicklem.shoppinglist.core.mail.MailService;
import de.yannicklem.shoppinglist.core.user.entity.SLUser;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.stereotype.Service;


@Service
public class ConfirmationMailService {

    private final String confirmationUri;
    private final MailService mailService;

    @Autowired
    public ConfirmationMailService(@Value("${shoppingList.confirmation.uri}") String confirmationUri,
        MailService mailService) {

        this.confirmationUri = confirmationUri;
        this.mailService = mailService;
    }

    public void sendConfirmationMailTo(SLUser slUser) {

        String confirmationMessage = String.format("Hallo %s,\n\n"
                + "es freut mich, dass du dich für die Verwendung der Einkaufsliste entschieden hast.\n"
                + "Es fehlt noch ein letzter Schritt, damit du die Einkaufsliste verwenden kannst.\n"
                + "Bestätige deine Registrierung mit folgendem Code: \n\n%s\n\n"
                + "Folge einfach diesem Link zur Bestätigung deiner Registrierung: \n\n%s\n\n"
                + "Solltest du dich nicht registriert haben, ignoriere diese E-Mail einfach.\n\n"
                + "Viel Erfolg mit deiner Einkaufsliste",
                slUser.getFirstName() != null ? slUser.getFirstName() : slUser.getUsername(),
                slUser.getConfirmation().getCode(), confirmationUri.replace("{username}", slUser.getUsername()).replace("{code}", slUser.getConfirmation().getCode()));

        mailService.sendMail(slUser.getEmail(), confirmationMessage);
    }
}
