package battlescript.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Resultado del análisis sintáctico actual. Se reinicia para cada archivo analizado. */
public final class ProgramStore {
    private static final ProgramStore INSTANCE = new ProgramStore();
    private List<Strategy> strategies = new ArrayList<Strategy>();
    private List<Match> matches = new ArrayList<Match>();
    private List<RunInstruction> main = new ArrayList<RunInstruction>();
    
    private ProgramStore() { }
    public static ProgramStore getInstance() { return INSTANCE; }
    
    public void clear() { strategies.clear(); matches.clear(); main.clear(); }
    
    public void setStrategies(List strategies) { 
        this.strategies = new ArrayList<Strategy>(strategies); 
    }
    public void setMatches(List matches) { 
        this.matches = new ArrayList<Match>(matches); 
    }
    public void setMain(List main) { 
        this.main = new ArrayList<RunInstruction>(main); 
    }
    public List<Strategy> getStrategies() { 
        return Collections.unmodifiableList(strategies); 
    }
    public List<Match> getMatches() { 
        return Collections.unmodifiableList(matches); 
    }
    public List<RunInstruction> getMain() { 
        return Collections.unmodifiableList(main); 
    }
}
