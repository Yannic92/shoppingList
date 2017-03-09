package de.yannicklem.restutils.entity.owned;

import de.yannicklem.restutils.entity.RestEntity;

import de.yannicklem.shoppinglist.core.user.entity.SLUser;

import javax.persistence.MappedSuperclass;
import java.io.Serializable;

import java.util.Set;

@MappedSuperclass
public abstract class OwnedRestEntity<ID extends Serializable> extends RestEntity<ID> {

    public abstract Set<SLUser> getOwners();


    public abstract void setOwners(Set<SLUser> owners);
}
