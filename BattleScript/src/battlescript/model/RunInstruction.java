package battlescript.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class RunInstruction {
    private final List<String> matchIds;
    private final int seed;
    public RunInstruction(List matchIds, int seed) {
        if (matchIds == null || matchIds.isEmpty() || seed <= 0) throw new IllegalArgumentException("La instrucción run requiere partidas y una semilla positiva");
        this.matchIds = new ArrayList<String>(matchIds); this.seed = seed;
    }
    public List<String> getMatchIds() { return Collections.unmodifiableList(matchIds); }
    public int getSeed() { return seed; }
}
