package domain.item;

import com.googlecode.lanterna.TextColor;
import domain.Backpack;
import domain.Coordinate;
import domain.model.Equipment;
import domain.model.Stats;

public abstract class EquippableItem extends Item implements Equippable {

    protected EquippableItem(Coordinate coordinate, boolean visibility, TextColor color) {
        super(coordinate, visibility, color);
    }

    public void equip(Stats stats, Equipment equipment, Backpack backpack){
    equipEffect(stats, equipment, backpack);
    }

    public abstract void equipEffect(Stats stats, Equipment equipment, Backpack backpack);
    public abstract void unEquip(Stats stats, Equipment equipment, Backpack backpack);
    public abstract void putIntoSlot(Equipment equipment);
    public abstract void unApplyBonus(Stats stats);

}
