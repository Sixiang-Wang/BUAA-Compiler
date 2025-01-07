package Middle.Values.instructions.terminator;

import Middle.Types.Type;
import Middle.Values.BasicBlock;
import Middle.Values.instructions.Instruction;
import Middle.Values.instructions.Operator;

public abstract class TerminatorInst extends Instruction {
    public TerminatorInst(Type type, Operator op, BasicBlock basicBlock) {
        super(type, op, basicBlock);
    }
}
