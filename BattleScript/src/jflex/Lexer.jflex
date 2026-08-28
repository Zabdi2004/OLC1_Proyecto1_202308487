package analyzer;

import java_cup.runtime.Symbol;
import java.util.ArrayList;
import java.util.List;
import analyzer.Error;
import analyzer.TokenInfo;

%%

%class Lexer
%unicode
%cup
%line
%column
%state COMMENT
%public

%{
    private static List<Error> lexicalErrors = new ArrayList<>();
    private static List<TokenInfo> tokens = new ArrayList<>();
    
    public static List<Error> getLexicalErrors() {
        return lexicalErrors;
    }
    
    public static void clearErrors() {
        lexicalErrors.clear();
        tokens.clear();
    }
    public static List<TokenInfo> getTokens() { return new ArrayList<>(tokens); }
    
    private Symbol symbol(int type) {
        tokens.add(new TokenInfo(sym.terminalNames[type], yytext(), yyline + 1, yycolumn + 1));
        return new Symbol(type, yyline + 1, yycolumn + 1);
    }
    
    private Symbol symbol(int type, Object value) {
        tokens.add(new TokenInfo(sym.terminalNames[type], yytext(), yyline + 1, yycolumn + 1));
        return new Symbol(type, yyline + 1, yycolumn + 1, value);
    }
    
    private void addLexicalError(String message) {
        lexicalErrors.add(new Error("Léxico", message, yyline + 1, yycolumn + 1));
    }
%}

Digito = [0-9]
Letra = [a-zA-Z]
ID = {Letra}({Letra}|{Digito}|_)*
ENTERO = {Digito}+
DECIMAL = {Digito}+"."{Digito}+
WHITESPACE = [ \t\r\n]+

%%

<YYINITIAL> {
    "//"[^\n]* { /* ignorar */ }
    "/*" { yybegin(COMMENT); }
    {WHITESPACE} { /* ignorar */ }

    "mage"      { return symbol(sym.MAGE); }
    "warrior"   { return symbol(sym.WARRIOR); }
    "initial"   { return symbol(sym.INITIAL); }
    "rules"     { return symbol(sym.RULES); }
    "if"        { return symbol(sym.IF); }
    "then"      { return symbol(sym.THEN); }
    "else"      { return symbol(sym.ELSE); }
    "match"     { return symbol(sym.MATCH); }
    "players"   { return symbol(sym.PLAYERS); }
    "rounds"    { return symbol(sym.ROUNDS); }
    "scoring"   { return symbol(sym.SCORING); }
    "bonuses"   { return symbol(sym.BONUSES); }
    "main"      { return symbol(sym.MAIN); }
    "run"       { return symbol(sym.RUN); }
    "with"      { return symbol(sym.WITH); }
    "seed"      { return symbol(sym.SEED); }

    "self_health"       { return symbol(sym.SELF_HEALTH); }
    "opponent_health"   { return symbol(sym.OPPONENT_HEALTH); }
    "self_resource"     { return symbol(sym.SELF_RESOURCE); }
    "opponent_resource" { return symbol(sym.OPPONENT_RESOURCE); }
    "self_score"        { return symbol(sym.SELF_SCORE); }
    "opponent_score"    { return symbol(sym.OPPONENT_SCORE); }
    "round_number"      { return symbol(sym.ROUND_NUMBER); }
    "total_rounds"      { return symbol(sym.TOTAL_ROUNDS); }
    "self_history"      { return symbol(sym.SELF_HISTORY); }
    "opponent_history"  { return symbol(sym.OPPONENT_HISTORY); }
    "random"            { return symbol(sym.RANDOM); }

    "damage_point"          { return symbol(sym.DAMAGE_POINT); }
    "healing_point"         { return symbol(sym.HEALING_POINT); }
    "successful_defense"    { return symbol(sym.SUCCESSFUL_DEFENSE); }
    "victory_bonus"         { return symbol(sym.VICTORY_BONUS); }
    "failed_action_penalty" { return symbol(sym.FAILED_ACTION_PENALTY); }
    "mage_combo_points"     { return symbol(sym.MAGE_COMBO_POINTS); }
    "mage_combo"            { return symbol(sym.MAGE_COMBO); }
    "warrior_combo_points"  { return symbol(sym.WARRIOR_COMBO_POINTS); }
    "warrior_combo"         { return symbol(sym.WARRIOR_COMBO); }
    "low_health_victory"    { return symbol(sym.LOW_HEALTH_VICTORY); }

    "get_moves_count"   { return symbol(sym.GET_MOVES_COUNT); }
    "get_last_n_moves"  { return symbol(sym.GET_LAST_N_MOVES); }
    "get_move"          { return symbol(sym.GET_MOVE); }
    "last_move"         { return symbol(sym.LAST_MOVE); }

    "ARCANE_BOLT"    |
    "FIREBALL"       |
    "MAGIC_BARRIER"  |
    "HEALING_RUNE"   |
    "MEDITATE"       { return symbol(sym.MAGE_ACTION, yytext()); }

    "SLASH"          |
    "HEAVY_STRIKE"   |
    "SHIELD_BLOCK"   |
    "WAR_CRY"        |
    "REST"           { return symbol(sym.WARRIOR_ACTION, yytext()); }

    "==" { return symbol(sym.EQ); }
    "!=" { return symbol(sym.NEQ); }
    ">=" { return symbol(sym.GE); }
    "<=" { return symbol(sym.LE); }
    ">"  { return symbol(sym.GT); }
    "<"  { return symbol(sym.LT); }
    "&&" { return symbol(sym.AND); }
    "||" { return symbol(sym.OR); }
    "!"  { return symbol(sym.NOT); }

    "{" { return symbol(sym.LLAVEA); }
    "}" { return symbol(sym.LLAVEC); }
    "[" { return symbol(sym.CORCHETEA); }
    "]" { return symbol(sym.CORCHETEC); }
    "(" { return symbol(sym.PARENTESISA); }
    ")" { return symbol(sym.PARENTESISC); }
    ":" { return symbol(sym.OJOS); }
    "," { return symbol(sym.COMA); }

    {DECIMAL} { return symbol(sym.DECIMAL, Double.parseDouble(yytext())); }
    {ENTERO}  { return symbol(sym.ENTERO, Integer.parseInt(yytext())); }
    {ID}      { return symbol(sym.ID, yytext()); }

    [^] {
        addLexicalError("Token no reconocido (token: '" + yytext() + "' tipo: Léxico)");
    }
}

<COMMENT> {
    "*/"      { yybegin(YYINITIAL); }
    [^*\r\n]+ { /* ignorar */ }
    "*"       { /* ignorar */ }
    \r\n      { /* ignorar */ }
    \n        { /* ignorar */ }
    \r        { /* ignorar */ }
    <<EOF>>   { addLexicalError("Comentario multilínea sin cerrar (se esperaba '*/' antes de fin de archivo)"); return symbol(sym.EOF); }
}