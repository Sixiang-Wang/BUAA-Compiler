package Backend;

import Middle.Types.*;
import Middle.Values.*;
import Middle.Values.instructions.BinaryInst;
import Middle.Values.instructions.ConvInst;
import Middle.Values.instructions.Instruction;
import Middle.Values.instructions.Operator;
import Middle.Values.instructions.mem.AllocaInst;
import Middle.Values.instructions.mem.GEPInst;
import Middle.Values.instructions.mem.LoadInst;
import Middle.Values.instructions.mem.StoreInst;
import Middle.Values.instructions.terminator.BrInst;
import Middle.Values.instructions.terminator.CallInst;
import Middle.Values.instructions.terminator.RetInst;

import java.util.LinkedHashMap;
import java.util.Map;

public class Translator {
    public Translator(StringBuilder s){
        this.s = s;
    }


    public final Map<String, Reg> mem = new LinkedHashMap<>();
    public int spOff = 0;
    public int rec = 0;
    private final StringBuilder s;

    public void addGlobal(String name, Value value){
        if(mem.containsKey(name)){
            return;
        }
        Reg reg = new Reg("", 0, value);
        mem.put(name,reg);
    }
    public void addSp(String name, Value value){
        if (mem.containsKey(name)) {
            return;
        }
        spOff -= 4;
        Reg reg = new Reg("$sp", spOff, value);
        mem.put(name, reg);
    }
    public void addSpArray(String name, int offset, Value value){
        if (mem.containsKey(name)) {
            return;
        }
        addSp(name, value);
        spOff -= offset;
        String string = "addu $t0, $sp, " + spOff + "\n";
        s.append(string);
        store("$t0", name);
    }
    public void load(String reg,String name,int offset){
        String string = "lw " + reg + ", " + offset + "(" + name + ")\n";
        s.append(string);
    }
    public void load(String reg,String name){
        String tmpString;
        String loadType = "lw ";

        if(name.matches("-?\\d+(\\.\\d+)?")){
            tmpString = "li " + reg +", "+name+"\n";
            s.append(tmpString);
        } else if (mem.get(name).value instanceof GlobalVar globalVar) {
            tmpString = "la " + reg + ", " + name + "\n";
            s.append(tmpString);
            if(globalVar.isInt()){
                tmpString = loadType + reg + ", 0(" + reg + ")\n";
                s.append(tmpString);
            }
        }else {
            tmpString = loadType + reg + ", " + mem.get(name).offset + "(" + mem.get(name).reg + ")\n";
            s.append(tmpString);
        }
    }

    public void loadBinary(String reg,String name){
        String tmpString;
        String loadType = "lb ";

        if(name.matches("-?\\d+(\\.\\d+)?")){
            tmpString = "li " + reg +", "+name+"\n";
            s.append(tmpString);
        } else if (mem.get(name).value instanceof GlobalVar globalVar) {
            tmpString = "la " + reg + ", " + name + "\n";
            s.append(tmpString);
            if(globalVar.isInt()){
                tmpString = loadType + reg + ", 0(" + reg + ")\n";
                s.append(tmpString);
            }
        }else {
            tmpString = loadType + reg + ", " + mem.get(name).offset + "(" + mem.get(name).reg + ")\n";
            s.append(tmpString);
        }
    }


    void store(String reg, String name, int offset) {
        String string = "sw " + reg + ", " + offset + "(" + name + ")\n";
        s.append(string);
    }

    public void store(String reg, String name) {
        String tmpString;
        if(mem.get(name).value instanceof GlobalVar globalVar){
            tmpString = "la $t1, " + name + "\n";
            s.append(tmpString);
            if(globalVar.isInt()){
                tmpString = "sw " + reg + ", 0($t1)\n";
            }
            s.append(tmpString);
        }else {
            tmpString = "sw " + reg + ", " + mem.get(name).offset + "(" + mem.get(name).reg + ")\n";
            s.append(tmpString);
        }
    }



    public void translate(Instruction instruction){
        if (instruction instanceof BinaryInst) {
            translateBinary(instruction);
        } else if (instruction instanceof CallInst) {
            translateCall(instruction);
        } else if (instruction instanceof RetInst) {
            translateRet(instruction);
        } else if (instruction instanceof AllocaInst) {
            translateAlloc(instruction);
        } else if (instruction instanceof LoadInst) {
            translateLoad(instruction);
        } else if (instruction instanceof StoreInst) {
            translateStore(instruction);
        } else if (instruction instanceof GEPInst) {
            translateGEP(instruction);
        } else if (instruction instanceof BrInst) {
            translateBr(instruction);
        } else if (instruction instanceof ConvInst) {
            translateConv(instruction);
        }
    }

    private void translateBinary(Instruction instruction) {
        BinaryInst binaryInst = (BinaryInst) instruction;
        if(binaryInst.isAdd()){
            calculate(binaryInst,"addu",CalculateType.Replace_t0);
        } else if(binaryInst.isSub()){
            calculate(binaryInst,"subu",CalculateType.Replace_t1);
        } else if(binaryInst.isMul()){
            calculate(binaryInst,"mul",CalculateType.Replace_t0);
        } else if(binaryInst.isDiv()){
            calculate(binaryInst,"div",CalculateType.Replace_t1);
        } else if (binaryInst.isMod()) {
            calculate(binaryInst,"rem",CalculateType.Replace_t1);
        } else if (binaryInst.isAnd()) {
            calculate(binaryInst,"and",CalculateType.Replace_t0);
        } else if (binaryInst.isOr()) {
            calculate(binaryInst,"or",CalculateType.Replace_t0);
        } else if (binaryInst.isLe()) {
            calculate(binaryInst,"sle",CalculateType.Replace_t1);
        } else if (binaryInst.isLt()) {
            calculate(binaryInst,"slt",CalculateType.No_Replace);
        } else if (binaryInst.isGe()) {
            calculate(binaryInst,"sge",CalculateType.Replace_t1);
        } else if (binaryInst.isGt()) {
            calculate(binaryInst,"sgt",CalculateType.No_Replace);
        } else if (binaryInst.isEq()) {
            calculate(binaryInst,"seq",CalculateType.Replace_t0);
        } else if (binaryInst.isNe()) {
            calculate(binaryInst,"sne",CalculateType.Replace_t0);
        } else if (binaryInst.isNot()) {
            load("$t0", binaryInst.getOperand(0).getNameId());
            s.append("not $t0, $t0\n");
            store("$t0", binaryInst.getNameId());
        }
    }

    private void calculate(BinaryInst binaryInst,String op,CalculateType calculateType){
        if(calculateType == CalculateType.Replace_t0&&binaryInst.getOperand(0) instanceof ConstInt){
            load("$t0", binaryInst.getOperand(1).getNameId());
            s.append(op).append(" $t0, $t0, ").append(((ConstInt) binaryInst.getOperand(0)).getValue()).append("\n");
            store("$t0", binaryInst.getNameId());
        } else if(calculateType == CalculateType.Replace_t1&&binaryInst.getOperand(1) instanceof ConstInt){
            load("$t0", binaryInst.getOperand(0).getNameId());
            s.append(op).append(" $t0, $t0, ").append(((ConstInt) binaryInst.getOperand(1)).getValue()).append("\n");
            store("$t0", binaryInst.getNameId());
        } else {
            load("$t0", binaryInst.getOperand(0).getNameId());
            load("$t1", binaryInst.getOperand(1).getNameId());
            s.append(op).append(" $t0, $t0, $t1\n");
            store("$t0", binaryInst.getNameId());
        }
    }
    private void translateCall(Instruction instruction){
        CallInst callInst = (CallInst) instruction;
        Function function = callInst.getCalledFunction();
        if(function.isLibraryFunction()){
            if ("getint".equals(function.getName())){
                s.append("GETINT()\n");
                store("$v0", callInst.getNameId());
            }else if ("getchar".equals(function.getName())){
                s.append("GETCHAR()\n");
                store("$v0", callInst.getNameId());
            } else if ("putint".equals(function.getName())) {
                load("$a0",callInst.getOperand(1).getNameId());
                s.append("PUTINT()\n");
            } else if ("putch".equals(function.getName())) {
                loadBinary("$a0",callInst.getOperand(1).getNameId());

                s.append("PUTCH()\n");
            } else if ("putstr".equals(function.getName())) {
                s.append("PUTSTR()\n");
            } else {
                System.out.println("translateCall Error: No such library function");
            }
        } else {
            store("$ra", "$sp", spOff - 4);
            rec = 1;
            int argSize = callInst.getCalledFunction().getArguments().size();
            for(int i=1; i<=argSize; i++){
                rec++;
                load("$t0",callInst.getOperand(i).getNameId());
                store("$t0","$sp",spOff - rec * 4);
            }
            String tmpS = "addu $sp, $sp, " + (spOff - rec * 4) + "\n";
            s.append(tmpS);
            tmpS = "jal " + function.getName() + "\n";
            s.append(tmpS);
            tmpS = "addu $sp, $sp, " + (-spOff + rec * 4) + "\n";
            s.append(tmpS);
            load("$ra", "$sp", spOff - 4);

            if(!(((FunctionType)function.getType()).getReturnType() instanceof VoidType)){
                store("$v0",callInst.getNameId());
            }
        }
    }
    private void translateRet(Instruction instruction){
        RetInst retInst = (RetInst) instruction;
        if(!(retInst.getType() instanceof VoidType)){
            load("$v0", retInst.getOperand(0).getNameId());
        }
        s.append("jr $ra\n");
    }
    private void translateAlloc(Instruction instruction){
        AllocaInst allocaInst = (AllocaInst) instruction;
        if(allocaInst.getAllocaType() instanceof PointerType pointerType){
            if (pointerType.getTargetType() instanceof IntegerType) {
                addSp(allocaInst.getNameId(), allocaInst);
            } else if (pointerType.getTargetType() instanceof ArrayType arrayType) {
                addSpArray(allocaInst.getNameId(), 4 * arrayType.getCapacity(), allocaInst);
            }
        } else if(allocaInst.getAllocaType() instanceof IntegerType){
            addSp(allocaInst.getNameId(), allocaInst);
        } else if (allocaInst.getAllocaType() instanceof ArrayType arrayType) {
            addSpArray(allocaInst.getNameId(),4 * arrayType.getCapacity(), allocaInst);
        }
    }
    private void translateLoad(Instruction instruction){
        LoadInst loadInst = (LoadInst) instruction;

        if (loadInst.getOperand(0) instanceof GEPInst) {
            load("$t0", loadInst.getOperand(0).getNameId());
            load("$t1", "$t0", 0);
            store("$t1", loadInst.getNameId());
        } else {
            load("$t0", loadInst.getOperand(0).getNameId());
            store("$t0", loadInst.getNameId());
        }
    }
    private void translateStore(Instruction instruction){
        StoreInst storeInst = (StoreInst) instruction;
        if(storeInst.getOperand(1) instanceof GEPInst){
            load("$t0",storeInst.getOperand(0).getNameId());
            load("$t1",storeInst.getOperand(1).getNameId());
            store("$t0","$t1",0);
        }else {
            load("$t0",storeInst.getOperand(0).getNameId());
            store("$t0",storeInst.getOperand(1).getNameId());
        }
    }
    private void translateGEP(Instruction instruction){
        GEPInst gepInst = (GEPInst) instruction;
        PointerType pointerType = (PointerType) gepInst.getPointer().getType();
        String tmpS;
        int offsetNum;
        int len = 0;
        if(pointerType.isString() && gepInst.getTarget() instanceof GlobalVar globalVar){
            if(globalVar.getValue() instanceof ConstString){
                tmpS = "la $a0, " + gepInst.getPointer().getGlobalName() + "\n";
                s.append(tmpS);
                return;
            }

        }
        if (pointerType.getTargetType() instanceof ArrayType) {
            offsetNum = gepInst.getOperandList().size() - 1;
            len = ((ArrayType) pointerType.getTargetType()).getLength();
        } else {
            offsetNum = 1;
        }
        load("$t2", gepInst.getPointer().getNameId());
        int lastOffset = 0;
        for(int i=1;i<=offsetNum;i++){
            int base = 4;
            if(pointerType.getTargetType() instanceof ArrayType){
                for (int j = i - 1; j < 1; j++) {
                    base *= len;
                }
            }
            if (gepInst.getOperand(i).isNumber()) {
                int dimOffset = gepInst.getOperand(i).getNumber() * base;
                lastOffset += dimOffset;
                if (i == offsetNum) {
                    if (lastOffset != 0) {
                        s.append("addu $t2, $t2, ");
                        s.append(lastOffset);
                        s.append("\n");
                    }
                    store("$t2", gepInst.getNameId());
                }
            } else {
                if (lastOffset != 0) {
                    s.append("addu $t2, $t2, ");
                    s.append(lastOffset);
                    s.append("\n");
                }
                load("$t0", gepInst.getOperand(i).getNameId()); // offset

                load("$t0", gepInst.getOperand(i).getNameId());
                s.append("mul").append(" $t0, $t0, ").append(base).append("\n");
                store("$t0", gepInst.getNameId());

                s.append("addu $t2, $t2, $t0\n");
                store("$t2", gepInst.getNameId());
            }

            s.append("\n");
        }
    }


    private void translateBr(Instruction instruction){
        BrInst brInst = (BrInst) instruction;
        String tmpS;
        if (brInst.isCondBr()) {
            load("$t0", brInst.getCond().getNameId());
            tmpS = "beqz $t0, " + brInst.getFalseLabel().getLabelName() + "\n";
            s.append(tmpS);
            tmpS = "j " + brInst.getTrueLabel().getLabelName() + "\n";
            s.append(tmpS);
        } else {
            tmpS = "j " + brInst.getTrueLabel().getLabelName() + "\n";
            s.append(tmpS);
        }
    }
    private void translateConv(Instruction instruction){
        ConvInst convInst = (ConvInst) instruction;
        if (convInst.getOperator() == Operator.Zext) {
            load("$t0", convInst.getOperand(0).getNameId());
            store("$t0", convInst.getNameId());
        } else if (convInst.getOperator() == Operator.Trunc) {
            loadBinary("$t0", convInst.getOperand(0).getNameId());
            store("$t0", convInst.getNameId());
        }
    }

    enum CalculateType{
        Replace_t0,
        Replace_t1,
        No_Replace
    }
}
