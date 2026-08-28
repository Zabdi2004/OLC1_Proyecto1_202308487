package engine.analysis;

import analyzer.Error;
import analyzer.Lexer;
import analyzer.Parser;
import battlescript.model.ProgramStore;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

public final class SourceAnalyzer {
    public AnalysisResult analyze(String source) {
        ProgramStore store = ProgramStore.getInstance(); 
        store.clear(); 
        Lexer.clearErrors();
        List<Error> errors = new ArrayList<Error>();
        try { 
            Parser parser = new Parser(new Lexer(new StringReader(source == null ? "" : source))); 
            parser.parse(); errors.addAll(parser.getErrors()); 
            
        }
        catch (Exception exception) { 
            errors.add(new Error("Sintáctico", exception.getMessage() == null ? "No fue posible analizar la entrada" : exception.getMessage(), 0, 0)); }
        if (errors.isEmpty()) {
            errors.addAll(new SemanticValidator().validate(store));
        }
        return new AnalysisResult(store, Lexer.getTokens(), errors);
    }
}
