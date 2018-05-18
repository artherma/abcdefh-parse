package main.test;

import main.util.ContenUtils;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

/**
 * 提取包名
 */
//com.blank.mirame_demo-1000005.apk
//        com.jb.bdbook.chdija-55.apk
//        com.serviceticketonline.FireExtinguisher-5.apk
//        com.aisyah.informasipenyakit-5.apk
public class GetPackageName {
    private static final String fileName = "J:\\playdrone-apk-fa-fileName.txt";
    private static final String resFileName = "E:\\gexf\\playdrone-apk-fa-packageName.txt";
    public static void main(String[] args) throws FileNotFoundException,IOException{
        File file = new File(fileName);
        File newFile = new File(resFileName);
        Scanner scanner = new Scanner(file);
        int acount = 0;
        StringBuilder sb = new StringBuilder("");
        while (scanner.hasNext()){
            acount++;
            String line = scanner.nextLine();
            String res = ContenUtils.getRegContent(line,"(.*)-(\\d+)\\.apk$",1)+"\n";
            sb.append(res);
            if(acount  % 300 == 0){
                FileUtils.write(newFile,sb.toString(),"utf-8",true);
                sb=new StringBuilder("");
            }
        }
        FileUtils.write(newFile,sb.toString(),"UTF-8",true);
        scanner.close();
//        test();
    }

    private static void test(){
        String line = "com.blank.mirame_demo-1000005.apk";
        String res = ContenUtils.getRegContent(line,"(.*)-(\\d+)\\.apk$",1);
        System.out.println(res);
    }
}
