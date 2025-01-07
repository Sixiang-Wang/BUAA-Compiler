package Block;

public class DeclBlock {
    public ConstDeclBlock constDeclBlock;
    public VarDeclBlock varDeclBlock;

    public DeclBlock(ConstDeclBlock constDeclBlock,VarDeclBlock varDeclBlock){
        this.constDeclBlock = constDeclBlock;
        this.varDeclBlock = varDeclBlock;
    }

    public void print(){
        if(constDeclBlock!=null){
            constDeclBlock.print();
        } else {
            varDeclBlock.print();
        }
    }
}
