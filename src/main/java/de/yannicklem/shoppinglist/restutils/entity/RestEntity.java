package de.yannicklem.shoppinglist.restutils.entity;

import java.io.Serializable;


public interface RestEntity<ID extends Serializable> {

    ID getId();


    void setId(ID id);
}
