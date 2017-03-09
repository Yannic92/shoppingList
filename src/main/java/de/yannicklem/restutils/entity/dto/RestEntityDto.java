package de.yannicklem.restutils.entity.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.hateoas.ResourceSupport;

import java.io.Serializable;

/**
 * @author Yannic Klem - yann.klem@gmail.com
 */
@Getter
@Setter
public abstract class RestEntityDto<ID extends Serializable> extends ResourceSupport {

    private ID entityId;
}
