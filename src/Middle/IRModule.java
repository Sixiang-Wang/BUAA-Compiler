package Middle;

import Middle.Values.*;
import Middle.Values.instructions.Instruction;
import Tool.FileController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class IRModule {
    private static final IRModule module = new IRModule();
    public List<GlobalVar> globalVars;
    public LinkedList<Function> functionList = new LinkedList<>();

    public HashMap<Integer, Instruction> instructions = new HashMap<>();

    private IRModule() {
        this.globalVars = new ArrayList<>();
    }

    public static IRModule getInstance() {
        return module;
    }




    public void addGlobalVar(GlobalVar globalVariable) {
        this.globalVars.add(globalVariable);
    }

    public void refreshRegNumber() {

        for (Function function : functionList) {
            Value.REG_NUMBER = 0;
            function.refreshArgReg();
            if (!function.isLibraryFunction()) {
                for (BasicBlock basicBlock : function.basicBlockList) {
                    if (basicBlock.instructionList.isEmpty()) {
                        System.out.println("BasicBlock Empty Error:"
                                + basicBlock.getName()
                                +" in func "+basicBlock.parentFunc);
                    }
                    basicBlock.setName(String.valueOf(Value.REG_NUMBER++));
                    basicBlock.refreshReg();
                }
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        for (GlobalVar globalVar : globalVars) {
            s.append(globalVar.toString()).append("\n");
        }
        if (!globalVars.isEmpty()) {
            s.append("\n");
        }
        for (Function function:functionList) {
            if (function.isLibraryFunction()) {
                s.append("declare ").append(function).append("\n\n");
            } else {
                s.append("define dso_local ").append(function).append("{\n");
                for (BasicBlock basicBlock : function.basicBlockList) {
                    if (basicBlock != function.basicBlockList.getFirst()) {
                        s.append("\n");
                    }
                    //s.append(";<label>:");
                    s.append(basicBlock.getName()).
                            append(":\n").
                            append(basicBlock);
                }
                s.append("}\n\n");
            }
        }
        return s.toString();
    }

    public void filePrintln(){
        FileController.printlnLLVM(toString());
    }
    public void println(){
        System.out.println(this);
    }
}
