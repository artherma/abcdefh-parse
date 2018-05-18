package main.util;

import main.bean.CoolApkTag;
import main.parse.ParseCoolApk;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

/**
 * 从https://www.coolapk.com/apk/ 访问，获取more apk的相关链接
 */
public class CoolApkSpider {
    public static void main(String[] args) {
        CloseableHttpClient httpClient = HttpUtil.createDefaultClient();
        String baseUrl = "https://www.coolapk.com/apk/";
        try {
            /**********访问首页，获取一级tag和二级 tag的分类信息**********/
            String html = spiderRightAndTag(httpClient,baseUrl);
            List<CoolApkTag> coolApkTagList = ParseCoolApk.parseFromFirstPage(html);

            /******** 取1个 一级 tag ******/
            HashMap<String,String> tmpMap = coolApkTagList.get(3).getSecondTag();
            String secondTagEx = "聊天";
            String url = tmpMap.get(secondTagEx);
            String secondATagUrl = ContenUtils.encodingChinese(url,"UTF-8");
            System.out.println(secondATagUrl);

            /*****获取 tag 的页面 范围 ***********/
            String seondHtml = spiderRightAndTag(httpClient,secondATagUrl);
            HashMap<String,String> tagRangeMap = ParseCoolApk.parseFromOneTagIndexPage(seondHtml,secondTagEx);
            int endIndex = 1;
            if(tagRangeMap != null){
//                System.out.println(tagRangeMap.get(secondTagEx));
                String[] array = tagRangeMap.get(secondTagEx).split("-");
                String endPageIndex = array[1];
                endIndex = Integer.parseInt(endPageIndex);
            }

            /*********访问二级 tag 的 page =2*******/

            for (int i = 1; i <= endIndex ; i++) {
                if(i > 10){
                    break;
                }
                String secondTagUrlIndex1 = secondATagUrl+"?p="+i;
                String sourceList = spiderRightAndTag(httpClient,secondTagUrlIndex1);
                List<String> apkUrlList = ParseCoolApk.getListFromOnePage(sourceList);
                System.out.println(apkUrlList);
//                HttpUtil.downloadCoolApk(httpClient,apkUrlList);
            }

            /********测试使用*********/
//            List<String> testList = new ArrayList<>();
//            testList.add("https://www.coolapk.com/apk/com.ccmt.supercleaner");
//            testList.add("https://www.coolapk.com/apk/in.co.pricealert.apps2sd");
//            testList.add("https://www.coolapk.com/apk/com.smartisanos.smartfolder.aoa");
//            HttpUtil.downloadCoolApk(httpClient,testList);


        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public static String spiderRightAndTag(CloseableHttpClient httpClient,String requestUrl){
        if(requestUrl != null){
            HttpGet httpGet = new HttpGet(requestUrl);
            try {
                CloseableHttpResponse response = httpClient.execute(httpGet);
                HttpEntity entity = response.getEntity();
                if(response.getStatusLine().getStatusCode() == 200 && entity != null){
                    String html = EntityUtils.toString(entity);
                    return html;
                }else {
                    System.out.println("url="+requestUrl+"\nstatuscode="+response.getStatusLine().getStatusCode());
                }
                response.close();
            }catch (IOException e){
                e.printStackTrace();
            }
        }
        return null;
    }
}
