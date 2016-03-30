package de.yannicklem.shoppinglist.core.user.registration.service;

import de.yannicklem.shoppinglist.core.user.registration.entity.Confirmation;
import org.springframework.data.repository.CrudRepository;


public interface ConfirmationRepository extends CrudRepository<Confirmation, Integer> {
}
