package Middle.Values;

import Middle.Types.*;
import Middle.Values.instructions.BinaryInst;
import Middle.Values.instructions.ConvInst;
import Middle.Values.instructions.Operator;
import Middle.Values.instructions.mem.*;
import Middle.Values.instructions.terminator.BrInst;
import Middle.Values.instructions.terminator.CallInst;
import Middle.Values.instructions.terminator.RetInst;

import java.util.ArrayList;
import java.util.List;

public class BuildFactory {

    private static final BuildFactory buildFactory = new BuildFactory();

    public BuildFactory() {
    }

    public static BuildFactory getInstance() {
        return buildFactory;
    }

    /**
     * Functions
     **/
    public Function buildFunction(String name, Type ret, List<Type> parametersTypes) {
        return new Function(name, getFunctionType(ret, parametersTypes), false);
    }

    public Function buildLibraryFunction(String name, Type ret, List<Type> parametersTypes) {
        return new Function(name, getFunctionType(ret, parametersTypes), true);
    }

    public FunctionType getFunctionType(Type retType, List<Type> parametersTypes) {
        return new FunctionType(retType, parametersTypes);
    }

    public List<Value> getFunctionArguments(Function function) {
        return function.getArguments();
    }

    /**
     * BasicBlock
     */
    public BasicBlock buildBasicBlock(Function function) {
        return new BasicBlock(function);
    }

    public void checkFuncRet(BasicBlock basicBlock) {
        Type retType = ((FunctionType) basicBlock.parentFunc.getType()).getReturnType();
        if (!basicBlock.instructionList.isEmpty()) {
            Value lastInst = basicBlock.instructionList.getLast();
            if (lastInst instanceof RetInst || lastInst instanceof BrInst) {
                return;
            }
        }
        if (retType instanceof IntegerType) {
            buildRet(basicBlock, ConstInt.ZERO);
        } else {
            buildRet(basicBlock);
        }
    }

    /**
     * BinaryInst
     **/
    public BinaryInst buildBinary(BasicBlock basicBlock, Operator op, Value left, Value right) {
        BinaryInst tmp = new BinaryInst(basicBlock, op, left, right);
        if (op == Operator.And || op == Operator.Or) {
            tmp = buildBinary(basicBlock, Operator.Ne, tmp, ConstInt.ZERO);
        }
        tmp.addInstToBlock(basicBlock);
        return tmp;
    }

    public BinaryInst buildNot(BasicBlock basicBlock, Value value) {
        return buildBinary(basicBlock, Operator.Eq, value, ConstInt.ZERO);
    }

    /**
     * Var
     */
    public GlobalVar buildGlobalVar(String name, Type type, boolean isConst, Value value) {
        return new GlobalVar(name, type, isConst, value);
    }

    public AllocaInst buildVar(BasicBlock basicBlock, Value value, boolean isConst, Type allocaType) {
        AllocaInst tmp = new AllocaInst(basicBlock, isConst, allocaType);
        tmp.addInstToBlock(basicBlock);
        if (value != null) {
            buildStore(basicBlock, tmp, value);
        }
        return tmp;
    }

    public ConstInt getConstInt(int value) {
        return new ConstInt(value);
    }
    public ConstInt getConstInt(int value,Type type) {
        return new ConstInt(value,type);
    }

    public ConstString getConstString(String value) {
        return new ConstString(value);
    }

    /**
     * Array
     */
    public GlobalVar buildGlobalArray(String name, Type type, boolean isConst) {
        Value tmp = new ConstArray(type, ((ArrayType) type).getElementType());
        return new GlobalVar(name, type, isConst, tmp);
    }

    public AllocaInst buildArray(BasicBlock basicBlock, boolean isConst, Type arrayType) {
        AllocaInst tmp = new AllocaInst(basicBlock, isConst, arrayType);
        tmp.addInstToBlock(basicBlock);
        return tmp;
    }

    public void buildInitArray(Value array, int index, Value value) {
        ((ConstArray) ((GlobalVar) array).getValue()).storeValue(index, value);
    }

    public ArrayType getArrayType(Type elementType, int length) {
        return new ArrayType(elementType, length);
    }

    /**
     * ConvInst
     */
    public Value buildZext(Value value, BasicBlock basicBlock) {
        if (value instanceof ConstInt) {
            return new ConstInt(((ConstInt) value).getValue(),IntegerType.i32);
        }
        ConvInst tmp;
        tmp = new ConvInst(basicBlock, Operator.Zext, value);
        tmp.addInstToBlock(basicBlock);
        return tmp;
    }
    public Value buildTrunc(Value value,BasicBlock basicBlock){
        if (value instanceof ConstInt) {
            return new ConstInt(((ConstInt) value).getValue(),IntegerType.i8);
        }
        ConvInst tmp;
        tmp = new ConvInst(basicBlock,Operator.Trunc,value);
        tmp.addInstToBlock(basicBlock);
        return tmp;
    }

    public BinaryInst buildConvToI1(Value val, BasicBlock basicBlock) {
        BinaryInst tmp = new BinaryInst(basicBlock, Operator.Ne, val, getConstInt(0));
        tmp.addInstToBlock(basicBlock);
        return tmp;
    }

    /**
     * MemInst
     */
    public LoadInst buildLoad(BasicBlock basicBlock, Value pointer) {
        LoadInst tmp = new LoadInst(basicBlock, pointer);
        tmp.addInstToBlock(basicBlock);
        return tmp;
    }

    public StoreInst buildStore(BasicBlock basicBlock, Value ptr, Value value) {
        if(((PointerType)ptr.getType()).getTargetType()!=value.getType()){
            if(value.getType()==IntegerType.i32){
                value = buildTrunc(value,basicBlock);
            } else {
                value = buildZext(value,basicBlock);
            }
        }
        StoreInst tmp = new StoreInst(basicBlock, ptr, value);
        tmp.addInstToBlock(basicBlock);
        return tmp;
    }

    public GEPInst buildGEP(BasicBlock basicBlock, Value pointer, List<Value> indices) {
        GEPInst tmp = new GEPInst(basicBlock, pointer, indices);
        tmp.addInstToBlock(basicBlock);
        return tmp;
    }

    public GEPInst buildGEP(BasicBlock basicBlock, Value pointer, int offset) {
        GEPInst tmp = new GEPInst(basicBlock, pointer, offset);
        tmp.addInstToBlock(basicBlock);
        return tmp;
    }


    /**
     * TerminatorInst
     */
    public void buildBr(BasicBlock basicBlock, BasicBlock trueBlock) {
        BrInst tmp = new BrInst(basicBlock, trueBlock);
        tmp.addInstToBlock(basicBlock);
    }

    public void buildBr(BasicBlock basicBlock, Value cond, BasicBlock trueBlock, BasicBlock falseBlock) {
        BrInst tmp = new BrInst(basicBlock, cond, trueBlock, falseBlock);
        tmp.addInstToBlock(basicBlock);
    }

    public CallInst buildCall(BasicBlock basicBlock, Function function, List<Value> args) {
        int index = 0;
        List<Value> newArgs = new ArrayList<>();
        //转类型
        //如果是putStr类型是指针，所以要排除
        if(function.getArguments()!=null&&
                !function.getArguments().isEmpty())
        {
            for(Value param:function.getArguments()){
                if(param.getType() instanceof IntegerType paramType){
                    Value arg = args.get(index);
                    IntegerType argType = (IntegerType) arg.getType();
                    if(paramType.isI8()&&argType.isI32()){
                        newArgs.add(buildTrunc(arg,basicBlock));
                    } else if (paramType.isI32()&&argType.isI8()) {
                        newArgs.add(buildZext(arg,basicBlock));
                    } else {
                        newArgs.add(arg);
                    }
                }else {
                    newArgs.add(args.get(index));
                }
                //TODO: 数组变量转换？不需要在这里写哈哈
                index++;
            }
        }else {
            newArgs = args;
        }
        CallInst tmp = new CallInst(basicBlock, function, newArgs);
        tmp.addInstToBlock(basicBlock);
        return tmp;
    }

    public void buildRet(BasicBlock basicBlock) {
        RetInst tmp = new RetInst(basicBlock);
        tmp.addInstToBlock(basicBlock);
    }

    public void buildRet(BasicBlock basicBlock, Value ret) {
        Value retValue = ret;
        IntegerType retType = (IntegerType) ((FunctionType)basicBlock.parentFunc.getType()).getReturnType();
        if(retType.isI32()&&((IntegerType)ret.getType()).isI8()){
            retValue = buildZext(ret,basicBlock);
        } else if (retType.isI8()&&((IntegerType)ret.getType()).isI32()) {
            retValue = buildTrunc(ret,basicBlock);
        }
        RetInst tmp = new RetInst(basicBlock, retValue);
        tmp.addInstToBlock(basicBlock);
    }

}
