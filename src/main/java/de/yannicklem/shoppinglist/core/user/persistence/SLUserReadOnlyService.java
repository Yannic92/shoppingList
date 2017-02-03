package de.yannicklem.shoppinglist.core.user.persistence;

import de.yannicklem.restutils.entity.service.EntityReadOnlyService;
import de.yannicklem.shoppinglist.core.user.entity.SLUser;

import java.util.Optional;

/**
 * @author Yannic Klem - yann.klem@gmail.com
 */
public interface
SLUserReadOnlyService extends EntityReadOnlyService<SLUser, String> {

    boolean usernameExists(String name);

    boolean emailExists(String email);

    Optional<SLUser> findByName(String name);

    Optional<SLUser> findByEmail(String email);
}
