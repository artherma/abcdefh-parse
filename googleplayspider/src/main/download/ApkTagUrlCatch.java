package main.download;

import main.parse.SaveToMysql;
import main.util.ContenUtils;
import main.util.CoolApkSpider;
import main.util.HttpUtil;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.http.impl.client.CloseableHttpClient;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/*******
 * 下载酷安市场分类的一级、二级tag
 * 保存到文件 、 数据库
 * **********/
public class ApkTagUrlCatch {

    /**
     * 返回一个关于tag的 json数组
     * @param html  访问 https://www.coolapk.com/apk/后的页面
     * @return
     */
    public static JSONArray createJsonTag(String html){
        try {
            // 访问首页
            JSONArray allTagArray = new JSONArray();
            if(html != null && !html.isEmpty()){
                org.jsoup.nodes.Document document = Jsoup.parse(html);
                Elements divEles = document.select("div.type_list");
                for(Element tmpDiv: divEles){
                    JSONObject firstTagObj = new JSONObject();
                    /*****一级tag******/
                    String firstTagName = tmpDiv.select("p.type_title").first().text().trim();
                    String firstTagUrl = tmpDiv.select("p.type_title>a").attr("href").trim();
                    firstTagObj.element("FT_NAME",firstTagName);
                    firstTagObj.element("FT_URL","https://www.coolapk.com"+firstTagUrl);
                    JSONArray firstTagChildArray = new JSONArray();
                    /*****二级tag******/
                    Elements aEles = tmpDiv.select("p.type_tag").first().select("a");
                    for (Element tmpA : aEles) {
                        JSONObject secondTagObj = new JSONObject();
                        String secondTagName = tmpA.text().trim();
                        String secondTagUrl = tmpA.attr("href").trim();

                        secondTagObj.element("ST_NAME",secondTagName);
                        secondTagObj.element("ST_URL","https://www.coolapk.com"+secondTagUrl);
                        firstTagChildArray.add(secondTagObj);
                    }

                    firstTagObj.element("FT_CHILD",firstTagChildArray);
                    allTagArray.add(firstTagObj);
                 }
                return allTagArray;
            }
        }catch (Exception e){
            e.printStackTrace();
            System.out.println(e);
        }

        return null;
    }

    /**
     * 保存 tag 到文件 和 数据库
     * @param jsonArray
     * @param saveFilePath
     */
    private static void saveTagToDataTable(JSONArray jsonArray,String saveFilePath,
                                           String siteName,String siteUrl,String rd){
        if(jsonArray != null && saveFilePath != null && siteName != null
                && siteUrl != null){
            try {
                ContenUtils.writeFile(saveFilePath,jsonArray.toString());
                Connection connection = SaveToMysql.getDefaultConn();
                connection.setAutoCommit(false);

                String sql = "INSERT INTO main_next_tag(first_tag_name,first_tag_url,second_tag_name,second_tag_url," +
                        "site_name,site_url,rd) " +
                        "VALUES(?,?,?,?,?,?,?)";
                PreparedStatement statement = connection.prepareStatement(sql);
                int sum = 0;
                for (int i = 0; i < jsonArray.size(); i++) {
                    JSONObject tmpJsonObj = jsonArray.getJSONObject(i);
                    String FT_NAME = tmpJsonObj.getString("FT_NAME");
                    String FT_URL = tmpJsonObj.getString("FT_URL");

                    JSONArray childArray = tmpJsonObj.getJSONArray("FT_CHILD");

                    for (int j = 0; j < childArray.size(); j++) {
                        JSONObject tmpSecondJsonObj = childArray.getJSONObject(j);
                        String ST_NAME = tmpSecondJsonObj.getString("ST_NAME");
                        String ST_URL = tmpSecondJsonObj.getString("ST_URL");

                        statement.setString(1,FT_NAME);
                        statement.setString(2,FT_URL);
                        statement.setString(3,ST_NAME);
                        statement.setString(4,ST_URL);
                        statement.setString(5,siteName);
                        statement.setString(6,siteUrl);
                        statement.setString(7,rd);
                        int result = statement.executeUpdate();
                        connection.commit();
                        sum++;
                    }
                }
                System.out.println("插入结束，共计"+sum);
            }catch (IOException e){
                e.printStackTrace();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

    }

    public static void main(String[] args) {
        String baseUrl = "https://www.coolapk.com/apk/";
        String tagFilePath = "E:\\tag.json";
        String siteName = "酷安";
        String siteUrl = "https://www.coolapk.com";
        String rd = "mashangzhao";
        CloseableHttpClient httpClient = HttpUtil.createDefaultClient();
        try {
            /**********访问首页，获取一级tag和二级 tag的分类信息 ,解析为json格式文件**********/
            String html = CoolApkSpider.spiderRightAndTag(httpClient,baseUrl);
            JSONArray jsonArray = createJsonTag(html);
            if(jsonArray != null ){
                System.out.println(jsonArray.toString());
                // 保存到文件,写到数据库
                saveTagToDataTable(jsonArray,tagFilePath,siteName,siteUrl,rd);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        try {
            httpClient.close();
        }catch (IOException e){
            e.printStackTrace();
        }
     }

}
