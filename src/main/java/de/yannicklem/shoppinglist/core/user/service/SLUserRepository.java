package de.yannicklem.shoppinglist.core.user.service;

import de.yannicklem.shoppinglist.core.user.entity.SLUser;

import org.springframework.data.repository.CrudRepository;

import java.util.List;


public interface SLUserRepository extends CrudRepository<SLUser, String> {

    SLUser findByEmail(String email);


    @Override
    List<SLUser> findAll();
}
