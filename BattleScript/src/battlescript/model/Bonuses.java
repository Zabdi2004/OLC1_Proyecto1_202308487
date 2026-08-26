package battlescript.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class Bonuses {
    private final List<Action> mageCombo, warriorCombo;
    private final int mageComboPoints, warriorComboPoints, lowHealthVictory;
    public Bonuses(List mageCombo, int mageComboPoints, List warriorCombo, int warriorComboPoints, int lowHealthVictory) {
        if (mageComboPoints < 0 || warriorComboPoints < 0 || lowHealthVictory < 0){
            throw new IllegalArgumentException("Bonificaciones inválidas");
        }
        this.mageCombo = new ArrayList<Action>(mageCombo); 
        this.mageComboPoints = mageComboPoints;
        this.warriorCombo = new ArrayList<Action>(warriorCombo); 
        this.warriorComboPoints = warriorComboPoints;
        this.lowHealthVictory = lowHealthVictory;
    }
    public List<Action> getMageCombo() { return Collections.unmodifiableList(mageCombo); }
    public int getMageComboPoints() { return mageComboPoints; }
    public List<Action> getWarriorCombo() { return Collections.unmodifiableList(warriorCombo); }
    public int getWarriorComboPoints() { return warriorComboPoints; }
    public int getLowHealthVictory() { return lowHealthVictory; }
}
