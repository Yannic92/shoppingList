package de.yannicklem.shoppinglist.core.user.validation;

import de.yannicklem.shoppinglist.core.user.entity.SLAuthority;
import de.yannicklem.shoppinglist.core.user.entity.SLUser;
import de.yannicklem.shoppinglist.exception.EntityInvalidException;
import org.hibernate.validator.internal.constraintvalidators.hv.EmailValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collection;


@Service
public class SLUserValidationService {

    private final int minPasswordLength;

    @Autowired
    public SLUserValidationService(@Value("${slUser.password.length.min}") int minPasswordLength) {

        this.minPasswordLength = minPasswordLength;
    }

    public void validate(SLUser slUser) throws EntityInvalidException {

        if (slUser == null) {
            throw new EntityInvalidException("User is null");
        }

        validateAuthorities(slUser.getAuthorities());
        validateUsername(slUser.getUsername());
        validateEmail(slUser.getEmail());
        validatePassword(slUser.getPassword());
    }


    private void validateAuthorities(Collection<? extends GrantedAuthority> authorities) {

        if (authorities.isEmpty()) {
            throw new EntityInvalidException("Authorities must not be empty");
        }

        for (GrantedAuthority authority : authorities) {
            validateAuthority(authority);
        }
    }


    private void validateAuthority(GrantedAuthority authority) {

        if (authority == null || authority.getAuthority() == null || authority.getAuthority().isEmpty()) {
            throw new EntityInvalidException("Authority must not be empty");
        }

        for (String validAuthority : SLAuthority.VALID_AUTHORITIES) {
            if (validAuthority.equals(authority.getAuthority())) {
                return;
            }
        }

        throw new EntityInvalidException("Authority must be one of: " + Arrays.toString(SLAuthority.VALID_AUTHORITIES));
    }


    private void validateUsername(String username) {

        if (isNullOrEmpty(username)) {
            throw new EntityInvalidException("Username must not be null or emtpy");
        }

        if (!username.matches("^[a-zA-Z0-9]*$")) {
            throw new EntityInvalidException("Username must be alphanumeric ([a-zA-Z0-9]*)");
        }
    }


    private void validateEmail(String email) {

        if (isNullOrEmpty(email)) {
            throw new EntityInvalidException("Email must not be null or empty");
        }

        EmailValidator emailValidator = new EmailValidator();

        if (!emailValidator.isValid(email, null)) {
            throw new EntityInvalidException("e-mail address is not valid");
        }
    }


    private void validatePassword(String password) {

        if (isNullOrEmpty(password)) {
            throw new EntityInvalidException("Password must not be null or empty");
        }

        if (password.length() < minPasswordLength) {
            throw new EntityInvalidException(String.format("Password must contain at least %d characters",
                    minPasswordLength));
        }
    }


    private boolean isNullOrEmpty(String value) {

        return value == null || value.isEmpty();
    }
}
