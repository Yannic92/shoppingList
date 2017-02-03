package de.yannicklem.restutils.entity;

import org.springframework.hateoas.MediaTypes;

import org.springframework.http.MediaType;


/**
 * @author  Yannic Klem - klem@synyx.de
 */
public class SlMediaTypes extends MediaTypes {

    public static final String HAL_JSON_UTF8_VALUE = "application/hal+json;charset=UTF-8";
    public static final MediaType HAL_JSON_UTF8 = MediaType.valueOf("application/hal+json;charset=UTF-8");
}
