package de.yannicklem.restutils.entity;

import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import java.io.Serializable;

/**
 * @author Yannic Klem - yann.klem@gmail.com
 */
@MappedSuperclass
public class RestEntityDefaultId<ID extends Serializable> extends RestEntity<ID> {

    @Id
    private ID entityId;

    @Override
    public ID getEntityId() {
        return entityId;
    }

    @Override
    public void setEntityId(ID id) {
        this.entityId = id;
    }
}
