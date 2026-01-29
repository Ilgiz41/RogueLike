package domain.item.weapon;

import com.googlecode.lanterna.TextColor;
import domain.Coordinate;
import domain.item.EquippableItem;


public abstract class AbstractWeapon extends EquippableItem {
    protected AbstractWeapon(Coordinate coordinate, boolean visibility, TextColor color) {
        super(coordinate, visibility, color);
    }
}
