package de.yannicklem.restutils.entity.owned.dto;

import de.yannicklem.restutils.entity.dto.RestEntityDto;
import de.yannicklem.shoppinglist.core.user.entity.SLUser;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Set;

/**
 * @author Yannic Klem - yann.klem@gmail.com
 */
@Getter
@Setter
public class OwnedRestEntityDto<ID extends Serializable> extends RestEntityDto<ID>{

    private Set<SLUser> owners;
}
