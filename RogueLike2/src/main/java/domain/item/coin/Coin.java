package domain.item.coin;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.googlecode.lanterna.TextColor;
import domain.Coordinate;
import domain.item.Item;

import java.util.Random;

public class Coin extends Item {

    private int amount;

    public Coin(Coordinate coordinate, int amount, boolean visibility, TextColor color) {
        super(coordinate, visibility, color);
        this.amount = amount;
    }

    public static int generateAmountCoin() {
        Random rand = new Random();
        return rand.nextInt(50) + 5;
    }

    public int getAmount() {
        return amount;
    }



    public String getType() {
        return "coins";
    }


}
