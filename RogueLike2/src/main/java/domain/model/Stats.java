package domain.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Stats {

    private int maxHealth;
    private int currentHealth;
    private int agility;
    private int strength;

    public Stats(int maxHealth){
        this.maxHealth = maxHealth;
        currentHealth = maxHealth;
    }

    //add stats

    public void addHealth(int healthPlus){
        int healthDiff = maxHealth - currentHealth;
        currentHealth += Math.min(healthDiff, healthPlus);
    }

    public void addAgility(int agilityPlus){
        agility += agilityPlus;
    }

    public void addStrength(int strengthPlus){
        strength += strengthPlus;
    }

    public void addMaxHealth(int maxHealthPlus){
        maxHealth += maxHealthPlus;
    }

    //sub stats
    //----------------------------------------------------------

    public void subHealth(int subHealth){
        currentHealth -= Math.min(currentHealth - 1, subHealth);
    }

    public void subAgility(int subAgility){
        agility -= Math.min(agility, subAgility);
    }

    public void subStrength(int  subStrength){
        strength -= Math.min(strength, subStrength);
    }

    public void subMaxHealth(int subMaxHealthPlus){
        maxHealth -= Math.min(maxHealth - 1, subMaxHealthPlus);
    }

    public void takeDamage(double damage){
        currentHealth -= damage;
    }
}
