package de.yannicklem.shoppinglist.core.user.persistence;

import de.yannicklem.restutils.entity.service.AbstractEntityReadOnlyService;

import de.yannicklem.shoppinglist.core.user.entity.SLUser;

import org.springframework.stereotype.Service;

import java.util.Optional;


/**
 * @author  Yannic Klem - yann.klem@gmail.com
 */
@Service("readOnlySLUserService")
public class SLUserReadOnlyServiceImpl extends AbstractEntityReadOnlyService<SLUser, String>
    implements SLUserReadOnlyService {

    private final SLUserRepository slUserRepository;

    public SLUserReadOnlyServiceImpl(SLUserRepository entityRepository) {

        super(entityRepository);

        this.slUserRepository = entityRepository;
    }

    @Override
    public boolean usernameExists(String name) {

        if (name == null) {
            return false;
        }

        return slUserRepository.exists(name);
    }


    @Override
    public boolean emailExists(String email) {

        if (email == null) {
            return false;
        }

        return slUserRepository.findByEmail(email).isPresent();
    }


    @Override
    public Optional<SLUser> findByName(String name) {

        if (name == null) {
            return Optional.empty();
        }

        return slUserRepository.findOne(name);
    }


    @Override
    public Optional<SLUser> findByEmail(String email) {

        if (email == null) {
            return Optional.empty();
        }

        return slUserRepository.findByEmail(email);
    }
}
