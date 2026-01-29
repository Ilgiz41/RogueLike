package domain.item.elixir;

import com.googlecode.lanterna.TextColor;
import domain.Coordinate;
import domain.item.Item;
import domain.model.Stats;

public class ElixirRed extends AbstractElixir {

    public ElixirRed(Coordinate coordinate, boolean visibility, TextColor color) {
        super(coordinate, visibility, color);
    }

    @Override
    public void applyEffect(Stats stats) {
        stats.addAgility(5);
        stats.subStrength(3);
    }

    public String getType() {
        return "red elixir";
    }

}
