package de.yannicklem.shoppinglist.core.user.dto;

import de.yannicklem.restutils.entity.dto.RestEntityMapper;
import de.yannicklem.shoppinglist.core.user.entity.SLUser;

/**
 * @author Yannic Klem - yann.klem@gmail.com
 */
public class SLUserMapper extends RestEntityMapper<SLUserDto, SLUser> {
    @Override
    public SLUserDto toDto(SLUser entity) {
        SLUserDto slUserDto = new SLUserDto();
        slUserDto.setUsername(entity.getUsername());
        slUserDto.setEmail(entity.getEmail());
        slUserDto.setFirstName(entity.getFirstName());
        slUserDto.setLastName(entity.getLastName());
        slUserDto.setPassword(entity.getPassword());
    }

    @Override
    public SLUser toEntity(SLUserDto dto) {
        new SLUser()
    }
}
