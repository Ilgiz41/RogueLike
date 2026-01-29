package domain.item;

import domain.model.Stats;

public interface Consumable {
    void apply(Stats stats);
}
