package de.yannicklem.shoppinglist.core.item.restapi.service;

import de.yannicklem.shoppinglist.core.item.entity.Item;
import de.yannicklem.shoppinglist.core.user.entity.SLUser;
import de.yannicklem.shoppinglist.restutils.service.RequestHandler;

import org.springframework.stereotype.Service;


@Service
public class ItemRequestHandler implements RequestHandler<Item> {

    @Override
    public void handleBeforeCreate(Item entity, SLUser currentUser) {
    }


    @Override
    public void handleBeforeUpdate(Item oldEntity, Item newEntity, SLUser currentUser) {
    }


    @Override
    public void handleRead(Item entity, SLUser currentUser) {
    }


    @Override
    public void handleBeforeDelete(Item entity, SLUser currentUser) {
    }


    @Override
    public void handleAfterCreate(Item entity, SLUser currentUser) {
    }


    @Override
    public void handleAfterUpdate(Item oldEntity, Item newEntity, SLUser currentUser) {
    }


    @Override
    public void handleAfterDelete(Item entity, SLUser currentUser) {
    }
}
