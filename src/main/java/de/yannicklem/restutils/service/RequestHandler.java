package de.yannicklem.restutils.service;

import de.yannicklem.restutils.entity.dto.RestEntityDto;
import de.yannicklem.shoppinglist.core.user.entity.SLUser;


public interface RequestHandler<DtoType extends RestEntityDto> {

    void handleBeforeCreate(DtoType entityDto, SLUser currentUser);


    void handleBeforeUpdate(DtoType oldEntityDto, DtoType newEntityDto, SLUser currentUser);


    void handleRead(DtoType entityDto, SLUser currentUser);


    void handleBeforeDelete(DtoType entityDto, SLUser currentUser);


    void handleAfterCreate(DtoType entityDto, SLUser currentUser);


    void handleAfterUpdate(DtoType oldEntityDto, DtoType newEntityDto, SLUser currentUser);


    void handleAfterDelete(DtoType entityDto, SLUser currentUser);
}
