package main.test;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

public class TestAny {
    public static void main(String[] args) {
        String okFilePath = "E:\\gexf\\abasdf\\dd.txt";
        String errorFilePath = "E:\\abcdefg\\aaa.txt";
        File hasProFile = new File(okFilePath);
        File errorProFile = new File(errorFilePath);

        if(!hasProFile.exists()){
            try {
                File file = new File(okFilePath);
                FileUtils.touch(file);
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }
}
