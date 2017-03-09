package de.yannicklem.restutils.entity;

import javax.persistence.MappedSuperclass;
import java.io.Serializable;

/**
 * @author Yannic Klem - yann.klem@gmail.com
 */
@MappedSuperclass
public abstract class RestEntityNoId<ID extends Serializable> extends RestEntity<ID> {

    public abstract ID getEntityId();

    public abstract void setEntityId(ID id);
}
