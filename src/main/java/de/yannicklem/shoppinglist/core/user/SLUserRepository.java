package de.yannicklem.shoppinglist.core.user;

import de.yannicklem.shoppinglist.core.user.restapi.SLUserProjection;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;

import java.util.List;


@RepositoryRestResource(excerptProjection = SLUserProjection.class)
public interface SLUserRepository extends CrudRepository<SLUser, String> {

    @RestResource(exported = false)
    SLUser findByEmail(String email);


    @Override
    List<SLUser> findAll();
}
