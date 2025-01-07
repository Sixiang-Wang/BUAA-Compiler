package Middle.Values;

import Middle.IRModule;
import Middle.Types.FunctionType;
import Middle.Types.Type;

import java.util.*;

public class Function extends Value {
    public LinkedList<BasicBlock> basicBlockList = new LinkedList<>();
    public IRModule parentModule;

    private final List<Argument> arguments;

    private final boolean isLibraryFunction;


    public Function(String name, Type type, boolean isLibraryFunction) {
        super(name, type);
        REG_NUMBER = 0;
        this.arguments = new ArrayList<>();

        this.isLibraryFunction = isLibraryFunction;
        for (Type t : ((FunctionType) type).getParametersType()) {
            arguments.add(new Argument(t, ((FunctionType) type).getParametersType().indexOf(t), isLibraryFunction));
        }

        this.parentModule = IRModule.getInstance();
        this.parentModule.functionList.addLast(this);
    }

    public List<Value> getArguments() {
        return new ArrayList<>(arguments);
    }

    public boolean isLibraryFunction(){return isLibraryFunction;}
    public void refreshArgReg() {
        for (Argument arg : arguments) {
            arg.setName("%" + REG_NUMBER++);
        }
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append(((FunctionType) this.getType()).getReturnType()).append(" @").append(this.getName()).append("(");
        for (int i = 0; i < arguments.size(); i++) {
            s.append(arguments.get(i).getType());
            if (i != arguments.size() - 1) {
                s.append(", ");
            }
        }
        s.append(")");
        return s.toString();
    }

    public static class Argument extends Value {
        public int index;

        public Argument(Type type, int index, boolean isLibraryFunction) {
            super(isLibraryFunction ? "" : "%" + REG_NUMBER++, type);
            this.index = index;
        }

        @Override
        public String toString() {
            return this.getType().toString() + " " + this.getName();
        }
    }
}
