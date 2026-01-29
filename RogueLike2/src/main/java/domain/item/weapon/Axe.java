package domain.item.weapon;

import com.googlecode.lanterna.TextColor;
import domain.Backpack;
import domain.Coordinate;
import domain.item.EquippableItem;
import domain.item.Item;
import domain.model.Equipment;
import domain.model.Stats;

public class Axe extends AbstractWeapon {

    public Axe(Coordinate coordinate, boolean visibility, TextColor color) {
        super(coordinate, visibility, color);
    }

    @Override
    public void equipEffect(Stats stats, Equipment equipment, Backpack backpack) {
        stats.addStrength(10);
        if (equipment.getAbstractWeapon() != null){
            unEquip(stats, equipment, backpack);
        }
        putIntoSlot(equipment);
    }

    @Override
    public void unEquip(Stats stats, Equipment equipment, Backpack backpack) {
        backpack.addItem(equipment.getAbstractWeapon());
        equipment.getAbstractWeapon().unApplyBonus(stats);
        equipment.setAbstractWeapon(null);
    }

    public void unApplyBonus(Stats stats) {
        stats.subStrength(10);
    }

    @Override
    public void putIntoSlot(Equipment equipment) {
        equipment.setAbstractWeapon(this);
    }

    public String getType(){
        return "axe";
    }
}
