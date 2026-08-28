package battlescript.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainBlock {
    private final List<RunInstruction> runInstructions;

    public MainBlock() {
        this.runInstructions = new ArrayList<>();
    }

    public MainBlock(List<RunInstruction> runInstructions) {
        this.runInstructions = new ArrayList<>(runInstructions);
    }

    public List<RunInstruction> getRunInstructions() {
        return Collections.unmodifiableList(runInstructions);
    }

    public void addRunInstruction(RunInstruction ri) {
        runInstructions.add(ri);
    }
}