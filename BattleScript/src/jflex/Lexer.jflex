package jflex;

import java_cup.runtime.Symbol;

%%

%class Lexer
%unicode
%cup
%line
%column
%state COMMENT /* Para los comentarios multilínea */
%public

%{
    private Symbol symbol(int type) {
        return new Symbol(type, yyline + 1, yycolumn + 1);
    }
    private Symbol symbol(int type, Object value) {
        return new Symbol(type, yyline + 1, yycolumn + 1, value);
    }
%}

/* ~ ~ ~ ~ Definiciones ~ ~ ~ ~ */

Digito = [0-9]
Letra = [a-zA-Z]
ID = {Letra}({Letra}|{Digito}|_)* //Identificador
Entero = {Digito}+
Decimal = {Digito}+"."{Digito}+
Whitespace = [ \t\r\n]+


%%
//Estado Inicial
<YYINITIAL> {

    /* ~ ~ ~ Reglas para comentarios ~ ~ ~ */
    "//"[^\r\n]* {/* Ignorar comentario */}

    "/*" { yybegin(COMMENT); }  // al abrir "/*", cambia al estado COMMENT
    
    {Whitespace}    { /* Ignorar espacios */ }
    
    /* ~ ~ ~ ~ ~ Palabras reservadas ~ ~ ~ ~ ~ */
    "mage" { return symbol(sym.MAGE); }
    "warrior" { return symbol(sym.WARRIOR); }
    "initial" { return symbol(sym.INITIAL); }
    "rules" { return symbol(sym.RULES); }
    "if" { return symbol(sym.IF); }
    "then" { return symbol(sym.THEN); }
    "else" { return symbol(sym.ELSE); }
    "match" { return symbol(sym.MATCH); }
    "players" { return symbol(sym.PLAYERS); }
    "rounds" { return symbol(sym.ROUNDS); }
    "scoring" { return symbol(sym.SCORING); }
    "bonuses" { return symbol(sym.BONUSES); }
    "main" { return symbol(sym.MAIN); }
    "run" { return symbol(sym.RUN); }
    "with" { return symbol(sym.WITH); }
    "seed" { return symbol(sym.SEED); }

    /* ~ ~ ~ ~ ~ Estados ~ ~ ~ ~ ~ */
    "self_health"            { return symbol(sym.SELF_HEALTH); }
    "opponent_health"        { return symbol(sym.OPPONENT_HEALTH); }
    "self_resource"          { return symbol(sym.SELF_RESOURCE); }
    "opponent_resource"      { return symbol(sym.OPPONENT_RESOURCE); }
    "self_score"             { return symbol(sym.SELF_SCORE); }
    "opponent_score"         { return symbol(sym.OPPONENT_SCORE); }
    "round_number"           { return symbol(sym.ROUND_NUMBER); }
    "total_rounds"           { return symbol(sym.TOTAL_ROUNDS); }
    "self_history"           { return symbol(sym.SELF_HISTORY); }
    "opponent_history"       { return symbol(sym.OPPONENT_HISTORY); }
    "random"                 { return symbol(sym.RANDOM); }

    /* ~ ~ ~ ~ ~ Propiedades ~ ~ ~ ~ ~ */
    "damage_point"           { return symbol(sym.DAMAGE_POINT); }
    "healing_point"          { return symbol(sym.HEALING_POINT); }
    "successful_defense"     { return symbol(sym.SUCCESSFUL_DEFENSE); }
    "victory_bonus"          { return symbol(sym.VICTORY_BONUS); }
    "failed_action_penalty"  { return symbol(sym.FAILED_ACTION_PENALTY); }
    "mage_combo_points"      { return symbol(sym.MAGE_COMBO_POINTS); }
    "mage_combo"             { return symbol(sym.MAGE_COMBO); }
    "warrior_combo_points"   { return symbol(sym.WARRIOR_COMBO_POINTS); }
    "warrior_combo"          { return symbol(sym.WARRIOR_COMBO); }
    "low_health_victory"     { return symbol(sym.LOW_HEALTH_VICTORY); }

    /* ~ ~ ~ ~ ~ Funciones ~ ~ ~ ~ ~ */
    "get_moves_count"        { return symbol(sym.GET_MOVES_COUNT); }
    "get_last_n_moves"       { return symbol(sym.GET_LAST_N_MOVES); }
    "get_move"                { return symbol(sym.GET_MOVE); }
    "last_move"               { return symbol(sym.LAST_MOVE); }

    /* ~ ~ ~ ~ ~ Acciones ~ ~ ~ ~ ~ */
    "ARCANE_BOLT"    |
    "FIREBALL"       |
    "MAGIC_BARRIER"  |
    "HEALING_RUNE"   |
    "MEDITATE"       |
    "SLASH"          |
    "HEAVY_STRIKE"   |
    "SHIELD_BLOCK"   |
    "WAR_CRY"        |
    "REST"                   { return symbol(sym.ACTION, yytext()); }

    /* ~ ~ ~ ~ ~ Operadores ~ ~ ~ ~ ~ */
    "=="                     { return symbol(sym.EQ); }
    "!="                     { return symbol(sym.NEQ); }
    ">="                     { return symbol(sym.GE); }
    "<="                     { return symbol(sym.LE); }
    ">"                      { return symbol(sym.GT); }
    "<"                      { return symbol(sym.LT); }

    "&&"                     { return symbol(sym.AND); }
    "||"                     { return symbol(sym.OR); }
    "!"                      { return symbol(sym.NOT); }

    /* ~ ~ ~ ~ ~ Símbolos ~ ~ ~ ~ ~ */
    "{"                      { return symbol(sym.LBRACE); }
    "}"                      { return symbol(sym.RBRACE); }
    "["                      { return symbol(sym.LBRACKET); }
    "]"                      { return symbol(sym.RBRACKET); }
    "("                      { return symbol(sym.LPAREN); }
    ")"                      { return symbol(sym.RPAREN); }
    ":"                      { return symbol(sym.COLON); }
    ","                      { return symbol(sym.COMMA); }

    /* ~ ~ ~ ~ ~ Literales numéricos ~ ~ ~ ~ ~ */
    {Decimal}                  { return symbol(sym.Decimal, Double.parseDouble(yytext())); }
    {Entero}                    { return symbol(sym.Entero, Integer.parseInt(yytext())); }

    /* ~ ~ ~ ~ ~ Identificadores ~ ~ ~ ~ ~ */
    {ID}                     { return symbol(sym.ID, yytext()); }

    /* ~ ~ ~ ~ ~ Cualquier otro carácter: error léxico ~ ~ ~ ~ ~ */
    [^]                      {
                                System.err.println("Error léxico: caracter no reconocido '" + yytext()
                                    + "' en línea " + (yyline + 1) + ", columna " + (yycolumn + 1));
                              }
}


<COMMENT> {
    "*/"      { yybegin(YYINITIAL); }  // Al cerrar "*/", regresa al estado normal
    [^*\r\n]+ { /* Ignorar el contenido del comentario*/ }
    "*"       { /* Consumir asteriscos sueltos que no cierren el comentario */ }
    \r\n      { /* Ignorar Salto de Línea */ }
    \n        { /* Ignorar Salto de Línea */ }
    \r        { /* Ignorar Salto de Línea */ }
    <<EOF>>   { registrarError("Comentario multilínea sin cerrar");
                return new Symbol(sym.EOF);}     

}

