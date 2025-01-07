package Middle.Values;

import Middle.Types.FunctionType;
import Middle.Types.LabelType;
import Middle.Types.VoidType;
import Middle.Values.instructions.Instruction;
import Middle.Values.instructions.mem.StoreInst;
import Middle.Values.instructions.terminator.BrInst;
import Middle.Values.instructions.terminator.CallInst;
import Middle.Values.instructions.terminator.RetInst;


import java.util.*;

public class BasicBlock extends Value {
    public LinkedList<Instruction> instructionList = new LinkedList<>();
    public Function parentFunc;


    public BasicBlock(Function function) {
        super(String.valueOf(REG_NUMBER++), new LabelType());

        this.parentFunc = function;
        function.basicBlockList.add(this);
    }

    public void refreshReg() {
        for (Instruction inst : this.instructionList) {
            if (!(inst instanceof StoreInst ||
                    inst instanceof BrInst ||
                    inst instanceof RetInst ||
                    (inst instanceof CallInst &&
                            ((FunctionType) inst.getOperandList().get(0).getType()).getReturnType() instanceof VoidType))) {
                inst.setName("%" + REG_NUMBER++);
            }
        }
    }

    public String getLabelName() {
        return "label_" + getId();
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();

        for (Instruction instruction : this.instructionList) {
            s.append("\t").append(instruction.toString()).append("\n");
        }
        return s.toString();
    }

}
