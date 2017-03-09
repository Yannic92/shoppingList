package de.yannicklem.restutils.entity.owned;

import com.fasterxml.jackson.annotation.JsonIgnore;
import de.yannicklem.restutils.entity.RestEntityDefaultId;
import de.yannicklem.shoppinglist.core.user.entity.SLUser;

import javax.persistence.FetchType;
import javax.persistence.ManyToMany;
import javax.persistence.MappedSuperclass;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@MappedSuperclass
public abstract class OwnedRestEntity<ID extends Serializable> extends RestEntityDefaultId<ID> {

    @ManyToMany(fetch = FetchType.EAGER)
    private final Set<SLUser> owners = new HashSet<>();

    @JsonIgnore
    public Set<SLUser> getOwners() {
        return this.owners;
    }


    @JsonIgnore
    public void setOwners(Set<SLUser> owners) {
        this.owners.clear();

        if (owners != null) {
            this.owners.addAll(owners);
        }
    }
}
