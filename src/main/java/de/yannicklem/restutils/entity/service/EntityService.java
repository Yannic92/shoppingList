package de.yannicklem.restutils.entity.service;

import de.yannicklem.restutils.entity.RestEntity;

import java.io.Serializable;


/**
 * Service to handle persistence operations for {@link RestEntity rest entities}.
 *
 * @param  <Type>  The {@link Class} type of the managed entity.
 * @param  <ID>  The {@link Class} type of the id of the managed entity.
 *
 * @author  Yannic Klem - mail@yannic-klem.de
 */
public interface EntityService<Type extends RestEntity<ID>, ID extends Serializable>
    extends EntityReadOnlyService<Type, ID> {

    /**
     * Persists the given entity and returns the persisted instance. Use the returned instance for further operations.
     *
     * @param  entity  The entity that should be persisted.
     *
     * @return  The persisted instance of the given entity.
     *
     * @throws  de.yannicklem.shoppinglist.core.exception.AlreadyExistsException  if an entity that matches to the given
     *                                                                            entity already exists.
     */
    Type create(Type entity);


    /**
     * Updates the given entity and returns the updated instance. Use the returned instance for further operations.
     *
     * @param  entity  The entity that should be updated.
     *
     * @return  The updated instance of the given entity.
     */
    Type update(Type entity);


    /**
     * Deletes the given entity.
     *
     * @param  entity
     */
    void delete(Type entity);


    void deleteAll();
}
