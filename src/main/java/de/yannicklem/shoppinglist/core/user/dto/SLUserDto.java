package de.yannicklem.shoppinglist.core.user.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import de.yannicklem.restutils.entity.dto.RestEntityDto;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author Yannic Klem - yann.klem@gmail.com
 */
@Getter
@Setter
@ToString
public class SLUserDto extends RestEntityDto<String> {

    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private String password;

    public SLUserDto() {
    }

    @Override
    @JsonIgnore
    public String getEntityId() {
        return super.getEntityId();
    }

    @Override
    @JsonIgnore
    public void setEntityId(String entityId) {
        super.setEntityId(entityId);
    }
}
