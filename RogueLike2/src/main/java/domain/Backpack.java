package domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import domain.item.Item;
import domain.item.UsableItem;
import domain.item.coin.Coin;
import domain.item.eat.AbstractEat;
import domain.item.elixir.AbstractElixir;
import domain.item.scrol.AbstractScroll;
import domain.item.weapon.AbstractWeapon;
import domain.item.weapon.Axe;
import domain.model.Stats;

import java.util.ArrayList;
import java.util.List;


public class Backpack {

    private List<Item> elixirs = new ArrayList<>(8);
    private List<Item> scrolls = new ArrayList<>(8);
    private List<Item> weapons = new ArrayList<>(8);
    private List<Item> eats = new ArrayList<>(8);
    private int coinCount = 0;

    private int currentItem;
    private int iter = 0;
    private BackpackState state = BackpackState.SHOW_BACKPACK;

    public enum BackpackState {
        SHOW_DIALOG_MENU_FOR_ITEM, SHOW_BACKPACK
    }

    Backpack() {

    }

    public void nextIter() {
        if (iter < 3) {
            iter += 1;
        }
    }

    public void backIter() {
        if (iter > 0) {
            iter -= 1;
        }
    }

    public void clearIter() {
        iter = 0;
    }

    public boolean addItem(Item item) throws IllegalStateException {
        boolean itemAdded = false;
        switch (item){
            case AbstractElixir elixir -> {
                if (elixirs.size() < 9){
                    elixirs.add(elixir);
                    itemAdded = true;
                }
            }
            case AbstractEat eat -> {
                if (eats.size() < 9){
                    eats.add(eat);
                    itemAdded = true;
                }
            }
            case AbstractScroll scroll -> {
                if (scrolls.size() < 9) {
                    scrolls.add(scroll);
                    itemAdded = true;
                }
            }
            case AbstractWeapon weapon -> {
                if (weapons.size() < 9) {
                    weapons.add(weapon);
                    itemAdded = true;
                }
            }
            case Coin coin -> {
                coinCount += coin.getAmount();
                itemAdded = true;
            }
            default -> throw new IllegalStateException("Unexpected value: " + item);
        }
        return itemAdded;
    }

    //GET

    @JsonIgnore
    public String getBackpackType() {
        return switch (iter) {
            case 0 -> "Эликсиры";
            case 1 -> "Свитки";
            case 2 -> "Оружие";
            case 3 -> "Еда";
            default -> "none";
        };
    }

    @JsonIgnore
    public List<Item> getItems() {
        switch (iter) {
            case 0 -> {
                return elixirs;
            }
            case 1 -> {
                return scrolls;
            }
            case 2 -> {
                return weapons;
            }
            case 3 -> {
                return eats;
            }
            default -> {
                return null;
            }
        }
    }

    @JsonIgnore
    public int getIter() {
        return iter;
    }

    @JsonIgnore
    public BackpackState getState() {
        return state;
    }

    @JsonIgnore
    public int getCurrentItem() {
        return currentItem;
    }

    public int getCoinCount(){
        return coinCount;
    }

    @JsonIgnore
    public void setState(BackpackState state) {
        this.state = state;
    }

    @JsonIgnore
    public void setCurrentItem(int itemIndex) {
        currentItem = itemIndex;
    }
}
