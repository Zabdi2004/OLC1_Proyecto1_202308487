package battlescript.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * El bloque main contiene las instrucciones run que indican
 * qué partidas deben ejecutarse y con qué semilla.
 */
public class MainBlock {

    // Lista de instrucciones run definidas en el programa.
    private final List<RunInstruction> runInstructions;

    // Constructor de Bloque vacio
    public MainBlock() {
        this.runInstructions = new ArrayList<>();
    }

    //Constructor utilizado cuando el Parser ya tiene las instrucciones obtenidas del archivo.
    public MainBlock(List<RunInstruction> runInstructions) {
        this.runInstructions = new ArrayList<>(runInstructions);
    }

    //Obtiene las instrucciones del bloque main.
    public List<RunInstruction> getRunInstructions() {
        return Collections.unmodifiableList(runInstructions);
    }

    // Agrega una instrucción run al bloque main.
    public void addRunInstruction(RunInstruction ri) {
        runInstructions.add(ri);
    }
}