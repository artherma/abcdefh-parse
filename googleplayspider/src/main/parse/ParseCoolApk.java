package main.parse;

import main.bean.CoolApkInfo;
import main.bean.CoolApkTag;
import main.download.ApkFileDown;
import main.util.ContenUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * coolapk page parse
 */
public class ParseCoolApk {

    /**
     * 解析 某个 apk 独立页面
     * @param html
     * @param url
     * @return
     */
    public static CoolApkInfo parseFromPage(String html,String url){
        CoolApkInfo coolApkInfo = new CoolApkInfo();

        if(html.contains("你访问的页面不存在或已删除")){
            return null;
        }
        if(html != null && !html.isEmpty()){
            Document document = Jsoup.parse(html);
            String appName = document.select("span.bread_nav_second").first().text().trim();
            String version = document.select("span.list_app_info").first().text().trim();
            String fileSizeAndDownloadNum = document.select("p.apk_topba_message").first().text().trim();
            String newVersionDes = document.select("p:contains(新版特性)").next().text().trim();
            // 介绍
            String description = document.select("div.apk_left_title_info").first().text().trim();

            String permission = document.select("div.apk_left_title_info").get(1).text();
            String otherTag = document.select("p:contains(分类标签)").next().text().trim();

            String score = document.select("p.rank_num").first().text().trim();
            String scoreNumStr = document.select("p.apk_rank_p1").first().text().trim();
            String scoreNum = ContenUtils.getRegContent(scoreNumStr,"共(.*)个评分",1);

            String dev = document.select("p:contains(应用包名)").first().text().trim();
            String packageName = ContenUtils.getRegContent(dev,"应用包名：\\s*(.*)\\s*更新时间",1).trim();
            String updateTime = ContenUtils.getRegContent(dev,"更新时间：\\s*(.*)\\s*支持ROM",1).trim();
            String supportRom = ContenUtils.getRegContent(dev,"支持ROM：\\s*(.*)\\s*开发者名称",1).trim();
            String developer = ContenUtils.getRegContent(dev,"开发者名称：\\s*(.*)\\s*",1).trim();

            coolApkInfo.setScore(score);
            coolApkInfo.setScoreUserNum(scoreNum);
            coolApkInfo.setUpdateTime(updateTime);
            coolApkInfo.setSupportRom(supportRom);
            coolApkInfo.setDevelop(developer);

//            68.35M / 585万下载 / 5480人关注 / 2874个评论 / 简体中文
            String[] array = fileSizeAndDownloadNum.split("/");
            if(array.length >= 4){
                 coolApkInfo.setAppFileSizeShowInPage(array[0].trim());

                if(array[1].trim().contains("下载")){
                    coolApkInfo.setDownloadNum(ContenUtils.getRegContent(array[1].trim(),"(.*)下载",1));
                }
                if(array[2].trim().contains("关注")){
                    coolApkInfo.setFocusUserNum(ContenUtils.getRegContent(array[2].trim(),"(.*)人关注",1));
                }
                coolApkInfo.setLanguage(array[4].trim());
            }
            coolApkInfo.setApkUrl(url);
            coolApkInfo.setNewVersionDes(newVersionDes);
            coolApkInfo.setAppName(appName);
            coolApkInfo.setVersion(version);
            coolApkInfo.setDescription(description);
            coolApkInfo.setPackageName(packageName);
            coolApkInfo.setPermission(permission);
            coolApkInfo.setOtherTag(otherTag);

            System.out.println(coolApkInfo.toString());
        }

        return coolApkInfo;
    }

    /**
     * 解析tag 名称 和 url，得到一级tag 和二级 tag
     * @param html
     * @return
     */
    public static List<CoolApkTag> parseFromFirstPage(String html){
        List<CoolApkTag> coolApkTagList = new ArrayList<CoolApkTag>();

        if(html != null && !html.isEmpty()){
            Document document = Jsoup.parse(html);
            Elements divEles = document.select("div.type_list");
            for (Element tmpDiv : divEles) {
                CoolApkTag coolApkTag = new CoolApkTag();

                String firstTag = tmpDiv.select("p.type_title").first().text().trim();
                String firstTagUrl = tmpDiv.select("p.type_title>a").attr("href").trim();

                coolApkTag.setFirstTagName(firstTag);
                coolApkTag.setFirstTagUrl("https://www.coolapk.com"+firstTagUrl);

                Elements aEles = tmpDiv.select("p.type_tag").first().select("a");
                HashMap<String,String> secondTagMap = new HashMap<String,String >();
                for (Element tmpA : aEles) {
                    String secondTag = tmpA.text().trim();
                    String secondTagUrl = tmpA.attr("href").trim();

                    secondTagMap.put(secondTag,"https://www.coolapk.com"+secondTagUrl);
                    coolApkTag.setSecondTag(secondTagMap);
//                    System.out.println("2-st tag = "+secondTag);
//                    System.out.println("2-st tag url = "+secondTagUrl);
                }
                coolApkTagList.add(coolApkTag);
                System.out.println(coolApkTag);
            }
        }
        return coolApkTagList;
    }

//    "浏览器","1-66"
/**
 * 解析某个二级 tag 的起始终结 页面 range
 *  2-st tag : start_page - end_page
 *  找到开始，结束页面
 */
    public static HashMap<String,String> parseFromOneTagIndexPage(String html,String tag){
        if(html !=null && tag != null && !html.isEmpty()){
            HashMap<String,String> tagPageIndexRange = new HashMap<String,String>();
            Document document = Jsoup.parse(html);
            Element ulEles = document.select("ul.pagination").first();
            String href = ulEles.select("li>a:contains(尾页)").first().attr("href");
            String range = "1-1";
            if(!href.contains("javascript:void") && href.contains("?p=")){
                String endPageIndex = ContenUtils.getRegContent(href,"p\\s*=\\s*(\\d+)",1);
                range = "1-"+endPageIndex;
            }
            tagPageIndexRange.put(tag,range);
            return tagPageIndexRange;
        }
        return null;
    }

    /*******获取页面 p=2中的 app 链接****/
    public static List<String> getListFromOnePage(String html){
        List<String> apkUrlList = new ArrayList<String>();
        if(html != null && !html.isEmpty()){
            Document document  = Jsoup.parse(html);
            Elements div = document.select("div.app_list_left>a");
            for (Element tmpEle : div) {
                String url = tmpEle.attr("href").trim();
                if(!url.startsWith("http")){
                    apkUrlList.add("https://www.coolapk.com"+url);
                }
            }
        }
        return apkUrlList;
    }

    public static void main(String[] args) throws SQLException{
        String filePath = "E:\\parse\\t1.html";
        try {
            String html = ContenUtils.readAsString(filePath);
            String downloadUrl = ContenUtils.getRegContent(html,"window.location.href\\s*=\\s*\"(.*)\";",1);
            CoolApkInfo apkInfo = parseFromPage(html,downloadUrl);
            List<CoolApkInfo> coolApkInfoList = new ArrayList<>();
            coolApkInfoList.add(apkInfo);

            ApkFileDown.saveApkInfoToDataTable(coolApkInfoList);

        }catch (IOException e){
            e.printStackTrace();
        }
    }


}
