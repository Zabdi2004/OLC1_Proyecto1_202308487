package battlescript.model;

import java.util.*;

public final class ProgramStore {
    private final Map<String, Strategy> strategies = new HashMap<>();
    private final Map<String, Match> matches = new HashMap<>();
    private MainBlock mainBlock = new MainBlock();

    private static final ProgramStore INSTANCE = new ProgramStore();

    private ProgramStore() {}

    public static ProgramStore getInstance() {
        return INSTANCE;
    }

    public void clear() {
        strategies.clear();
        matches.clear();
        mainBlock = new MainBlock();
    }

    // ─── Métodos que usa el Parser ───
    public void setStrategies(List<Strategy> list) {
        strategies.clear();
        for (Strategy s : list) {
            strategies.put(s.getName(), s);
        }
    }

    public void setMatches(List<Match> list) {
        matches.clear();
        for (Match m : list) {
            matches.put(m.getName(), m);
        }
    }

    public void setMain(List<RunInstruction> list) {
        mainBlock = new MainBlock(list);
    }

    // ─── Métodos de agregado individual ───
    public void addStrategy(Strategy s) {
        strategies.put(s.getName(), s);
    }

    public void addMatch(Match m) {
        matches.put(m.getName(), m);
    }

    public void addRunInstruction(RunInstruction ri) {
        mainBlock.addRunInstruction(ri);
    }

    // ─── Getters ───
    public Strategy getStrategy(String name) {
        return strategies.get(name);
    }

    public Match getMatch(String name) {
        return matches.get(name);
    }

    public List<Strategy> getStrategies() {
        return new ArrayList<>(strategies.values());
    }

    public List<Match> getMatches() {
        return new ArrayList<>(matches.values());
    }

    public MainBlock getMainBlock() {
        return mainBlock;
    }

    public List<RunInstruction> getMain() {
        return mainBlock.getRunInstructions();
    }
}