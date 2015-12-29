package de.yannicklem.shoppinglist.restutils.entity;

import org.springframework.hateoas.ResourceSupport;

import java.io.Serializable;


public abstract class RestEntity<ID extends Serializable> extends ResourceSupport {

    public abstract ID getEntityId();


    public abstract void setEntityId(ID id);
}
