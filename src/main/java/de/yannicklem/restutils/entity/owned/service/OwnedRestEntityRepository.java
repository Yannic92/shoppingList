package de.yannicklem.restutils.entity.owned.service;

import de.yannicklem.restutils.entity.owned.OwnedRestEntity;
import de.yannicklem.restutils.entity.service.RestEntityRepository;
import de.yannicklem.shoppinglist.core.user.entity.SLUser;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.io.Serializable;
import java.util.List;

/**
 * @author Yannic Klem - yann.klem@gmail.com
 */
public interface OwnedRestEntityRepository<Type extends OwnedRestEntity<ID>, ID extends Serializable>
        extends RestEntityRepository<Type, ID> {

    @Query("SELECT entity FROM #{#entityName} entity INNER JOIN entity.owners owner WHERE :user = owner")
    List<Type> findEntitiesOwnedBy(@Param("user") SLUser slUser);
}
