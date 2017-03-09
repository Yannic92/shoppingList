package de.yannicklem.restutils.service;

import de.yannicklem.restutils.entity.dto.RestEntityDto;
import de.yannicklem.shoppinglist.core.user.entity.SLUser;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityLinks;


@RequiredArgsConstructor(onConstructor = @__(@Autowired ))
public abstract class MyResourceProcessor<DtoType extends RestEntityDto> {

    protected final EntityLinks entityLinks;

    public DtoType process(DtoType entityDto, SLUser currentUser) {

        if (entityDto.getId() == null) {
            entityDto.add(entityLinks.linkToSingleResource(entityDto.getClass(), entityDto.getEntityId()).withSelfRel());
        }

        return entityDto;
    }


    public abstract DtoType initializeNestedEntities(DtoType entityDto);
}
