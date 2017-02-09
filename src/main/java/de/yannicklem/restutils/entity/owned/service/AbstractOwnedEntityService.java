package de.yannicklem.restutils.entity.owned.service;

import de.yannicklem.restutils.entity.owned.OwnedRestEntity;
import de.yannicklem.restutils.entity.service.AbstractEntityService;
import de.yannicklem.restutils.entity.service.EntityPersistenceHandler;

import de.yannicklem.shoppinglist.core.user.entity.SLUser;
import de.yannicklem.shoppinglist.core.user.security.service.CurrentUserService;

import java.io.Serializable;

import java.util.List;


/**
 * @author  Yannic Klem - yann.klem@gmail.com
 */
public abstract class AbstractOwnedEntityService<Type extends OwnedRestEntity<ID>, ID extends Serializable>
    extends AbstractEntityService<Type, ID> implements OwnedEntityService<Type, ID> {

    private final OwnedRestEntityRepository<Type, ID> ownedRestEntityRepository;
    private final CurrentUserService currentUserService;

    public AbstractOwnedEntityService(OwnedRestEntityRepository<Type, ID> entityRepository,
        EntityPersistenceHandler<Type> persistenceHandler, CurrentUserService currentUserService) {

        super(entityRepository, persistenceHandler);

        this.ownedRestEntityRepository = entityRepository;
        this.currentUserService = currentUserService;
    }

    @Override
    public List<Type> findAll() {

        SLUser currentUser = currentUserService.getCurrentUser();

        if (currentUser == null || currentUser.isAdmin()) {
            return ownedRestEntityRepository.findAll();
        } else {
            return ownedRestEntityRepository.findEntitiesOwnedBy(currentUser);
        }
    }
}
