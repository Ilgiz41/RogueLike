package domain.item.weapon;

import com.googlecode.lanterna.TextColor;
import domain.Backpack;
import domain.Coordinate;
import domain.model.Equipment;
import domain.model.Stats;

public class Sword extends AbstractWeapon {

    public Sword(Coordinate coordinate, boolean visibility, TextColor color) {
        super(coordinate, visibility, color);
    }

    @Override
    public void equipEffect(Stats stats, Equipment equipment, Backpack backpack) {
        stats.addAgility(10);
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
        stats.subAgility(10);
    }

    @Override
    public void putIntoSlot(Equipment equipment) {
        equipment.setAbstractWeapon(this);
    }

    public String getType(){
        return "sword";
    }

}
