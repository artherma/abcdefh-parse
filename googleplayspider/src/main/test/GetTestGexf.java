package main.test;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

/**
 * 获取需要的gexf 数据
 * 筛选出来的数据 stats_code =200 and has_gexf = 1 and has_xml = 1 and xml_file_size > 0
 */
public class GetTestGexf {

    // 按要求获取数据
    public static void getFileNameWithOrder(){

    }

    public static void main(String[] args) {
        String filePath = "E:\\parse\\need.txt";
        File file = new File(filePath);
        if(file.exists() && file.isFile()){
            try {
                Scanner scanner = new Scanner(file);
                while (scanner.hasNext()){
                    String line = scanner.nextLine();
                    if(line != null && !line.isEmpty()){
                        String[] results = line.split("\\s+");
                        if(results.length > 1){
                            System.out.println("name:\t\t"+results[0]+"dir:\t\t" + results[1]);
//                            System.out.println(results[1]);
                        }
                    }
                }
            }catch (IOException e){
                e.printStackTrace();
            }

        }else {

        }
    }
}
