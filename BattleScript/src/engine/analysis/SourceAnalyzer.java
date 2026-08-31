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

        // Limpiar el programa anterior antes de analizar uno nuevo.
        store.clear();

        // Limpiar errores y tokens del análisis anterior.
        Lexer.clearErrors();

        List<Error> errors = new ArrayList<>();

        try {

            String input = source == null ? "" : source;

            Lexer lexer = new Lexer(new StringReader(input));

            Parser parser = new Parser(lexer);

            parser.parse();

            // Obtener errores léxicos y sintácticos.
            errors.addAll(parser.getErrors());

        } catch (Exception exception) {

            errors.add(
                new Error(
                    "Sintáctico",
                    exception.getMessage() == null
                        ? "No fue posible analizar la entrada"
                        : exception.getMessage(),
                    0,
                    0
                )
            );
        }

        /*
         * Aunque existan errores léxicos o sintácticos,
         * se valida todo lo que el parser consiguió construir.
         */
        errors.addAll(new SemanticValidator().validate(store));

        return new AnalysisResult(
            store,
            Lexer.getTokens(),
            errors
        );
    }
}