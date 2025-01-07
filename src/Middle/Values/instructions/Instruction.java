package Middle.Values.instructions;

import Middle.Types.Type;
import Middle.Values.*;
import Middle.Values.instructions.terminator.BrInst;
import Middle.Values.instructions.terminator.RetInst;

public abstract class Instruction extends User {
    private Operator op;
    public BasicBlock parentBlock;

    public Instruction(Type type, Operator op, BasicBlock basicBlock) {
        super("", type);
        this.op = op;
        this.parentBlock = basicBlock;
    }

    public Operator getOperator() {
        return op;
    }

    public void setOperator(Operator op) {
        this.op = op;
    }

    public void addInstToBlock(BasicBlock basicBlock) {
        if (basicBlock.instructionList.isEmpty() ||
                (!(basicBlock.instructionList.getLast() instanceof BrInst) &&
                        !(basicBlock.instructionList.getLast() instanceof RetInst))) {
            this.parentBlock = basicBlock;
            basicBlock.instructionList.add(this);
        }
    }



}
