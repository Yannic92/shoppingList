package de.yannicklem.restutils.entity.service;

import de.yannicklem.restutils.entity.RestEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

/**
 * @author Yannic Klem - yann.klem@gmail.com
 */
@Transactional
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public abstract class AbstractEntityReadOnlyService<Type extends RestEntity<ID>, ID extends Serializable> implements EntityReadOnlyService<Type, ID> {

    private final RestEntityRepository<Type, ID> entityRepository;

    @Override
    public Optional<Type> findById(ID id) {

        if(id == null) {
            return Optional.empty();
        }

        return entityRepository.findOne(id);
    }

    @Override
    public List<Type> findAll() {
        return entityRepository.findAll();
    }

    @Override
    public boolean exists(ID id) {

        if (id == null) {
            return false;
        }

        return entityRepository.exists(id);
    }

}
