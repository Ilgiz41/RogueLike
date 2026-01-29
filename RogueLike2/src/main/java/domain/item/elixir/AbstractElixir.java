package domain.item.elixir;

import com.googlecode.lanterna.TextColor;
import domain.Coordinate;
import domain.item.Item;
import domain.item.UsableItem;

public abstract class AbstractElixir extends UsableItem {

    protected AbstractElixir(Coordinate coordinate, boolean visibility, TextColor color) {
        super(coordinate, visibility, color);
    }

    public abstract String getType();
}
