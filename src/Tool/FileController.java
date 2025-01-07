package Tool;

import java.io.*;

public class FileController {
    static FileWriter fileWriter;
    static FileWriter symbolFileWriter;
    static FileWriter llvmWriter;
    static FileWriter mipsWriter;

    static {
        try {
            fileWriter = new FileWriter("parser.txt");
            symbolFileWriter = new FileWriter("symbol.txt");
            llvmWriter = new FileWriter("llvm_ir.txt");
            mipsWriter = new FileWriter("mips.txt");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    static PrintWriter printWriter = new PrintWriter(fileWriter);
    static PrintWriter symbolPrintWriter = new PrintWriter(symbolFileWriter);
    static PrintWriter llvmPrintWriter = new PrintWriter(llvmWriter);
    static PrintWriter mipsPrintWriter = new PrintWriter(mipsWriter);

    public static String readFileToString(String filePath) {
        StringBuilder content = new StringBuilder();

        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                content.append(line).append("\n");  // 每行读取后追加换行符
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return content.toString();
    }

    public static void printlnParser(String string){
        printWriter.println(string);
    }
    public static void printlnSymbol(String string){symbolPrintWriter.println(string);}
    public static void printlnLLVM(String s){
        llvmPrintWriter.println(s);
    }
    public static void printlnMIPS(String s){
        mipsPrintWriter.println(s);
    }

    public static void closeAllFile(){
        printWriter.close();
        symbolPrintWriter.close();
        llvmPrintWriter.close();
        mipsPrintWriter.close();
    }
}
