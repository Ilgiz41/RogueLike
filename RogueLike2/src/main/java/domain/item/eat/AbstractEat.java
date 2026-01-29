package domain.item.eat;

import com.googlecode.lanterna.TextColor;
import domain.Coordinate;
import domain.item.UsableItem;

public abstract class AbstractEat extends UsableItem {

    protected AbstractEat(Coordinate coordinate, boolean visibility, TextColor color) {
        super(coordinate, visibility, color);
    }

}
