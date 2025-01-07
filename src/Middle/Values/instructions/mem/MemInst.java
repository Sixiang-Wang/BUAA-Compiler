package Middle.Values.instructions.mem;

import Middle.Types.Type;
import Middle.Values.BasicBlock;
import Middle.Values.instructions.Instruction;
import Middle.Values.instructions.Operator;

public abstract class MemInst extends Instruction {
    public MemInst(Type type, Operator op, BasicBlock basicBlock) {
        super(type, op, basicBlock);
    }
}
