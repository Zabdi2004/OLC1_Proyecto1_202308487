package battlescript.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Bonuses {
    private final List<Action> mageCombo;
    private final int mageComboPoints;
    private final List<Action> warriorCombo;
    private final int warriorComboPoints;
    private final int lowHealthVictory;

    // Constructor que usa el Parser
    public Bonuses(List<Action> mageCombo, int mageComboPoints, List<Action> warriorCombo, int warriorComboPoints, int lowHealthVictory) {
        this.mageCombo = (mageCombo == null) ? new ArrayList<>() : new ArrayList<>(mageCombo);
        this.mageComboPoints = mageComboPoints;
        this.warriorCombo = (warriorCombo == null) ? new ArrayList<>() : new ArrayList<>(warriorCombo);
        this.warriorComboPoints = warriorComboPoints;
        this.lowHealthVictory = lowHealthVictory;
    }

    // Constructor por defecto (para el parser si no encuentra)
    public Bonuses() {
        this(new ArrayList<>(), 0, new ArrayList<>(), 0, 0);
    }

    public List<Action> getMageCombo() { return Collections.unmodifiableList(mageCombo); }
    public int getMageComboPoints() { return mageComboPoints; }
    public List<Action> getWarriorCombo() { return Collections.unmodifiableList(warriorCombo); }
    public int getWarriorComboPoints() { return warriorComboPoints; }
    public int getLowHealthVictory() { return lowHealthVictory; }
}