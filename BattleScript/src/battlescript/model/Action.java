package battlescript.model;

/**
 * Enumera todas las acciones disponibles en BattleScript.
 *
 * Cada acción pertenece a una clase de personaje y tiene
 * características utilizadas durante la ejecución de una batalla:
 * poder, costo de recurso, prioridad y si es ofensiva.
 */

//Para las variables con FINAL: su referencia no puede cambiar una vez definida
// STATIC: pertenece a la clase y no a una instancia particular
public enum Action {

    // Acciones del Mago
    ARCANE_BOLT(ClassType.MAGE, 12, 10, 4, true),
    FIREBALL(ClassType.MAGE, 25, 30, 2, true),
    MAGIC_BARRIER(ClassType.MAGE, 0, 20, 7, false),
    HEALING_RUNE(ClassType.MAGE, 25, 30, 5, false),
    MEDITATE(ClassType.MAGE, 25, 0, 1, false),

    // Acciones del Guerrero
    SLASH(ClassType.WARRIOR, 12, 10, 4, true),
    HEAVY_STRIKE(ClassType.WARRIOR, 25, 25, 2, true),
    SHIELD_BLOCK(ClassType.WARRIOR, 0, 15, 7, false),
    WAR_CRY(ClassType.WARRIOR, 10, 20, 6, false),
    REST(ClassType.WARRIOR, 25, 0, 1, false);

    // Clase a la que pertenece la acción.
    private final ClassType owner;

    // Daño o efecto principal producido por la acción.
    private final int power;

    // Cantidad de recurso que consume la acción.
    private final int resourceCost;

    // Prioridad utilizada para determinar el orden de ejecución.
    private final int priority;

    // Indica si la acción es ofensiva.
    private final boolean offensive;

    /**
     * Inicializa una acción con todas sus características.
     */
    Action(ClassType owner, int power, int resourceCost,
           int priority, boolean offensive) {

        this.owner = owner;
        this.power = power;
        this.resourceCost = resourceCost;
        this.priority = priority;
        this.offensive = offensive;
    }

    /**
     * Convierte una cadena de texto en una acción.
     *
     * Se utiliza cuando el analizador obtiene el nombre de una
     * acción desde el archivo BattleScript.
     */
    public static Action fromString(String value) {
        return Action.valueOf(value);
    }

    public ClassType getOwner() {
        return owner;
    }

    public int getPower() {
        return power;
    }

    public int getResourceCost() {
        return resourceCost;
    }

    public int getPriority() {
        return priority;
    }

    public boolean isOffensive() {
        return offensive;
    }

    /**
     * Determina si la acción es una acción defensiva.
     */
    public boolean isDefense() {
        return this == MAGIC_BARRIER || this == SHIELD_BLOCK;
    }

    /**
     * Determina si la acción sirve para recuperar salud.
     */
    public boolean isHealing() {
        return this == HEALING_RUNE;
    }

    /**
     * Determina si la acción sirve para recuperar recursos.
     */
    public boolean isRecovery() {
        return this == MEDITATE || this == REST;
    }
}