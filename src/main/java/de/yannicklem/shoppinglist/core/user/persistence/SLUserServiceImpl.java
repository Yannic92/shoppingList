package de.yannicklem.shoppinglist.core.user.persistence;

import de.yannicklem.restutils.entity.service.AbstractEntityService;
import de.yannicklem.restutils.entity.service.EntityPersistenceHandler;
import de.yannicklem.restutils.entity.service.RestEntityRepository;

import de.yannicklem.shoppinglist.core.exception.EntityInvalidException;
import de.yannicklem.shoppinglist.core.exception.NotFoundException;
import de.yannicklem.shoppinglist.core.user.entity.SLUser;
import de.yannicklem.shoppinglist.core.user.registration.entity.Confirmation;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;


@Service
@Transactional
public class SLUserServiceImpl extends AbstractEntityService<SLUser, String> implements SLUserService {

    private final SLUserRepository slUserRepository;
    private final SLUserReadOnlyService slUserReadOnlyService;

    public SLUserServiceImpl(RestEntityRepository<SLUser, String> entityRepository, SLUserRepository slUserRepository,
        EntityPersistenceHandler<SLUser> persistenceHandler, SLUserReadOnlyService slUserReadOnlyService) {

        super(entityRepository, persistenceHandler);

        this.slUserRepository = slUserRepository;
        this.slUserReadOnlyService = slUserReadOnlyService;
    }

    @Override
    public void confirmUserRegistration(Confirmation confirmation, String name) {

        Optional<SLUser> slUserOptional = slUserRepository.findOne(name);

        if (!slUserOptional.isPresent() || slUserOptional.get().isEnabled()) {
            throw new EntityInvalidException("Aktivierung nicht möglich");
        }

        SLUser slUser = slUserOptional.get();

        if (slUser.getConfirmation().getCode().equals(confirmation.getCode())) {
            slUser.setEnabled(true);
            slUserRepository.save(slUser);
        } else {
            throw new EntityInvalidException("Aktivierung nicht möglich");
        }
    }


    @Override
    public List<SLUser> findInactiveUsersOlderThan(Date date) {

        return slUserRepository.findInactiveUsersOlderThan(date);
    }


    @Override
    public void setCurrentUser(String username) {

        SLUser newCurrentUser = findById(username).orElseThrow(() -> new NotFoundException("User not found"));

        SecurityContextHolder.getContext()
            .setAuthentication(new UsernamePasswordAuthenticationToken(newCurrentUser, null,
                    newCurrentUser.getAuthorities()));
    }


    @Override
    public boolean usernameExists(String name) {

        return slUserReadOnlyService.usernameExists(name);
    }


    @Override
    public boolean emailExists(String email) {

        return slUserReadOnlyService.emailExists(email);
    }


    @Override
    public Optional<SLUser> findByName(String name) {

        return slUserReadOnlyService.findByName(name);
    }


    @Override
    public Optional<SLUser> findByEmail(String email) {

        return slUserReadOnlyService.findByEmail(email);
    }
}
