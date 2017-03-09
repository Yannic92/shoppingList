package de.yannicklem.restutils.entity;

import java.io.Serializable;

public abstract class RestEntity<ID extends Serializable> {

    public abstract ID getEntityId();

    public abstract void setEntityId(ID id);
}
