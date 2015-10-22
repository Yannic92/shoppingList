package de.yannicklem.shoppinglist.core.user.registration;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;


@RepositoryRestResource(exported = false)
public interface ConfirmationRepository extends CrudRepository<Confirmation, Integer> {
}
