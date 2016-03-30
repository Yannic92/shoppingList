package de.yannicklem.shoppinglist.core;

import de.yannicklem.shoppinglist.core.user.entity.SLUser;
import de.yannicklem.shoppinglist.restutils.entity.RestEntity;

import java.io.Serializable;
import java.util.Set;


public abstract class OwnedRestEntity<ID extends Serializable> extends RestEntity<ID> {

    public abstract Set<SLUser> getOwners();


    public abstract void setOwners(Set<SLUser> owners);
}
