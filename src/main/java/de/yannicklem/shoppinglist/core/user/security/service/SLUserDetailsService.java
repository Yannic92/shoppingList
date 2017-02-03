package de.yannicklem.shoppinglist.core.user.security.service;

import de.yannicklem.shoppinglist.core.user.entity.SLUser;
import de.yannicklem.shoppinglist.core.user.persistence.SLUserReadOnlyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * @author Yannic Klem - yann.klem@gmail.com
 */
@Service
public class SLUserDetailsService implements UserDetailsService {

    private final SLUserReadOnlyService slUserReadOnlyService;

    @Autowired
    public SLUserDetailsService(@Qualifier("readOnlySLUserService") SLUserReadOnlyService slUserReadOnlyService) {
        this.slUserReadOnlyService = slUserReadOnlyService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<SLUser> userOptional = slUserReadOnlyService.findByName(username);

        if (!userOptional.isPresent()) {
            userOptional = slUserReadOnlyService.findByEmail(username.toLowerCase());
        }

        return userOptional.orElseThrow(
                () -> new UsernameNotFoundException(String.format("Username '%s' not found", username))
        );
    }
}
