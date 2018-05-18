package main.parse;

import main.util.ContenUtils;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Scanner;

/**
 * 存储apk的 gexf文件，
 * 需要检索 apk_store_info表，然后存储到method 表和 invoke表
 * 如果xml文件大小为0，或者xml文件内不能获取到一些信息，则不处理，标注到单独的文本
 * 正常情况，处理，插入数据库
 */
public class StoreApkGexf {

    private static final String gexfToSqlLog = "E:\\gexf\\proSql\\method_insert_f8.log";    //正常日志
    private static final String HAS_PROCESS = "E:\\gexf\\proSql\\has_pro_f8.txt";   // 已处理的apk
    private static final String ERROR_PROCESS = "E:\\gexf\\proSql\\error_pro_f8.txt"; // 出错的apk
    public static final String error_log = "E:\\gexf\\proSql\\method_insert_f8_error.log"; // 未处理的错误日志
    private static final String error_xml = "E:\\gexf\\proSql\\method_insert_f8_xmlError.log"; //xml文件错误日志，例如不存在xml文件或者文件过小（10字节）
    private static final String error_gexf = "E:\\gexf\\proSql\\method_insert_f8_gexfError.log"; //gexf文件错误日志，例如不存在gexf文件

    public static void main(String[] args) {
        String apkFilePath = "I:\\workfile\\playdrone-apk-f8";  //原始的apk目录
        String gexfPath = "E:\\gexf\\apk-f8"; //apk对应的gexf目录
        String xmlPath = "E:\\gexf\\xml\\f8";   //apk对应的xml目录
        String apkDir = "f8";   //手动设置的f8/f9/fa

        proGexf(apkFilePath,gexfPath,xmlPath,apkDir);

    }


    private static void proGexf(String apkFilePath,String gexfPath,String xmlPath,String apkRelDir) {
        File apkDir = new File(apkFilePath);
        File gexfDir = new File(gexfPath);
        File xmlDir = new File(xmlPath);
        File errorLogFile = new File(error_log);
        File gexfToSql = new File(gexfToSqlLog);
        File hasProFile = new File(HAS_PROCESS);
        File errorProFile = new File(ERROR_PROCESS);


        if (apkDir.exists() && gexfDir.exists() && xmlDir.exists() &&
                apkDir.isDirectory() && gexfDir.isDirectory() && xmlDir.isDirectory()) {
            System.out.println("apk,gexf,xml目录都存在");

            Connection connection = SaveToMysql.getDefaultConn();
            try {
                if(connection != null && !connection.isClosed()){
                    connection.setAutoCommit(false);
                }else {
                    return;
                }
            }catch (SQLException e){
                e.printStackTrace();
                commonErrorLog(errorLogFile,e.getMessage()+"\n");
            }

            File[] apkFiles = apkDir.listFiles();
            // 获取是否处理的hashmap
            HashMap<String,String> abcdMap = preCheckFile(HAS_PROCESS,ERROR_PROCESS);
            int counter  =0;
            for(File tmpFile: apkFiles){
                counter++;
                try {
    //                预处理，检查
                    String tmpName = tmpFile.getName();
                    if(abcdMap != null && abcdMap.containsKey(tmpName)){
                        String tmpValue = abcdMap.get(tmpName);
                        if(tmpValue.equals("ok")){
                            System.out.println("ok continue["+tmpName+"]");
                            continue;
                        }else if(tmpValue.equals("error")){
                            System.out.println("error continue["+tmpName+"]");
                            continue;
                        }
                    }

                    String data = "开始时间["+ContenUtils.getNowTime()+"],处理apk["+tmpFile.getName()+"]的gexf文件；";
                    commonErrorLog(gexfToSql,data);

                    boolean isOk = getOneApkMethod(tmpFile.getAbsolutePath(),gexfPath,xmlPath,errorLogFile,apkRelDir,connection);

                    if(isOk){
                        data= "结束时间["+ContenUtils.getNowTime()+"],处理结束["+tmpFile.getName()+"],counter="+counter+"\n";
                        commonErrorLog(gexfToSql,data);
                        commonErrorLog(hasProFile,tmpFile.getName()+"\n");
                    }else {
                        commonErrorLog(errorProFile,tmpFile.getName()+"\n");
                    }
                }catch (Exception e){
                    e.printStackTrace();
                    commonErrorLog(errorLogFile,e.getMessage()+"\n");
                }
            }
            // 关闭连接
            try {
                if(connection != null && !connection.isClosed()){
                    connection.close();
                    System.out.println("数据库连接关闭");
                }else {
                    System.out.println("数据库连接为null或已关闭");
                }
            }catch (SQLException e){
                System.out.println("数据库关闭失败");
                e.printStackTrace();
            }

        } else {
            System.out.println("目录可能出错，或者文件、目录不存在");
        }
    }

    // 处理单独的apk信息，返回true or false
    private static boolean getOneApkMethod(String filePath,String gexfDir,String xmlDir,File errorLog,String apkDir,Connection connection){
        File errorXmlFile = new File(error_xml);
        File errorGexfFile = new File(error_gexf);

//        Connection connection = SaveToMysql.getDefaultConn();
//        try {
//            if(connection != null && !connection.isClosed()){
//                connection.setAutoCommit(false);
//            }else {
//                return;
//            }
//        }catch (SQLException e){
//            e.printStackTrace();
//        }


        File apkFile = new File(filePath);
        // 带.apk后缀的
        String fileNameWithSuffix = apkFile.getName();
        // 不带.apk后缀
        String fileNameWithOutDotApk = fileNameWithSuffix.substring(0, fileNameWithSuffix.length() - 4);
        if (fileNameWithSuffix.matches("(.*)-(\\d+)\\.apk$")) {
            // 包名 com.tencent.qq
            String packageName = ContenUtils.getRegContent(fileNameWithSuffix, "(.*)-(\\d+)\\.apk$", 1);
            String tmpGexfPath = gexfDir + File.separator + fileNameWithOutDotApk + ".gexf";
            String tmpXmlPath = xmlDir + File.separator + fileNameWithOutDotApk + ".xml";

            File tmpGexfFile = new File(tmpGexfPath);
            File tmpXmlFile = new File(tmpXmlPath);

//          同时存在,且xml的文件大小大于10个字节，@TODO 有的xml文件内容为空
            if(tmpGexfFile.exists() && tmpXmlFile.exists() && tmpXmlFile.length() > 10){
//                @TODO 解析xml和gexf文件
                HashMap<String,String> manifestXmlMap = ParseXmlFile.parseManifestXml(tmpXmlFile.getAbsolutePath());
                if(manifestXmlMap != null){
                    String version = manifestXmlMap.get("version");
                    String packagename = manifestXmlMap.get("packagename");
                    // @TODO 用到了数据库连接
                    HashMap<String,String> storeInfoMap = selectFromApkstoreInfo(connection,fileNameWithSuffix,apkDir);
                    String recordId = storeInfoMap.get("id");
                    if(version.isEmpty()){
                        String data = "apk文件["+fileNameWithSuffix+"]，的manifest.xml中version为空，请注意\n";
                        commonErrorLog(errorLog,data);
                    }

                    try {
                        // @TODO 用到了数据库连接
                        ParseXmlFile.parseXmlGexf(tmpGexfPath,connection,version,fileNameWithSuffix,recordId);
                        return true;

                    }catch (SQLException e){
                        e.printStackTrace();
                    // @TODO 异常日志记录
                        String data = "apk文件["+fileNameWithSuffix+"]，gexf插入数据库时出现问题，报错为["+e.getMessage()+"]\n";
                        System.out.println(data);
                        commonErrorLog(errorLog,data);
                    }
                }else {
                    // @TODO 异常日志记录
                    String data = "apk文件["+fileNameWithSuffix+"]，manifest.xml文件返回值为null\n";
                    System.out.println(data);
                    commonErrorLog(errorLog,data);
                }
                return false;
            }else {
                // @TODO 异常日志记录
                String data = "apk文件["+fileNameWithSuffix+"]，没有对应的gexf和xml，或者xml文件大小为0\n";
                System.out.println(data);
                commonErrorLog(errorLog,data);
                // 分开记录
                if(!tmpGexfFile.exists()){
                    String log="apk文件["+fileNameWithSuffix+"]，gexf文件["+tmpGexfFile.getName()+"]不存在\n";
                    commonErrorLog(errorGexfFile,log);
                }else if(!tmpXmlFile.exists()){
                    String log="apk文件["+fileNameWithSuffix+"]，manifest.xml文件["+tmpXmlFile.getName()+"]不存在\n";
                    commonErrorLog(errorXmlFile,log);
                }else if(tmpXmlFile.exists() && !(tmpXmlFile.length() > 10)){
                    String log="apk文件["+fileNameWithSuffix+"]，manifest.xml文件["+tmpXmlFile.getName()+"]大小小于10个字节\n";
                    commonErrorLog(errorXmlFile,log);
                }
            }

        }else {
            System.out.println("发现其他格式的文件名（不符合'包名-数字-.apk格式'）: "+fileNameWithSuffix);
            String data = "apk文件["+fileNameWithSuffix+"]，发现其他格式的文件名\n";
            commonErrorLog(errorLog,data);
        }

        return false;

//        // 关闭连接
//        try {
//            if(connection != null && !connection.isClosed()){
//                connection.close();
//                System.out.println("数据库连接关闭");
//            }else {
//                System.out.println("数据库连接为null或已关闭");
//            }
//        }catch (SQLException e){
//            System.out.println("数据库关闭失败");
//            e.printStackTrace();
//        }
    }

//                    追加日志
    public static void commonErrorLog(File errorLogFile,String data){
        if(data != null ){
            try {
                FileUtils.write(errorLogFile,data,"utf-8",true);
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }

    /**
     * 从数据库中查询 id
     * @param connection
     * @param apkFileName
     * @param apkDirName
     * @return
     */
    private static HashMap<String,String> selectFromApkstoreInfo(Connection connection,String apkFileName,String apkDirName){
        String sql = "select * from apk_store_info where apk_file_name = '" +
                "" +apkFileName+
                "' and apk_file_dir = '" +
                "" + apkDirName+
                "' limit 1;";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            ResultSet resultSet = preparedStatement.executeQuery();
            int columSize = resultSet.getMetaData().getColumnCount();
            HashMap<String,String> resultMap = new HashMap<String ,String>();

            if(resultSet.next()){
                resultMap.put("id",resultSet.getString(1));
            }

            resultSet.close();
            preparedStatement.close();
            return resultMap;
        }catch (SQLException e){
            e.printStackTrace();
            return null;
        }
    }

    // 错误恢复
    private static HashMap<String,String>  preCheckFile(String okFilePath,String errorFilePath){
        File hasProFile = new File(okFilePath);
        File errorProFile = new File(errorFilePath);

        if(!hasProFile.exists()){
            try {
                FileUtils.touch(hasProFile);
                FileUtils.touch(errorProFile);
            }catch (IOException e){
                e.printStackTrace();
                System.out.println("新建文件失败");
            }

        }

        if(hasProFile.exists() && errorProFile.exists()){
            HashMap<String,String> checkMap = new HashMap<String, String>();
            try {
                Scanner scanner = new Scanner(hasProFile);
                while (scanner.hasNext()){
                    String tmpStr = scanner.nextLine();
                    if(!tmpStr.isEmpty()){
                        checkMap.put(tmpStr,"ok");
                    }
                }

                scanner = new Scanner(errorProFile);
                while (scanner.hasNext()){
                    String tmpStr = scanner.nextLine();
                    if(!tmpStr.isEmpty()){
                        checkMap.put(tmpStr,"error");
                    }
                }

                scanner.close();
            }catch (FileNotFoundException e){
                e.printStackTrace();
            }
            return checkMap;
        }else {
            return null;
        }

    }
}
