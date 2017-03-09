package de.yannicklem.restutils.entity.dto;

import de.yannicklem.restutils.entity.RestEntity;

/**
 * @author Yannic Klem - yann.klem@gmail.com
 */
public interface RestEntityMapper<DTO_TYPE extends RestEntityDto, TYPE extends RestEntity> {

    DTO_TYPE toDto(TYPE entity);
    TYPE toEntity(DTO_TYPE dto);
}
