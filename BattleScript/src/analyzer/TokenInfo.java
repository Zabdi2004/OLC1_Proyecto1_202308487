package analyzer;

public final class TokenInfo {
    private final String type, lexeme; 
    private final int line, column;
    public TokenInfo(String type, String lexeme, int line, int column) { 
        this.type = type; 
        this.lexeme = lexeme; 
        this.line = line; 
        this.column = column;
    }
    public String getType() { return type; } 
    public String getLexeme() { return lexeme; }
    public int getLine() { return line; }
    public int getColumn() { return column; }
}
