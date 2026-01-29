package domain.item.elixir;

import com.googlecode.lanterna.TextColor;
import domain.Coordinate;
import domain.model.Stats;

public class ElixirGreen extends AbstractElixir {

    public ElixirGreen(Coordinate coordinate, boolean visibility, TextColor color) {
        super(coordinate, visibility, color);
    }

    @Override
    public void applyEffect(Stats stats) {
        stats.subMaxHealth(5);
        stats.addStrength(5);
        stats.addAgility(5);
    }

    public String getType() {
        return "green elixir";
    }

}
