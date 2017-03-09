package de.yannicklem.shoppinglist.core.article.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import de.yannicklem.restutils.entity.owned.dto.OwnedRestEntityDto;
import de.yannicklem.shoppinglist.core.user.entity.SLUser;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Set;

/**
 * @author Yannic Klem - yann.klem@gmail.com
 */
@Getter
@Setter
@ToString
public class ArticleDto extends OwnedRestEntityDto<String> {

    private String name;
    private Double priceInEuro;
    private Set<SLUser> owners;

    @JsonIgnore
    public Set<SLUser> getOwners() {
        return this.owners;
    }
}
