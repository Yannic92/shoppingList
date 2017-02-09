package de.yannicklem.restutils.entity.service;

import de.yannicklem.restutils.entity.RestEntity;

import org.springframework.data.repository.Repository;

import java.io.Serializable;

import java.util.List;
import java.util.Optional;


/**
 * @author  Yannic Klem - yann.klem@gmail.com
 */
public interface RestEntityRepository<Type extends RestEntity, ID extends Serializable> extends Repository<Type, ID> {

    Optional<Type> findOne(ID id);


    List<Type> findAll();


    boolean exists(ID id);


    Type save(Type entity);


    void delete(Type entity);
}
