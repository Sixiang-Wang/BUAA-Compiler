import Backend.MipsBuilder;
import Block.CompUnitBlock;
import Frontend.Lexer;
import Error.*;


import Middle.IRModule;
import Middle.IrBuilder;
import Tool.*;
import Frontend.Parser;
import Frontend.Visitor;

public class Compiler {
    private static final boolean generateMips = true;

    public static void main(String[] args) {
        ErrorHandler.resetFile();
        String input = FileController.readFileToString("testfile.txt");
        System.out.println(input);

        //Lexer
        Lexer lexer = new Lexer(input);
        lexer.analyze();

        //Parser
        Parser parser = Parser.getInstance();
        parser.initialize(lexer.getTokenList());
        parser.analyze();
        CompUnitBlock compUnitBlock = parser.getCompUnitBlock();
        compUnitBlock.print();

        //Visitor
        System.out.println("\n=====Symbol Table=====\n");
        Visitor visitor = Visitor.getInstance();
        visitor.compUnit(compUnitBlock);
        visitor.print();

        //Error Handler
        ErrorHandler.sort();
        ErrorHandler.writeToFile();

        if(ErrorHandler.errList.isEmpty()){
            //LLVM IR
            System.out.println("\n=====LLVM IR=====\n");
            IrBuilder irBuilder = new IrBuilder(compUnitBlock);
            irBuilder.visitCompUnit(irBuilder.compUnitBlock);
            IRModule irModule = IRModule.getInstance();
            irModule.refreshRegNumber();
            irModule.println();
            irModule.filePrintln();

            //Mips
            if(generateMips){
                System.out.println("\n=====MIPS=====\n");
                MipsBuilder mipsBuilder = new MipsBuilder(irModule);
                mipsBuilder.buildMips();

                mipsBuilder.filePrintln();
            }
        }

        FileController.closeAllFile();
    }
}
