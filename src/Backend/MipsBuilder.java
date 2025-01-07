package Backend;

import Middle.IRModule;
import Middle.Types.ArrayType;
import Middle.Types.IntegerType;
import Middle.Types.PointerType;
import Middle.Values.*;
import Middle.Values.instructions.Instruction;
import Middle.Values.instructions.mem.AllocaInst;
import Tool.FileController;

public class MipsBuilder {
    private final IRModule irModule;
    private final Translator translator;
    StringBuilder s;
    private String mipsResult;
    public MipsBuilder(IRModule irModule){
        this.irModule = irModule;
        this.s = new StringBuilder();
        this.translator = new Translator(s);
    }


    public void buildMips(){

        // .data: global var
        s.append(".data\n");
        buildData();
        // .macro: library function
        buildLibraryFunction();
        // .text: function and main function
        s.append(".text\n");
        s.append("jal main\n");
        s.append("j return\n");
        s.append("\n");
        buildFunction();


        mipsResult = s.toString();
    }

    public void buildData(){
        for(GlobalVar globalVar:irModule.globalVars){

            if(globalVar.isInt()){
                String name = globalVar.getNameId();
                translator.addGlobal(name,globalVar);
                ConstInt constInt = (ConstInt) globalVar.getValue();
                s.append(globalVar.getNameId());
                if(((IntegerType)constInt.getType()).isI32()){
                    s.append(": .word ");
                }else {
                    s.append(": .word ");
                }
                s.append(constInt.value).append("\n");
            } else if (globalVar.isArray()) {
                ConstArray constArray = (ConstArray) globalVar.getValue();
                translator.addGlobal(globalVar.getNameId(),globalVar);
                PointerType pointer = (PointerType) globalVar.getType();
                s.append(globalVar.getNameId()).append(": ");
                IntegerType type = (IntegerType) ((ConstArray) globalVar.getValue()).elementType;

                //char saved as 4 byte
                int byteNum = 4;

                if(constArray.isInit()){
                    if(type.isI32()){
                        s.append(".word ");
                    }else {
                        s.append(".word ");
                    }
                    int capacity = ((ArrayType) pointer.getTargetType()).getCapacity();
                    for (int i = 0; i < capacity - 1; i++) {
                        s.append(((ConstInt) (constArray).get1DArray().get(i)).getValue());
                        s.append(", ");
                    }
                    s.append(((ConstInt) (constArray).get1DArray().get(capacity - 1)).getValue());
                    s.append("\n");
                } else {
                    s.append(".space ");
                    s.append(((ArrayType) pointer.getTargetType()).getCapacity() * byteNum);
                    s.append("\n");
                }
            } else if (globalVar.isString()) {
                ConstString constString = (ConstString) globalVar.getValue();
                s.append(globalVar.getNameId()).append(": .asciiz ").append(constString.getName()).append("\n");
            }
        }
        s.append("\n");
    }

    public void buildLibraryFunction(){
        for(Function function:irModule.functionList){
            if(function.isLibraryFunction()){
                s.append(".macro ");
                if ("getint".equals(function.getName())){
                    s.append("GETINT()\nli $v0, 5\n");
                }else if ("getchar".equals(function.getName())){
                    s.append("GETCHAR()\nli $v0, 12\n");
                } else if ("putint".equals(function.getName())) {
                    s.append("PUTINT()\nli $v0, 1\n");
                } else if ("putch".equals(function.getName())) {
                    s.append("PUTCH()\nli $v0, 11\n");
                } else if ("putstr".equals(function.getName())) {
                    s.append("PUTSTR()\nli $v0, 4\n");
                }
                s.append("syscall\n.end_macro\n\n");
            }

        }
    }

    public void buildFunction(){
        for(Function function:irModule.functionList){
            if(function.isLibraryFunction()){
                continue;
            }
            s.append("\n");
            s.append(function.getName());
            s.append(":\n");
            translator.rec = function.getArguments().size();
            int i = 0;
            while(translator.rec>0){
                translator.rec--;
                translator.load("$t0", "$sp", 4 * translator.rec);
                translator.addSp(function.getArguments().get(i).getNameId(),function.getArguments().get(i));
                translator.store("$t0",function.getArguments().get(i).getNameId());
                i++;
            }
            translator.rec = 0;
            for(BasicBlock basicBlock: function.basicBlockList){
                String tmpString =  basicBlock.getLabelName() + ":\n";
                s.append(tmpString);
                for(Instruction instruction: basicBlock.instructionList){

                    tmpString = "#"+instruction+"\n";
                    s.append(tmpString);

                    if(!(instruction instanceof AllocaInst)){
                        translator.addSp(instruction.getNameId(),instruction);
                    }
                    translator.translate(instruction);
                    s.append("\n");
                }
            }
        }

        s.append("return:\n");
    }

    public void filePrintln(){
        FileController.printlnMIPS(mipsResult);
    }

}
