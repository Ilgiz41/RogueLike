package domain.model;

import domain.item.weapon.AbstractWeapon;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Equipment {
    private AbstractWeapon abstractWeapon;
}
