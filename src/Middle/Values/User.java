package Middle.Values;

import Middle.Types.Type;

import java.util.ArrayList;
import java.util.List;

public abstract class User extends Value {
    private final List<Value> operandList;

    public User(String name, Type type) {
        super(name, type);
        this.operandList = new ArrayList<>();
    }

    public List<Value> getOperandList() {
        return operandList;
    }

    public Value getOperand(int index) {
        return operandList.get(index);
    }

    public void addOperand(Value operand) {
        this.operandList.add(operand);
    }


}
