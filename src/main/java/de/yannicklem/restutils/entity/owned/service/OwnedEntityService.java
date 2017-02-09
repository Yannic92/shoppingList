package de.yannicklem.restutils.entity.owned.service;

import de.yannicklem.restutils.entity.owned.OwnedRestEntity;
import de.yannicklem.restutils.entity.service.EntityService;

import de.yannicklem.shoppinglist.core.user.entity.SLUser;

import java.io.Serializable;

import java.util.List;


/**
 * @author  Yannic Klem - yann.klem@gmail.com
 */
public interface OwnedEntityService<Type extends OwnedRestEntity<ID>, ID extends Serializable>
    extends EntityService<Type, ID> {

    /**
     * Finds all entities owned by the current {@link SLUser user}. Returns ALL entities if no current user exists i.e.
     * not authenticated access/access by system.
     *
     * @return  All instances owned by the current {@link SLUser user}.
     */
    @Override
    List<Type> findAll();
}
