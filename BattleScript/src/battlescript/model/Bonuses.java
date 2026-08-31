package battlescript.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Representa los bonos adicionales que pueden otorgarse durante una partida.
 *
 * Los bonos pueden corresponder a combos realizados por el Mago,
 * combos realizados por el Guerrero o una victoria con poca vida.
 */
public class Bonuses {
 // Secuencia de acciones necesaria para activar el combo del Mago.
    private final List<Action> mageCombo;

    // Puntos otorgados al completar el combo del Mago.
    private final int mageComboPoints;

    // Secuencia de acciones necesaria para activar el combo del Guerrero.
    private final List<Action> warriorCombo;

    // Puntos otorgados al completar el combo del Guerrero.
    private final int warriorComboPoints;

    // Puntos otorgados por ganar teniendo poca vida.
    private final int lowHealthVictory;

    /**
     * Constructor utilizado principalmente por el Parser.
     *
     * Se crean copias de las listas recibidas para evitar que el objeto
     * pueda ser modificado externamente después de su creación.
     */
    public Bonuses(List<Action> mageCombo, int mageComboPoints, List<Action> warriorCombo, int warriorComboPoints, int lowHealthVictory) {
        this.mageCombo = (mageCombo == null) ? new ArrayList<>() : new ArrayList<>(mageCombo);
        this.mageComboPoints = mageComboPoints;
        this.warriorCombo = (warriorCombo == null) ? new ArrayList<>() : new ArrayList<>(warriorCombo);
        this.warriorComboPoints = warriorComboPoints;
        this.lowHealthVictory = lowHealthVictory;
    }

     /**
     * Constructor por defecto.
     *
     * Representa una partida sin bonos configurados.
     * En lugar de utilizar null, se crean listas vacías y valores en cero.
     */
    public Bonuses() {
        this(new ArrayList<>(), 0, new ArrayList<>(), 0, 0);
    }

    //.unmodifiableList evita que la lista pueda modificarse
    public List<Action> getMageCombo() { return Collections.unmodifiableList(mageCombo); }
    public int getMageComboPoints() { return mageComboPoints; }
    public List<Action> getWarriorCombo() { return Collections.unmodifiableList(warriorCombo); }
    public int getWarriorComboPoints() { return warriorComboPoints; }
    public int getLowHealthVictory() { return lowHealthVictory; }
}