package Error;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ErrorHandler {
    public static File errFile = new File("error.txt");
    public static List<Err> errList = new ArrayList<>();
    public static void resetFile(){
            try {
                if(!errFile.exists()) {
                    errFile.createNewFile();
                }
                FileWriter writer = new FileWriter(errFile, false);
                writer.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
    }

    public static void add(Integer lineNum, ErrType type) {
        for (Err err : errList) {
            if(err.lineNum.equals(lineNum)){
                return;
            }
        }
        errList.add(new Err(lineNum, type));
    }

    public static void sort(){
        errList.sort(Comparator.comparingInt(err -> err.lineNum));
    }

    public static void writeToFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(errFile, true))) { // 使用try-with-resources自动关闭资源
            errList.forEach(err -> {
                try {
                    writer.write(err.context); // 写入字符串和换行符
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
