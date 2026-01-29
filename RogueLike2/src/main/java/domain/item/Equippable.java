package domain.item;

import domain.Backpack;
import domain.model.Equipment;
import domain.model.Stats;

public interface Equippable {

    void equip(Stats stats, Equipment equipment, Backpack backpack);

}
