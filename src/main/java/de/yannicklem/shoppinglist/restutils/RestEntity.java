package de.yannicklem.shoppinglist.restutils;

import java.io.Serializable;


public interface RestEntity<ID extends Serializable> {

    ID getId();


    void setId(ID id);
}
