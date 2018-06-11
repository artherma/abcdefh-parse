package main.parse;

import main.bean.GooglePAppInfo;
import main.util.ContenUtils;
import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashSet;

/**
 * 解析静态的html页面（from-curl）
 */
public class ParseStaticHtml {


    public static GooglePAppInfo parseFromCurl(String packageName,String html){
        if(html != null && (!html.contains("error-section") || !html.contains("the requested URL was not found"))
            && !html.isEmpty()){
            System.out.printf("this %s is ok\n",packageName);

            GooglePAppInfo appInfo = new GooglePAppInfo();
            Document document = Jsoup.parse(html);
//            名称
            String appName = null;
            Elements appNameEles =document.select("h1[itemprop=name]");
            if(appNameEles != null && appNameEles.size() > 0){
                appName = appNameEles.first().text().trim();
            }
//            开发者信息，链接
            String devId = null;
            String devIdUrl = null;
            Elements devIdEles = document.select("span.T32cc.UAO9ie");
            if(devIdEles != null && devIdEles.size() > 0){
                devId = devIdEles.first().text().trim();
                devIdUrl = devIdEles.select("a").first().attr("href");
            }
//            System.out.println("devId="+devId);
//            System.out.println("devUrl="+devIdUrl);
            // 分类,以及url
            String tagInfos = "";
            String tagInfoUrls = "";
            Elements genreEles = document.select("a[itemprop=genre]");
            for (int i = 0; genreEles != null && i < genreEles.size(); i++) {
                String tagLink = genreEles.get(i).attr("href").trim();
                String tagName = genreEles.get(i).text().trim();
                tagInfos += tagName;
                tagInfoUrls += tagLink;
                if(i != genreEles.size()-1){
                    tagInfos += ";;;";
                    tagInfoUrls += ";;;";
                }
            }

            String description = null;
            Elements desElems = document.select("div[jsname=sngebd]");
            if(desElems != null && desElems.size() > 0){
                description = desElems.first().text().trim();
            }

            String whatsNew = null;
            Elements whatsNesEles = document.select("div[jsname=bN97Pc]");
            if(whatsNesEles != null && whatsNesEles.size() > 0){
                whatsNew = whatsNesEles.last().text().trim();
            }

//            评分信息（总数、及各个星星数量）
            String score = null;
            Elements scoreEles = document.select("div.BHMmbe");
            if(scoreEles != null && scoreEles.size() > 0){
                score = scoreEles.first().text().trim();

                String rateNum = null;
                Elements spansNum = document.select("span.EymY4b").first().select("span");
                if(spansNum.size() > 2){
                    rateNum = spansNum.get(2).text().trim().replace(",","");
                }
                String rate5 = document.select("div.mMF0fd>span:contains(5)").next().attr("title").trim().replace(",","");
                String rate4 = document.select("div.mMF0fd>span:contains(4)").next().attr("title").trim().replace(",","");
                String rate3 = document.select("div.mMF0fd>span:contains(3)").next().attr("title").trim().replace(",","");
                String rate2 = document.select("div.mMF0fd>span:contains(2)").next().attr("title").trim().replace(",","");
                String rate1 = document.select("div.mMF0fd>span:contains(1)").next().attr("title").trim().replace(",","");

                if(rateNum.matches("\\d+")){
                    appInfo.setRateNum(Integer.parseInt(rateNum));
                }else {
                    appInfo.setRateNum(0);
                }

                if(rate5.matches("\\d+")){
                    appInfo.setRate5(Integer.parseInt(rate5));
                }else {
                    appInfo.setRate5(0);
                }

                if(rate4.matches("\\d+")){
                    appInfo.setRate4(Integer.parseInt(rate4));
                }else {
                    appInfo.setRate4(0);
                }

                if(rate3.matches("\\d+")){
                    appInfo.setRate3(Integer.parseInt(rate3));
                }else {
                    appInfo.setRate3(0);
                }

                if(rate2.matches("\\d+")){
                    appInfo.setRate2(Integer.parseInt(rate2));
                }else {
                    appInfo.setRate2(0);
                }

                if(rate1.matches("\\d+")){
                    appInfo.setRate1(Integer.parseInt(rate1));
                }else {
                    appInfo.setRate1(0);
                }
            }


//            其他信息
            String Updated = document.select("div.hAyfc>div:contains(Updated)").next().text().trim();
            String Size = document.select("div.hAyfc>div:contains(Size)").next().text().trim();
            String Installs = document.select("div.hAyfc>div:contains(Installs)").next().text().trim();
            String Current_Version = document.select("div.hAyfc>div:contains(Current Version)").next().text().trim();
            String Requires_Android = document.select("div.hAyfc>div:contains(Requires Android)").next().text().trim();
            String Content_Rating = document.select("div.hAyfc>div:contains(Content Rating)").next().select("div>span>div").first().text().trim();
            String Interactive_Elements = document.select("div.hAyfc>div:contains(Interactive Elements)").next().text().trim();
            String In_app_Products = document.select("div.hAyfc>div:contains(In-app Products)").next().text().trim();
            String Offered_By = document.select("div.hAyfc>div:contains(Offered By)").next().text().trim();

            appInfo.setPackageName(packageName);
            appInfo.setAppName(appName);
            appInfo.setDevErName(devId);
            appInfo.setDevErUrl(devIdUrl);
            appInfo.setTagInfos(tagInfos);
            appInfo.setTagInfoUrls(tagInfoUrls);
            appInfo.setDescription(description);
            appInfo.setWhatsNew(whatsNew);
            appInfo.setScore(score);
            appInfo.setUpdated(ContenUtils.getAppPublishDate(Updated));
            appInfo.setSize(Size);
            appInfo.setInstalls(Installs);
            appInfo.setCurrent_Version(Current_Version);
            appInfo.setRequires_Android(Requires_Android);
            appInfo.setContent_Rating(Content_Rating);
            appInfo.setInteractive_Elements(Interactive_Elements);
            appInfo.setIn_app_Products(In_app_Products);
            appInfo.setOffered_By(Offered_By);

            return appInfo;

        }else {
            System.out.println("404 may");
            return null;
        }
    }

    public static void main(String[] args) {
        String htmlParrentPath = "E:\\gexf\\apk-fa\\fa-html";

        String allLog = "E:\\gexf\\apk-fa\\log-curl-sql\\allLog.txt";
        String okLog = "E:\\gexf\\apk-fa\\log-curl-sql\\okLog.txt";
        String errorLog = "E:\\gexf\\apk-fa\\log-curl-sql\\errorLog.txt";

        HashSet<String> tagSet = new HashSet<String>();

        File allLogFile = new File(allLog);
        File okLogFile = new File(okLog);
        File errorLogFile = new File(errorLog);

        if(!allLogFile.exists()){
            try {
                FileUtils.touch(allLogFile);
            }catch (IOException e){
                e.printStackTrace();
            }
        }

        if(!okLogFile.exists()){
            try {
                FileUtils.touch(okLogFile);
            }catch (IOException e){
                e.printStackTrace();
            }
        }
        if(!errorLogFile.exists()){
            try {
                FileUtils.touch(errorLogFile);
            }catch (IOException e){
                e.printStackTrace();
            }
        }


        // 获取数据库连接
        java.sql.Connection connection  = SaveToMysql.getDefaultConn();
        if(connection != null){
            try {
                connection.setAutoCommit(false);
            }catch (SQLException e){
                e.printStackTrace();
            }
//          批处理操作

            long startTime = System.currentTimeMillis();
            batchParse(htmlParrentPath,allLogFile,okLogFile,errorLogFile,connection,tagSet);
            long endTIme = System.currentTimeMillis();
            System.out.println("耗时:"+(endTIme - startTime) / 1000);
        }else {
            System.out.println("数据库连接失败");
        }

        // 关闭连接
        try {
            connection.close();
        }catch (SQLException e){
            e.printStackTrace();
        }



    }

    public static void batchParse(String htmlPath, File allLog, File okLog, File errorLog, Connection connection,HashSet<String> tags){
        if(htmlPath != null && !htmlPath.isEmpty()){
            String sql = "INSERT into apk_detail  (package_name ,app_name ,dev_name ,dev_url ,tag_info,tag_info_url , " +
                    "description ,whats_new ,score ,rate_num ,rate5 ,rate4," +
                    "rate3,rate2,rate1,updated_time ,size ,install_num ," +
                    "current_version ,requires_android ,content_rating ,interactive_elements ,in_app_products ,offered_by ) " +
                    "VALUES (?,?,?,?,?,?," +
                    "?,?,?,?,?,?," +
                    "?,?,?,?,?,?," +
                    "?,?,?,?,?,?);";
            PreparedStatement preparedStatement = null;
            try {
                preparedStatement = connection.prepareStatement(sql);

            }catch (SQLException e){
                e.printStackTrace();
            }


            File file = new File(htmlPath);
            if(file.exists() && file.isDirectory()){
                File[] filesHtml = file.listFiles();
                for (int i = 0; i < filesHtml.length; i++) {

//                    if(i > 500){
//                        break;
//                    }

                    // 文件太小，不符合
                    if(filesHtml[i].length() < 1024 * 3){
                        String data = "html文件["+filesHtml[i].getName()+"]大小不符合规范\n";
                        StoreApkGexf.commonErrorLog(allLog,data);
                        StoreApkGexf.commonErrorLog(errorLog,filesHtml[i].getName()+"\n");
                        continue;
                    }

                    String filePath = filesHtml[i].getPath();
                    String fileName = filesHtml[i].getName();
                    String packageName = fileName.substring(0,fileName.length()-5);
                    String html = null;

                    try {
                        html = ContenUtils.readAsString(filePath);
                    }catch(IOException e){
                        e.printStackTrace();
                    }

                    System.out.println(fileName);
                    GooglePAppInfo appinfo = null;

                    // 解析部分
                    try {
                        appinfo = parseFromCurl(packageName,html);
                    }catch (Exception e){
                        String data = "html文件["+fileName+"]，解析出错,原因["+e.getMessage()+"]\n";
                        StoreApkGexf.commonErrorLog(allLog,data);
                        StoreApkGexf.commonErrorLog(errorLog,fileName+"\n");
                        e.printStackTrace();
                    }

                    // 检查、插入部分
                    if(appinfo != null){
                        StoreApkGexf.commonErrorLog(okLog,fileName+"\n");

//                        整理tag信息
                        String tagInfos = appinfo.getTagInfos();
                        String[] strings = tagInfos.split(";;;");
                        for (int j = 0; j < strings.length; j++) {
                            if(!tags.contains(strings[j])){
                                tags.add(strings[j]);
                            }
                        }

                        try {
                            preparedStatement.setString(1,appinfo.getPackageName());
                            preparedStatement.setString(2,appinfo.getAppName());
                            preparedStatement.setString(3,appinfo.getDevErName());
                            preparedStatement.setString(4,appinfo.getDevErUrl());
                            preparedStatement.setString(5,appinfo.getTagInfos());
                            preparedStatement.setString(6,appinfo.getTagInfoUrls());
                            preparedStatement.setString(7,appinfo.getDescription());
                            preparedStatement.setString(8,appinfo.getWhatsNew());
                            preparedStatement.setString(9,appinfo.getScore());
                            preparedStatement.setInt(10,appinfo.getRateNum());
                            preparedStatement.setInt(11,appinfo.getRate5());
                            preparedStatement.setInt(12,appinfo.getRate4());
                            preparedStatement.setInt(13,appinfo.getRate3());
                            preparedStatement.setInt(14,appinfo.getRate2());
                            preparedStatement.setInt(15,appinfo.getRate1());
                            preparedStatement.setString(16,appinfo.getUpdated());
                            preparedStatement.setString(17,appinfo.getSize());
                            preparedStatement.setString(18,appinfo.getInstalls());
                            preparedStatement.setString(19,appinfo.getCurrent_Version());
                            preparedStatement.setString(20,appinfo.getRequires_Android());
                            preparedStatement.setString(21,appinfo.getContent_Rating());
                            preparedStatement.setString(22,appinfo.getInteractive_Elements());
                            preparedStatement.setString(23,appinfo.getIn_app_Products());
                            preparedStatement.setString(24,appinfo.getOffered_By());
                            preparedStatement.addBatch();
                        }catch (SQLException e){
                            e.printStackTrace();
                        }

                    }

                    if(i % 500 == 0){
                        try {
                            preparedStatement.executeBatch();
                            connection.commit();
                            System.out.println("Batch executed successful1");
                        }catch (SQLException e){
                            e.printStackTrace();
                        }
                    }
                }

                System.out.println(tags.toString());

                try {
                    preparedStatement.executeBatch();
                    connection.commit();
                    System.out.println("Batch executed successful1");
                }catch (SQLException e){
                    e.printStackTrace();
                }

            }else {
                System.out.println("不存在目录"+htmlPath);
            }
        }
    }


}
