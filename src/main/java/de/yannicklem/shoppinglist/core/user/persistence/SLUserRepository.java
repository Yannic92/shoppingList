package de.yannicklem.shoppinglist.core.user.persistence;

import de.yannicklem.shoppinglist.core.user.entity.SLUser;

import de.yannicklem.restutils.entity.service.RestEntityRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;
import java.util.Optional;


public interface SLUserRepository extends RestEntityRepository<SLUser, String> {

    Optional<SLUser> findByEmail(String email);

    @Query("SELECT u from SLUser u where u.createdAt < :date AND u.enabled = false")
    List<SLUser> findInactiveUsersOlderThan(@Param("date") Date date);
}
