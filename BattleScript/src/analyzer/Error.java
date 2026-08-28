package analyzer;

public class Error extends RuntimeException {
    private final String type;
    private final String message;
    private final int line;
    private final int column;
    private final String lexeme;

    public Error(String type, String message, int line, int column) {
        this(type, message, line, column, null);
    }

    public Error(String type, String message, int line, int column, String lexeme) {
        super(message);
        this.type = type;
        this.message = message;
        this.line = line;
        this.column = column;
        this.lexeme = lexeme;
    }

    public Error(String message) {
        this("Error", message, 0, 0, null);
    }

    public String getType() { return type; }
    public String getMessage() { return message; }
    public int getLine() { return line; }
    public int getColumn() { return column; }
    public String getLexeme() { return lexeme; }

    @Override
    public String toString() {
        return "[" + type + "] " + message + (lexeme != null ? " (lexema: '" + lexeme + "')" : "") 
                + " en linea " + line + ", columna " + column;
    }
}