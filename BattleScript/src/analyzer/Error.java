package analyzer;

/*
 * JFlex genera internamente "throw new Error(message)". Al estar esta clase
 * en el mismo paquete, debe ser una excepción para no impedir la compilación
 * del lexer generado.
 */
public class Error extends RuntimeException {
    private String type;
    private String message;
    private int line;
    private int column;

    public Error(String type, String message, int line, int column) {
        super(message);
        this.type = type;
        this.message = message;
        this.line = line;
        this.column = column;
    }

    // Constructor por si JFlex genera un throw new Error(message)
    public Error(String message) {
        this("Error", message, 0, 0);
    }

    public String getType() { return type; }
    public String getMessage() { return message; }
    public int getLine() { return line; }
    public int getColumn() { return column; }

    @Override
    public String toString() {
        return "[" + type + "] " + message + " at line " + line + ", column " + column;
    }
}
