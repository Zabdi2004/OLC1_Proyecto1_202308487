package engine.battle;

import battlescript.model.Action;
import battlescript.model.ClassType;
import battlescript.model.Strategy;
import java.util.ArrayList;
import java.util.List;

public class Fighter {
    private final Strategy strategy;
    private int health;
    private final int maxHealth;
    private int resource;
    private final int maxResource;
    private int physicalAttack;
    private int magicPower;
    private int armor;
    private int magicResistance;
    private int speed;
    private int score;
    private boolean defending;
    private boolean warCryBonus;
    private final List<Action> history;

    public Fighter(Strategy strategy) {
        this.strategy = strategy;
        this.history = new ArrayList<>();
        ClassType type = strategy.getClassType();

        // Inicializar estadísticas según clase
        if (type == ClassType.MAGE) {
            maxHealth = 100;
            maxResource = 120;
            physicalAttack = 5;
            magicPower = 25;
            armor = 8;
            magicResistance = 18;
            speed = 14;
        } else { // WARRIOR
            maxHealth = 140;
            maxResource = 100;
            physicalAttack = 22;
            magicPower = 0;
            armor = 20;
            magicResistance = 8;
            speed = 10;
        }
        this.health = maxHealth;
        this.resource = maxResource;
        this.score = 0;
        this.defending = false;
        this.warCryBonus = false;
    }

    // ─── Getters ───
    public Strategy getStrategy() { return strategy; }
    public int getHealth() { return health; }
    public int getMaxHealth() { return maxHealth; }
    public int getResource() { return resource; }
    public int getMaxResource() { return maxResource; }
    public int getPhysicalAttack() { return physicalAttack; }
    public int getMagicPower() { return magicPower; }
    public int getArmor() { return armor; }
    public int getMagicResistance() { return magicResistance; }
    public int getSpeed() { return speed; }
    public int getScore() { return score; }
    public boolean isDefending() { return defending; }
    public boolean hasWarCryBonus() { return warCryBonus; }
    public List<Action> getHistory() { return new ArrayList<>(history); }

    // ─── Modificadores ───
    public void applyDamage(int damage) {
        this.health = Math.max(0, health - damage);
    }

    public void heal(int amount) {
        this.health = Math.min(maxHealth, health + amount);
    }

    public void recoverResource(int amount) {
        this.resource = Math.min(maxResource, resource + amount);
    }

    public void payResource(int cost) {
        this.resource -= cost;
    }

    public boolean canPay(int cost) {
        return resource >= cost;
    }

    public void addScore(int points) {
        this.score = Math.max(0, score + points);
    }

    public void setDefending(boolean defending) {
        this.defending = defending;
    }

    public void setWarCryBonus(boolean bonus) {
        this.warCryBonus = bonus;
    }

    public void addToHistory(Action action) {
        if (action != null) {
            history.add(action);
        }
    }

    public boolean isAlive() {
        return health > 0;
    }
}