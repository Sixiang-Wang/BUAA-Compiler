package Middle.Values.instructions;

public enum Operator {
    Add, Sub, Mul, Div, Mod, Shl, Shr, And, Or, // 二元运算符
    Lt, Le, Ge, Gt, Eq, Ne, // 关系运算符
    Zext, Trunc,// 类型转换
    Alloca, Load, Store, GEP, // 内存操作
    Br, Call, Ret, // 跳转指令
    Not // 非运算符
}
