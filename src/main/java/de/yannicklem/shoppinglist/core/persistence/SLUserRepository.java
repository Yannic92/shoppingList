package de.yannicklem.shoppinglist.core.persistence;

import de.yannicklem.shoppinglist.core.user.entity.SLUser;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;


public interface SLUserRepository extends CrudRepository<SLUser, String> {

    SLUser findByEmail(String email);


    @Override
    List<SLUser> findAll();


    @Query("SELECT u from SLUser u where u.createdAt < :date AND u.enabled = false")
    List<SLUser> findInactiveUsersOlderThan(@Param("date") Date date);
}
