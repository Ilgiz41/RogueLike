package domain.item;

import com.googlecode.lanterna.TextColor;
import domain.Coordinate;
import domain.model.Stats;

public abstract class UsableItem extends Item implements Consumable{

    protected UsableItem(Coordinate coordinate, boolean visibility, TextColor color) {
        super(coordinate, visibility, color);
    }

    public final void apply(Stats stats){
        applyEffect(stats);
    }

    abstract public void applyEffect(Stats stats);

}
