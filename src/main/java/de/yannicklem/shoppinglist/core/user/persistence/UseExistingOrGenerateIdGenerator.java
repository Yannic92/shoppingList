package de.yannicklem.shoppinglist.core.user.persistence;

import org.hibernate.HibernateException;

import org.hibernate.engine.spi.SessionImplementor;

import org.hibernate.id.IncrementGenerator;

import java.io.Serializable;


public class UseExistingOrGenerateIdGenerator extends IncrementGenerator {

    @Override
    public Serializable generate(SessionImplementor session, Object object) throws HibernateException {

        Serializable id = session.getEntityPersister(null, object).getClassMetadata().getIdentifier(object, session);

        return id != null ? id : super.generate(session, object);
    }
}
