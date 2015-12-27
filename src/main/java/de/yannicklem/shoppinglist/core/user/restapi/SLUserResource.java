package de.yannicklem.shoppinglist.core.user.restapi;

import de.yannicklem.shoppinglist.core.user.entity.SLUser;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;


public class SLUserResource extends Resource<SLUser> {

    public SLUserResource(SLUser content, Link... links) {

        super(content, links);

        this.add();
    }


    public SLUserResource(SLUser content, Iterable<Link> links) {

        super(content, links);
    }
}
