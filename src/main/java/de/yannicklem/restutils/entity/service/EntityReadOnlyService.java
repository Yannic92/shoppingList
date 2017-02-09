package de.yannicklem.restutils.entity.service;

import de.yannicklem.restutils.entity.RestEntity;

import java.io.Serializable;

import java.util.List;
import java.util.Optional;


/**
 * @author  Yannic Klem - yann.klem@gmail.com
 */
public interface EntityReadOnlyService<Type extends RestEntity<ID>, ID extends Serializable> {

    /**
     * Finds the entity that is identified by the given id.
     *
     * @param  id  The id of the {@link RestEntity} that should be found.
     *
     * @return  An {@link Optional} of the specified {@link Type}. Empty if no instance with the given id exists.
     */
    Optional<Type> findById(ID id);


    /**
     * Finds all entities of the managed {@link Type}.
     *
     * @return  All instances of the of the managed {@link Type}.
     */
    List<Type> findAll();


    /**
     * Checks if an entity with the given id exists.
     *
     * @param  id  The id of the entity that should be checked for existence.
     *
     * @return  True if an entity with the given id exists. False if not.
     */
    boolean exists(ID id);
}
