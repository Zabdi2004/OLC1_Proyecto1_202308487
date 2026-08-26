package engine.analysis;

import analyzer.Error;
import analyzer.TokenInfo;
import battlescript.model.ProgramStore;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class AnalysisResult {
    private final ProgramStore program; 
    private final List<TokenInfo> tokens; 
    private final List<Error> errors;
    
    public AnalysisResult(ProgramStore program, List<TokenInfo> tokens, List<Error> errors) { 
        this.program=program; this.tokens=new ArrayList<TokenInfo>(tokens); 
        this.errors=new ArrayList<Error>(errors); 
    }
    public ProgramStore getProgram() { 
        return program; 
    } 
    public List<TokenInfo> getTokens() { 
        return Collections.unmodifiableList(tokens); 
    }
    public List<Error> getErrors() { 
        return Collections.unmodifiableList(errors); 
    } 
    public boolean isSuccessful() { 
        return errors.isEmpty(); 
    }
}
