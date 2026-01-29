package domain.item.scrol;

import com.googlecode.lanterna.TextColor;
import domain.Coordinate;
import domain.item.Item;
import domain.item.UsableItem;

public abstract class AbstractScroll extends UsableItem {
    protected AbstractScroll(Coordinate coordinate, boolean visibility, TextColor color) {
        super(coordinate, visibility, color);
    }
}
