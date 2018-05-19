package main.parse;

import main.bean.StoreApkInfoBean;
import main.util.ContenUtils;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * 存储到数据库，表 apk_store_info
 * 相关的bean StoreApkInfoBean
 *
 * 思路：列举文件，然后再去搜索，构建一个对象，然后插入数据库
 */
public class StoreApkInfo {
    private static final String log_apk_info = "E:\\gexf\\apkInfo-f8.sql.log";

    public static void main(String[] args) {
        String apkFilePath = "G:\\workfile\\playdrone-apk-fa";
        String gexfPath = "E:\\gexf\\apk-f8";
        String xmlPath = "E:\\gexf\\xml\\fa";
        search(apkFilePath,gexfPath,xmlPath);
    }

    private static void search(String apkFilePath,String gexfPath,String xmlPath){
        File apkDir  = new File(apkFilePath);
        File gexfDir = new File(gexfPath);
        File xmlDir = new File(xmlPath);
        File errorLogFile = new File(log_apk_info);

        Connection connection = SaveToMysql.getDefaultConn();
        String sql = "insert into apk_store_info(apk_file_name,apk_file_dir,apk_package_name ,has_gexf ,has_xml ,gexf_file_size ,xml_file_size )" +
                "VALUES(?,?,?,?," +
                "?,?,?);";
        PreparedStatement statement = null;

        try {
            connection.setAutoCommit(false);
            statement = connection.prepareStatement(sql);
        }catch (SQLException e){
            e.printStackTrace();
        }

        if(apkDir.exists() && gexfDir.exists() && xmlDir.exists() && apkDir.isDirectory() && gexfDir.isDirectory() && xmlDir.isDirectory()){
            System.out.println("apk,gexf,xml目录都存在");

            File[] apkFiles = apkDir.listFiles();

            int counter = 1;
            int perBatch = 1000;
            for (File tmpFile: apkFiles){
//                if(counter > 10){
//                    break;
//                }
                StoreApkInfoBean sApkIB = getOneApkInfo(tmpFile.getAbsolutePath(),gexfPath,xmlPath);
                try {
//                    检查，是否为空，若为空，则不插入数据库
                    if(sApkIB.getApkPackageName() == null){
                        FileUtils.write(errorLogFile,tmpFile.getName()+"\n","utf-8",true);
                    }else {
                        statement.setString(1,sApkIB.getApkFileName());
                        statement.setString(2,sApkIB.getApkFileDir());
                        statement.setString(3,sApkIB.getApkPackageName());
                        statement.setString(4,sApkIB.getHasGexf());
                        statement.setString(5,sApkIB.getHasXml());
                        statement.setLong(6,sApkIB.getGexfSize());
                        statement.setLong(7,sApkIB.getXmlSize());
                        statement.addBatch();
                    }
                }catch (IOException e){
                    e.printStackTrace();
                }catch (SQLException e){
                    e.printStackTrace();
                }

                // 写数据库
                if(counter % perBatch == 0){
                    try {
                        statement.executeBatch();
                        connection.commit();
                        System.out.println("Batch executed successful");
                    }catch (SQLException e){
                        e.printStackTrace();
                    }
                }

                counter++;
            }
            // 执行最后一次
            try {
                statement.executeBatch();
                connection.commit();
                System.out.println("最后一次批量操作执行成功");
            }catch (SQLException e){
                e.printStackTrace();
            }


        }
    }

    /**
     * 获取一个apk的信息
     * @param filePath
     * @param gexfDir
     * @param xmlDir
     * @return
     */
    private static StoreApkInfoBean getOneApkInfo(String filePath,String gexfDir,String xmlDir){
        File apkFile = new File(filePath);
        if(apkFile.exists() && !apkFile.isDirectory() && filePath.endsWith(".apk")){
            StoreApkInfoBean saib = new StoreApkInfoBean();

            // 相对目录
            String apkFileDir = null;
            String hasGexf = null;
            String hasXml = null;
            long gexfSize = 0;
            long xmlSize = 0;

            if(apkFile.getParent().endsWith("f8")){
                apkFileDir = "f8";
            }else if(apkFile.getParent().endsWith("f9")){
                apkFileDir = "f9";
            }else if(apkFile.getParent().endsWith("fa")){
                apkFileDir = "fa";
            }

            // 带.apk后缀的
            String fileNameWithSuffix = apkFile.getName();
            // 不带.apk后缀
            String fileNameWithOutDotApk = fileNameWithSuffix.substring(0,fileNameWithSuffix.length() -4);
            if(fileNameWithSuffix.matches("(.*)-(\\d+)\\.apk$")){
                // 包名 com.tencent.qq
                String packageName = ContenUtils.getRegContent(fileNameWithSuffix,"(.*)-(\\d+)\\.apk$",1);
                String tmpGexfPath = gexfDir+File.separator+fileNameWithOutDotApk+".gexf";
                String tmpXmlPath = xmlDir+File.separator+fileNameWithOutDotApk+".xml";

                File tmpGexfFile = new File(tmpGexfPath);
                File tmpXmlFile = new File(tmpXmlPath);

//                检查gexf文件
                if(tmpGexfFile.exists()){
                    hasGexf = "1";
                    gexfSize = tmpGexfFile.length();
//                    System.out.println("文件："+filePath +",gexf存在"+",文件大小"+ tmpGexfFile.length());
                }else {
                    hasGexf = "0";
//                    System.out.println("文件："+filePath +",gexf不存在");
                }

//                检查xml文件ssss
                if(tmpXmlFile.exists()){
                    hasXml = "1";
                    xmlSize = tmpXmlFile.length();
//                    System.out.println("文件："+filePath +",xml存在"+",文件大小" + tmpXmlFile.length());
                }else {
                    hasXml = "0";
//                    System.out.println("文件："+filePath +",xml不存在");
                }
                saib.setApkFileName(fileNameWithSuffix);
                saib.setApkFileDir(apkFileDir);
                saib.setApkPackageName(packageName);
                saib.setHasGexf(hasGexf);
                saib.setHasXml(hasXml);
                saib.setGexfSize(gexfSize);
                saib.setXmlSize(xmlSize);

            }else {
                System.out.println("发现其他格式的文件名（不符合'包名-数字-.apk格式'）: "+fileNameWithSuffix);
            }

            return saib;
        }else {
            System.out.println("发现其他后缀的文件名(不符合.apk后缀)： "+filePath);
            return null;
        }
    }

}

