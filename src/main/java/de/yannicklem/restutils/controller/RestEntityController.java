package de.yannicklem.restutils.controller;

import de.yannicklem.restutils.entity.RestEntity;
import de.yannicklem.restutils.entity.dto.RestEntityDto;
import de.yannicklem.restutils.entity.dto.RestEntityMapper;
import de.yannicklem.restutils.entity.service.EntityService;
import de.yannicklem.restutils.service.MyResourceProcessor;
import de.yannicklem.restutils.service.RequestHandler;
import de.yannicklem.shoppinglist.core.exception.NotFoundException;
import de.yannicklem.shoppinglist.core.user.entity.SLUser;
import de.yannicklem.shoppinglist.core.user.persistence.SLUserService;
import lombok.RequiredArgsConstructor;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityLinks;
import org.springframework.hateoas.Resources;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.Serializable;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static java.lang.String.*;
import static java.lang.invoke.MethodHandles.lookup;
import static org.apache.log4j.Logger.getLogger;


@RequiredArgsConstructor(onConstructor = @__(@Autowired ))
public abstract class RestEntityController<DtoType extends RestEntityDto<ID>, Type extends RestEntity<ID>, ID extends Serializable> {

    private static final Logger LOGGER = getLogger(lookup().lookupClass());

    protected final SLUserService slUserService;

    protected final EntityService<Type, ID> entityService;

    protected final RequestHandler<DtoType> requestHandler;

    protected final RestEntityMapper<DtoType, Type> entityMapper;

    protected final MyResourceProcessor<DtoType> resourceProcessor;

    protected final EntityLinks entityLinks;

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public HttpEntity<? extends DtoType> getSpecificEntity(@PathVariable("id") ID id, Principal principal) {

        Type specificEntity = entityService.findById(id).orElseThrow(() -> new NotFoundException("Entity not found"));
        DtoType specfifcEntityDto = entityMapper.toDto(specificEntity);

        SLUser currentUser = principal == null ? null : slUserService.findById(principal.getName()).orElse(null);

        requestHandler.handleRead(specfifcEntityDto, currentUser);

        return new HttpEntity<>(resourceProcessor.process(specfifcEntityDto, currentUser));
    }


    @RequestMapping(method = RequestMethod.GET)
    public HttpEntity<? extends Resources<? extends DtoType>> getAllEntities(Principal principal) {

        SLUser currentUser = principal == null ? null : slUserService.findById(principal.getName()).orElse(null);

        List<DtoType> all = entityService.findAll().stream().map(entityMapper::toDto).collect(Collectors.toList());
        List<DtoType> resourcesList = new ArrayList<>();

        for (DtoType entityDto : all) {
            try {
                requestHandler.handleRead(entityDto, currentUser);
                resourcesList.add(resourceProcessor.process(entityDto, currentUser));
            } catch (Exception e) {
                LOGGER.debug(format("Filtered %s with id '%s'", entityDto.getClass().getTypeName(),
                        entityDto.getEntityId().toString()));
            }
        }

        Resources<? extends DtoType> entityResources = new Resources<>(resourcesList);

        if (!all.isEmpty()) {
            entityResources.add(entityLinks.linkToCollectionResource(all.get(0).getClass()));
        }

        return new HttpEntity<>(entityResources);
    }


    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public HttpEntity<? extends DtoType> putEntity(@RequestBody DtoType entityDto, @PathVariable ID id, Principal principal) {

        entityDto.setEntityId(id);

        resourceProcessor.initializeNestedEntities(entityDto);

        SLUser currentUser = principal == null ? null : slUserService.findById(principal.getName()).orElse(null);

        if (!entityService.exists(id)) {
            return createEntity(entityDto, currentUser);
        } else {
            return updateEntity(entityDto, currentUser);
        }
    }


    @RequestMapping(method = RequestMethod.POST)
    public HttpEntity<? extends DtoType> postEntity(@RequestBody DtoType entity, Principal principal) {

        resourceProcessor.initializeNestedEntities(entity);

        SLUser currentUser = principal == null ? null : slUserService.findById(principal.getName()).orElse(null);

        return createEntity(entity, currentUser);
    }


    protected HttpEntity<? extends DtoType> createEntity(DtoType entityDto, SLUser currentUser) {

        requestHandler.handleBeforeCreate(entityDto, currentUser);

        Type createdEntity = entityService.create(entityMapper.toEntity(entityDto));
        DtoType createdEntityDto = entityMapper.toDto(createdEntity);

        DtoType createdEntityResource = resourceProcessor.process(createdEntityDto, currentUser);

        requestHandler.handleAfterCreate(createdEntityDto, currentUser);

        return new ResponseEntity<>(createdEntityResource, HttpStatus.CREATED);
    }


    protected HttpEntity<? extends DtoType> updateEntity(DtoType entityDto, SLUser currentUser) {

        Type currentEntity = entityService.findById(entityDto.getEntityId()).orElseThrow(
                () -> new NotFoundException("Entity Not Found")
        );
        DtoType currentEntityDto = entityMapper.toDto(currentEntity);

        requestHandler.handleBeforeUpdate(currentEntityDto, entityDto, currentUser);

        Type updatedEntity = entityService.update(entityMapper.toEntity(entityDto));
        DtoType updatedEntityDto = entityMapper.toDto(updatedEntity);

        DtoType updatedEntityResource = resourceProcessor.process(updatedEntityDto, currentUser);

        requestHandler.handleAfterUpdate(currentEntityDto, updatedEntityDto, currentUser);

        return new ResponseEntity<>(updatedEntityResource, HttpStatus.OK);
    }


    @RequestMapping(method = RequestMethod.DELETE, value = "/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteEntity(@PathVariable ID id, Principal principal) {

        SLUser currentUser = principal == null ? null : slUserService.findById(principal.getName()).orElse(null);

        Type toDelete = entityService.findById(id).orElseThrow(() -> new NotFoundException("Entity Not Found"));
        DtoType toDeleteDto = entityMapper.toDto(toDelete);

        requestHandler.handleBeforeDelete(toDeleteDto, currentUser);

        entityService.delete(toDelete);

        requestHandler.handleAfterDelete(toDeleteDto, currentUser);
    }
}
