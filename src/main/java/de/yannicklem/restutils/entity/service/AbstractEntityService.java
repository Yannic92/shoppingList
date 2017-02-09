package de.yannicklem.restutils.entity.service;

import de.yannicklem.restutils.entity.RestEntity;

import de.yannicklem.shoppinglist.core.exception.NotFoundException;

import java.io.Serializable;

import java.util.List;


/**
 * @author  Yannic Klem - yann.klem@gmail.com
 */
public abstract class AbstractEntityService<Type extends RestEntity<ID>, ID extends Serializable>
    extends AbstractEntityReadOnlyService<Type, ID> implements EntityService<Type, ID> {

    private final RestEntityRepository<Type, ID> entityRepository;
    private final EntityPersistenceHandler<Type> persistenceHandler;

    public AbstractEntityService(RestEntityRepository<Type, ID> entityRepository,
        EntityPersistenceHandler<Type> persistenceHandler) {

        super(entityRepository);
        this.entityRepository = entityRepository;
        this.persistenceHandler = persistenceHandler;
    }

    @Override
    public Type create(Type entity) {

        persistenceHandler.handleBeforeCreate(entity);

        Type createdEntity = entityRepository.save(entity);

        persistenceHandler.handleAfterCreate(entity);

        return createdEntity;
    }


    @Override
    public Type update(Type entity) {

        persistenceHandler.handleBeforeUpdate(entity);

        Type updatedEntity = entityRepository.save(entity);

        persistenceHandler.handleAfterUpdate(entity);

        return updatedEntity;
    }


    @Override
    public void delete(Type entity) {

        if (entity == null) {
            throw new NotFoundException("Entity not found");
        }

        persistenceHandler.handleBeforeDelete(entity);

        entityRepository.delete(entity);

        persistenceHandler.handleAfterDelete(entity);
    }


    @Override
    public void deleteAll() {

        List<Type> all = findAll();

        for (Type entity : all) {
            delete(entity);
        }
    }
}
