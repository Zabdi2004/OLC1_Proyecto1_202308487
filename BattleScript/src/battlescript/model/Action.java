package battlescript.model;

/** Acciones válidas del lenguaje; no se permiten acciones definidas por el usuario. */
public enum Action {
    ARCANE_BOLT(ClassType.MAGE, 12, 10, 4, true),
    FIREBALL(ClassType.MAGE, 25, 30, 2, true),
    MAGIC_BARRIER(ClassType.MAGE, 0, 20, 7, false),
    HEALING_RUNE(ClassType.MAGE, 25, 30, 5, false),
    MEDITATE(ClassType.MAGE, 25, 0, 1, false),
    SLASH(ClassType.WARRIOR, 12, 10, 4, true),
    HEAVY_STRIKE(ClassType.WARRIOR, 25, 25, 2, true),
    SHIELD_BLOCK(ClassType.WARRIOR, 0, 15, 7, false),
    WAR_CRY(ClassType.WARRIOR, 10, 20, 6, false),
    REST(ClassType.WARRIOR, 25, 0, 1, false);

    private final ClassType owner;
    private final int power;
    private final int resourceCost;
    private final int priority;
    private final boolean offensive;

    Action(ClassType owner, int power, int resourceCost, int priority, boolean offensive) {
        this.owner = owner;
        this.power = power;
        this.resourceCost = resourceCost;
        this.priority = priority;
        this.offensive = offensive;
    }

    public static Action fromString(String value) { return Action.valueOf(value); }
    public ClassType getOwner() { return owner; }
    public int getPower() { return power; }
    public int getResourceCost() { return resourceCost; }
    public int getPriority() { return priority; }
    public boolean isOffensive() { return offensive; }
    public boolean isDefense() { return this == MAGIC_BARRIER || this == SHIELD_BLOCK; }
    public boolean isHealing() { return this == HEALING_RUNE; }
    public boolean isRecovery() { return this == MEDITATE || this == REST; }
}
