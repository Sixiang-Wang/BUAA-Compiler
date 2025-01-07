package Block;

public class BlockItemBlock {
    public DeclBlock declBlock;
    public StmtBlock stmtBlock;

    public BlockItemBlock(DeclBlock declBlock,StmtBlock stmtBlock){
        this.declBlock = declBlock;
        this.stmtBlock = stmtBlock;
    }

    public void print() {
        if (declBlock != null) {
            declBlock.print();
        } else {
            stmtBlock.print();
        }
    }
}
