package de.yannicklem.shoppinglist;

import de.yannicklem.shoppinglist.core.user.entity.SLAuthority;
import de.yannicklem.shoppinglist.core.user.entity.SLUser;

import org.springframework.context.annotation.Configuration;

import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.webmvc.config.RepositoryRestMvcConfiguration;


@Configuration
public class SLRepositoryRestMVCConfig extends RepositoryRestMvcConfiguration {

    @Override
    protected void configureRepositoryRestConfiguration(RepositoryRestConfiguration config) {

        super.configureRepositoryRestConfiguration(config);

        config.setReturnBodyOnUpdate(true);
        config.setReturnBodyOnCreate(true);
        config.exposeIdsFor(SLUser.class, SLAuthority.class);
        config.setBaseUri("/api");
    }
}
