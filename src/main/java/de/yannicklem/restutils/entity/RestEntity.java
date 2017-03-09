package de.yannicklem.restutils.entity;

import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.hateoas.ResourceSupport;

import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import java.io.Serializable;


@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class RestEntity<ID extends Serializable> extends ResourceSupport {

    @LastModifiedDate
    private Long lastModified;

    public abstract ID getEntityId();

    public abstract void setEntityId(ID id);

    public Long getLastModified() {

        return this.lastModified;
    }

    public void setLastModified(Long lastModified) {

        this.lastModified = lastModified;
    }
}
