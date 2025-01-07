package Middle.Values.instructions;

import Middle.Types.IntegerType;
import Middle.Types.VoidType;
import Middle.Values.*;

public class BinaryInst extends Instruction {

    public BinaryInst(BasicBlock basicBlock, Operator op, Value left, Value right) {
        super(VoidType.voidType, op, basicBlock);

        boolean isLeftI1 = left.getType() instanceof IntegerType && ((IntegerType) left.getType()).isI1();
        boolean isRightI1 = right.getType() instanceof IntegerType && ((IntegerType) right.getType()).isI1();
        boolean isLeftI8 = left.getType() instanceof IntegerType && ((IntegerType) left.getType()).isI8();
        boolean isRightI8 = right.getType() instanceof IntegerType && ((IntegerType) right.getType()).isI8();

        Value leftValue = left;
        Value rightValue = right;

        if(isLeftI1||isLeftI8){
            leftValue = BuildFactory.getInstance().buildZext(left, basicBlock);
        }
        if(isRightI1||isRightI8){
            rightValue = BuildFactory.getInstance().buildZext(right, basicBlock);
        }

        addOperands(leftValue,rightValue);
        this.setType(this.getOperandList().get(0).getType());
        if (isCond()) {
            this.setType(IntegerType.i1);
        }
        this.setName("%" + REG_NUMBER++);
    }

    private void addOperands(Value left, Value right) {
        this.addOperand(left);
        this.addOperand(right);
    }



    @Override
    public String toString() {
        String s = getName() + " = ";
        switch (this.getOperator()) {
            case Add:
                s += "add i32 ";
                break;
            case Sub:
                s += "sub i32 ";
                break;
            case Mul:
                s += "mul i32 ";
                break;
            case Div:
                s += "sdiv i32 ";
                break;
            case Mod:
                s += "srem i32 ";
                break;
            case And:
                s += "and " + this.getOperandList().get(0).getType().toString() + " ";
                break;
            case Or:
                s += "or " + this.getOperandList().get(0).getType().toString() + " ";
                break;
            case Lt:
                s += "icmp slt " + this.getOperandList().get(0).getType().toString() + " ";
                break;
            case Le:
                s += "icmp sle " + this.getOperandList().get(0).getType().toString() + " ";
                break;
            case Ge:
                s += "icmp sge " + this.getOperandList().get(0).getType().toString() + " ";
                break;
            case Gt:
                s += "icmp sgt " + this.getOperandList().get(0).getType().toString() + " ";
                break;
            case Eq:
                s += "icmp eq " + this.getOperandList().get(0).getType().toString() + " ";
                break;
            case Ne:
                s += "icmp ne " + this.getOperandList().get(0).getType().toString() + " ";
                break;
            default:
                break;
        }
        return s + this.getOperandList().get(0).getName() + ", " + this.getOperandList().get(1).getName();
    }

    public boolean isAdd() {
        return this.getOperator() == Operator.Add;
    }

    public boolean isSub() {
        return this.getOperator() == Operator.Sub;
    }

    public boolean isMul() {
        return this.getOperator() == Operator.Mul;
    }

    public boolean isDiv() {
        return this.getOperator() == Operator.Div;
    }

    public boolean isMod() {
        return this.getOperator() == Operator.Mod;
    }

    public boolean isAnd() {
        return this.getOperator() == Operator.And;
    }

    public boolean isOr() {
        return this.getOperator() == Operator.Or;
    }

    public boolean isLt() {
        return this.getOperator() == Operator.Lt;
    }

    public boolean isLe() {
        return this.getOperator() == Operator.Le;
    }

    public boolean isGe() {
        return this.getOperator() == Operator.Ge;
    }

    public boolean isGt() {
        return this.getOperator() == Operator.Gt;
    }

    public boolean isEq() {
        return this.getOperator() == Operator.Eq;
    }

    public boolean isNe() {
        return this.getOperator() == Operator.Ne;
    }

    public boolean isCond() {
        return this.isLt() || this.isLe() || this.isGe() || this.isGt() || this.isEq() || this.isNe();
    }

    public boolean isNot() {
        return this.getOperator() == Operator.Not;
    }


}
