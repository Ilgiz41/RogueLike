package domain.item.elixir;

import com.googlecode.lanterna.TextColor;
import domain.Coordinate;
import domain.item.Item;
import domain.model.Stats;

public class ElixirYellow extends AbstractElixir {

    public ElixirYellow(Coordinate coordinate, boolean visibility, TextColor color) {
        super(coordinate, visibility, color);
    }

    @Override
    public void applyEffect(Stats stats) {
    stats.subMaxHealth(10);
    }

    public String getType() {
        return "yellow elixir";
    }

}
