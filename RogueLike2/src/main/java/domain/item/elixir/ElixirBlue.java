package domain.item.elixir;

import com.googlecode.lanterna.TextColor;
import domain.Coordinate;
import domain.model.Stats;

public class ElixirBlue extends AbstractElixir {

    public ElixirBlue(Coordinate coordinate, boolean visibility, TextColor color) {
        super(coordinate, visibility, color);
    }

    @Override
    public void applyEffect(Stats stats) {
        stats.addMaxHealth(10);
        stats.subAgility(3);
    }

    public String getType() {
        return "blue elixir";
    }

}
