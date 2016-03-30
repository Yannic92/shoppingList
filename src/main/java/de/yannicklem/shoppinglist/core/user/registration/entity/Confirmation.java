package de.yannicklem.shoppinglist.core.user.registration.entity;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;


@Entity
@EqualsAndHashCode
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Confirmation {

    @Id
    @GeneratedValue
    private Integer id;

    private String code;
}
