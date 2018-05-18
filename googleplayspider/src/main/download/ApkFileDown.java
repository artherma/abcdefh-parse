package main.download;

import main.bean.CoolApkInfo;
import main.bean.MyCoolApkTag;
import main.parse.ParseCoolApk;
import main.parse.SaveToMysql;
import main.util.ContenUtils;
import main.util.HttpUtil;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.io.FileUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/***********
 * 下载apk文件
 */
public class ApkFileDown {

    private static final String APKSAVEPARENTPATH = "E:\\test1\\";
    private static final String LOCAL_JSON_TAG_RANGE = "E:\\tag-page-range.json";

    // 下载链接在json文件中
    public static JSONArray getTagWithJson(String tagJsonFilePath) throws IOException{
        JSONArray resultArray = new JSONArray();
        String tagJson = ContenUtils.readAsString(tagJsonFilePath);
        JSONArray jsonArray = JSONArray.fromObject(tagJson);
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject tmpJsonObj = jsonArray.getJSONObject(i);
            String FT_NAME = tmpJsonObj.getString("FT_NAME");
            String FT_URL = tmpJsonObj.getString("FT_URL");

            JSONArray childArray = tmpJsonObj.getJSONArray("FT_CHILD");

            for (int j = 0; j < childArray.size(); j++) {
                JSONObject tmpSecondJsonObj = childArray.getJSONObject(j);
                String ST_NAME = tmpSecondJsonObj.getString("ST_NAME");
                String ST_URL = tmpSecondJsonObj.getString("ST_URL");

                JSONObject jsonObject = new JSONObject();
                jsonObject.element("FT_NAME",FT_NAME);
                jsonObject.element("FT_URL",FT_URL);
                jsonObject.element("ST_NAME",ST_NAME);
                jsonObject.element("ST_URL",ST_URL);
                resultArray.add(jsonObject);
            }
        }
        System.out.println(resultArray.toString());
        return resultArray;
    }

    // 下载链接从数据库中提取(有序的)，合成 json格式
    public static JSONArray getTagWithSqlResult(){
        Connection connection = SaveToMysql.getDefaultConn();

        String sql = "SELECT first_tag_name,first_tag_url,second_tag_name,second_tag_url from main_next_tag " +
                "where site_name = '酷安' " +
                "ORDER BY first_tag_name DESC;";
        JSONArray jsonArray = new JSONArray();
        try{
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()){
                JSONObject jsonObject = new JSONObject();

                String FT_NAME = resultSet.getString(1);
                String FT_URL = resultSet.getString(2);
                String ST_NAME = resultSet.getString(3);
                String ST_URL = resultSet.getString(4);

                jsonObject.element("FT_NAME",FT_NAME);
                jsonObject.element("FT_URL",FT_URL);
                jsonObject.element("ST_NAME",ST_NAME);
                jsonObject.element("ST_URL",ST_URL);
                jsonArray.add(jsonObject);
            }
            System.out.println(jsonArray.toString());
            return jsonArray;
        }catch (SQLException e){
            e.printStackTrace();
        }
        return null;
    }

    // 实时 构造二级tag的页面范围
    public static JSONArray getTagPageRange(JSONArray jsonArray) throws UnsupportedEncodingException{
        JSONArray resultJsonArray = JSONArray.fromObject(jsonArray);

        CloseableHttpClient httpClient = HttpUtil.createDefaultClient();
        String baseUrl = "https://www.coolapk.com/apk/";
        HttpUtil.doASimpleGet(httpClient,baseUrl);

        for (int i = 0; i < resultJsonArray.size(); i++) {
            JSONObject jsonObject = resultJsonArray.getJSONObject(i);
            String ST_NAME = jsonObject.getString("ST_NAME");
            String ST_URL = jsonObject.getString("ST_URL");

            String tagUrl = ContenUtils.encodingChinese(ST_URL,"UTF-8");
            String secondHtml = HttpUtil.doASimpleGet(httpClient,tagUrl);
            HashMap<String,String> tagRangeMap = ParseCoolApk.parseFromOneTagIndexPage(secondHtml,ST_NAME);
            int endIndex = 1;
            if(tagRangeMap != null){
                String[] array = tagRangeMap.get(ST_NAME).split("-");
                String endPageIndex = array[1];
                endIndex = Integer.parseInt(endPageIndex);
            }
            jsonObject.element("ST_RANGE",endIndex);
        }
        System.out.println(resultJsonArray.toString());
        return resultJsonArray;
    }
    // 获取apk-url集合
    public static void getApksUrl(JSONArray ttArray,CloseableHttpClient httpClient) throws IOException, SQLException {

        String localTagWithRange = ContenUtils.readAsString(LOCAL_JSON_TAG_RANGE);
        JSONArray tagArray = JSONArray.fromObject(localTagWithRange);
        for (int i = 0; i < tagArray.size(); i++) {
            JSONObject tmpJsonObj = tagArray.getJSONObject(i);
            String FT_NAME = tmpJsonObj.getString("FT_NAME");
            String FT_URL = tmpJsonObj.getString("FT_URL");
            String ST_NAME = tmpJsonObj.getString("ST_NAME");
            String ST_URL = tmpJsonObj.getString("ST_URL");
            String ST_RANGE = tmpJsonObj.getString("ST_RANGE");


//            系统工具
            if(FT_NAME.equals("金融财经") || FT_NAME.equals("通讯网络") || FT_NAME.equals("运动健康") ||
                    FT_NAME.equals("社交聊天") || FT_NAME.equals("资讯阅读")){
                continue;
            }

            MyCoolApkTag myCoolApkTag = new MyCoolApkTag();
            myCoolApkTag.setFT_NAME(FT_NAME);
            myCoolApkTag.setFT_URL(FT_URL);
            myCoolApkTag.setST_NAME(ST_NAME);
            myCoolApkTag.setST_URL(ST_URL);

            for (int j = 1; j <= Integer.parseInt(ST_RANGE) ; j++) {
                if(j > 5){
                    break;
                }
                String tmpSecondTagWithIndex = ContenUtils.encodingChinese(ST_URL+"?p="+j,"UTF-8");

                // 打印日志，访问的二级tag页面index
                System.out.println("==========[TIME:"+ContenUtils.getNowTime() +" ,FT_NAME:"+FT_NAME+" ,ST_NAME:"+ST_NAME+" ,ST_URL: "+ST_URL+" ,PAGE:"+
                        j+" ,url: "+tmpSecondTagWithIndex+" ]===============\n");
                String apkUrlListHtml = HttpUtil.doASimpleGet(httpClient,tmpSecondTagWithIndex);
                List<String> apkUrlList = ParseCoolApk.getListFromOnePage(apkUrlListHtml);
                // @TODO 继续
                middleProcess(httpClient,apkUrlList,myCoolApkTag);
            }
        }

    }

    public static void middleProcess(CloseableHttpClient httpClient,List<String> apkUrls,MyCoolApkTag myCoolApkTag) throws IOException, SQLException {
        if(apkUrls != null && apkUrls.size() > 0){
            List<CoolApkInfo> coolApkInfoList = new ArrayList<CoolApkInfo>();
            for (String apkUrl : apkUrls) {
                CoolApkInfo coolApkInfo = process(httpClient, apkUrl,myCoolApkTag);
                coolApkInfoList.add(coolApkInfo);
            }
            // 写一次数据库
//            @TODO
//            saveApkInfoToDataTable(coolApkInfoList);

        }
    }
    /*****解析apk信息到数据库*************/
    public static void saveApkInfoToDataTable(List<CoolApkInfo> coolApkInfos) throws SQLException {
        if(coolApkInfos == null){
            return;
        }
        Connection connection = SaveToMysql.getDefaultConn();
        connection.setAutoCommit(false);
        String sql = "INSERT INTO apk_info (apk_name,apk_version,apk_package_name,apk_file_size,apk_description,apk_first_tag,apk_second_tag," +
                "apk_other_tag,apk_permission,apk_download_num,apk_focus_num,apk_language,apk_url,apk_newVersion_des,apk_score," +
                "apk_score_userNum,apk_update_time,apk_support_rom,apk_develper,apk_has_download" +
                ") values (?,?,?,?,?," +
                "?,?,?,?,?," +
                "?,?,?,?,?," +
                "?,?,?,?,?" +
                ");";
        PreparedStatement statement = connection.prepareStatement(sql);
        int sum = 0;
        for(CoolApkInfo apkInfo: coolApkInfos){
            if(apkInfo == null){
                continue;
            }
            statement.setString(1,apkInfo.getAppName());
            statement.setString(2,apkInfo.getVersion());
            statement.setString(3,apkInfo.getPackageName());
            statement.setString(4,apkInfo.getAppFileSizeShowInPage());
            statement.setString(5,apkInfo.getDescription());
            statement.setString(6,apkInfo.getMainTag());
            statement.setString(7,apkInfo.getNextTag());
            statement.setString(8,apkInfo.getOtherTag());
            statement.setString(9,apkInfo.getPermission());
            statement.setString(10,apkInfo.getDownloadNum());
            statement.setString(11,apkInfo.getFocusUserNum());
            statement.setString(12,apkInfo.getLanguage());
            statement.setString(13,apkInfo.getApkUrl());
            statement.setString(14,apkInfo.getNewVersionDes());
            statement.setString(15,apkInfo.getScore());
            statement.setString(16,apkInfo.getScoreUserNum());
            statement.setString(17,apkInfo.getUpdateTime());
            statement.setString(18,apkInfo.getSupportRom());
            statement.setString(19,apkInfo.getDevelop());
            statement.setBoolean(20,apkInfo.isHasDownload());
            int result  = statement.executeUpdate();
            connection.commit();
            sum++;
        }
        System.out.println("apk_info插入结束，共计"+sum);
    }


    /***
     * 对每个app的下载，较耗时，研究 多线程下载。。
     * @TODO
     * @param httpClient
     * @param apkUrl
     * @param myCoolApkTag
     * @return
     * @throws IOException
     */
    public static CoolApkInfo process(CloseableHttpClient httpClient,String apkUrl,MyCoolApkTag myCoolApkTag) throws IOException {
        if(apkUrl != null){
            System.out.println("\n==================[开始访问链接  "+apkUrl+" ]===================");
            HttpGet httpGet = new HttpGet(apkUrl);

            Header[] headers = new Header[]{
                    new BasicHeader("Upgrade-Insecure-Requests","1"),
                    new BasicHeader("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8"),
                    new BasicHeader("Accept-Language","zh-CN,zh;q=0.9,en-GB;q=0.8,en;q=0.7"),
                    new BasicHeader("User-Agent","Mozilla/5.0 (Windows NT 6.3; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/65.0.3325.181 Safari/537.36")
            };
            httpGet.setHeaders(headers);
            httpGet.setHeader("Accept-Encoding","gzip, deflate");
            CloseableHttpResponse response = httpClient.execute(httpGet);
            HttpEntity entity = response.getEntity();
            if(entity != null){
                System.out.println("==================[访问链接  "+apkUrl+" 成功]===================");
                String html = EntityUtils.toString(entity);
                /***********预判断 是否为正确页面********/
                if(html.contains("你访问的页面不存在或已删除")){
                    System.out.println("=============[预判断为错误页面，停止继续访问，未下载文件]==================\n\n");
                    return null;
                }
                /*********** 下载apk文件********/
                String ssid = HttpUtil.generateCoolApkCookie(response.getHeaders("Set-Cookie"));
                String downloadUrl = ContenUtils.getRegContent(html,"window.location.href\\s*=\\s*\"(.*)\";",1);
                //文件名
                StringBuilder fileName = new StringBuilder(ContenUtils.getPackageNameCoolApk(downloadUrl));
                File file = new File(APKSAVEPARENTPATH+fileName.toString()+".apk");
                long startTime = System.currentTimeMillis();
                boolean isDown = downloadFile(httpClient,file,ssid,headers,downloadUrl,apkUrl);
                long endTime = System.currentTimeMillis();
                if(isDown){
                    System.out.println("=================[下载文件完成 "+file.getName()+" ,耗时："+(endTime-startTime)+"ms]=========================");
                }else {
                    System.out.println("=================[下载文件失败 "+file.getName()+" ,耗时："+(endTime-startTime)+"ms]=========================\n\n");
                }

                /*********** 解析apk属性信息********/
                CoolApkInfo coolApkInfo = ParseCoolApk.parseFromPage(html,apkUrl);
                coolApkInfo.setHasDownload(isDown);
                coolApkInfo.setMainTag(myCoolApkTag.getFT_NAME());
                coolApkInfo.setNextTag(myCoolApkTag.getST_NAME());

                /*********** 存储apk属性信息到 数据库
                 * 频繁写数据库，耗时，一页一写
                 * ********/

                JSONObject jsonObject = JSONObject.fromObject(coolApkInfo);
                String apkInfoPath = APKSAVEPARENTPATH+"info\\"+fileName+".json";
                ContenUtils.writeFile(apkInfoPath,jsonObject.toString());
                return coolApkInfo;

            }else {
                System.out.println("==================[访问链接 "+apkUrl+" 失败，状态码"+ response.getStatusLine().getStatusCode()+"]===================");
            }
            return null;
        }

        return null;
    }

    private static boolean downloadFile(CloseableHttpClient httpClient,File desFile,
                                     String cookie,Header[] headers,String downloadUrl,String apkUrl){
        HttpGet  httpGet = new HttpGet(downloadUrl);
        httpGet.setHeaders(headers);
        httpGet.setHeader("Set-Cookie",cookie);
        httpGet.setHeader("Accept-Encoding","gzip, deflate, br");
        httpGet.setHeader("Referer",apkUrl);
        try {
            CloseableHttpResponse response = httpClient.execute(httpGet);
            HttpEntity entity = response.getEntity();
            if(response.getStatusLine().getStatusCode() == 302){
                System.out.println("==========请求下载地址返回状态码 302============");
                String location = response.getFirstHeader("Location").getValue();
                System.out.println("localtion = "+location);
                System.out.println("==========\r"+EntityUtils.toString(entity));

            }else if(response.getStatusLine().getStatusCode() == 404){
                System.out.println("==========请求下载地址返回状态码404=======");
            }else if(response.getStatusLine().getStatusCode() ==200){
                System.out.println("==========请求下载地址返回状态码200==========");
                if(desFile.exists()){
                    System.out.println("====文件已存在 "+desFile.getName() + "======");
                }else {
                    FileUtils.copyInputStreamToFile(entity.getContent(),desFile);
                }
                System.out.println("==========写到文件完成 "+desFile.getAbsolutePath()+" ===========");
                try {
//                    double random = Math.random();
//                    int sleep = ((int) (random * 10000 )) % 1000;
                    Thread.sleep(1*1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return true;
            }
        }catch (IOException e){
            e.printStackTrace();
        }
        return false;
    }



    public static void main(String[] args) throws SQLException{
        String fileReadPath = "E:\\tag.json";
        try {
            getTagWithJson(fileReadPath);
            JSONArray tmpArray = getTagWithSqlResult();
//            JSONArray jsonWithRange = getTagPageRange(tmpArray);

            CloseableHttpClient httpClient = HttpUtil.createDefaultClient();
            String baseUrl = "https://www.coolapk.com/apk/";
            HttpUtil.doASimpleGet(httpClient,baseUrl);

            getApksUrl(null,httpClient);

        }catch (IOException e){

        }

    }


}
