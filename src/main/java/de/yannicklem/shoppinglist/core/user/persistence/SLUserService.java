package de.yannicklem.shoppinglist.core.user.persistence;

import de.yannicklem.restutils.entity.service.EntityService;
import de.yannicklem.shoppinglist.core.user.entity.SLUser;
import de.yannicklem.shoppinglist.core.user.registration.entity.Confirmation;

import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * @author Yannic Klem - yann.klem@gmail.com
 */
public interface SLUserService extends SLUserReadOnlyService, EntityService<SLUser, String>{

    void confirmUserRegistration(Confirmation confirmation, String name);

    List<SLUser> findInactiveUsersOlderThan(Date date);

    void setCurrentUser(String username);

    boolean usernameExists(String name);

    boolean emailExists(String email);

    Optional<SLUser> findByName(String name);

    Optional<SLUser> findByEmail(String email);
}
