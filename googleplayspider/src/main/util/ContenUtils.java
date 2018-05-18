package main.util;

import java.io.*;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 获取http请求返回的内容
 * 读文件 、 写文件
 * html解析工具方法
 */
public class ContenUtils {

    public static final String zhPatter = "[\u4e00-\u9fa5]+";
    static  Map<String,String>  monthItem = new HashMap<String,String>();
    static {
        monthItem.put("January","01");
        monthItem.put("February","02");
        monthItem.put("March","03");
        monthItem.put("April","04");
        monthItem.put("May","05");
        monthItem.put("June","06");
        monthItem.put("July","07");
        monthItem.put("August","08");
        monthItem.put("September","09");
        monthItem.put("October","10");
        monthItem.put("November","11");
        monthItem.put("December","12");
    }

    public static String readAsString(InputStream in,String charsetNaem) throws IOException {
        BufferedReader r = new BufferedReader(new InputStreamReader(in,charsetNaem));
        StringBuffer b = new StringBuffer();

        while(true) {
            int ch = r.read();
            if (ch == -1) {
                in.close();
                r.close();
                return b.toString();
            }

            b.append((char)ch);
        }
    }


    public static String readAsString(String filePath) throws IOException{
        BufferedReader r = new BufferedReader(new FileReader(filePath));
        StringBuffer b = new StringBuffer();

        while (true){
            int ch = r.read();
            if(ch == -1){
                r.close();
                return b.toString();
            }
            b.append((char)ch);
        }
    }

    /**
     *  写文件
     * @param filePath
     * @param content
     * @throws IOException
     */
    public static void writeFile(String filePath,String content) throws IOException{
        if(filePath != null && content != null){
            File file = new File(filePath);
            File parentDir = file.getParentFile();

            if(parentDir.exists() || parentDir.mkdirs()){
                Writer writer = null;
                try {
                    writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filePath),"UTF-8"));
                    writer.write(content);
                }finally {
                    writer.close();
                }
            }
        }
    }

    /**
     *
     * @param conten
     * @param regex
     * @param regIndex 表示正则的规则括号
     * @return
     */
    public static String getRegContent(String conten,String regex,int regIndex){
        if(conten == null || regex == null){
            return "";
        }
        if(conten.isEmpty() || regex.isEmpty()){
            return "";
        }
        if(regIndex < 0){
            return "";
        }

        Pattern p = Pattern.compile(regex);
        Matcher matcher = p.matcher(conten);
        while (matcher.find()){
            return matcher.group(regIndex);
        }
        return "";
    }

    public static List<String> getRegContentList(String conten,String regex){
        if(conten == null || regex == null){
            return null;
        }else if (conten.isEmpty() || regex.isEmpty()){
            return null;
        }

        Pattern p = Pattern.compile(regex);
        Matcher matcher = p.matcher(conten);
        List<String> data = new ArrayList<String>();
        while (matcher.find()){
            String tmp = matcher.group();
            if(!data.contains(tmp)){
                data.add(tmp);
            }
        }
        return data;
    }

    /**
     * 构造格式化的URL链接
     * @param oriUrl
     * @return
     */
    public static String getFormatUrl(String oriUrl){
        if(oriUrl != null && !oriUrl.isEmpty()){
            if(oriUrl.matches("(http:|https:){0,1}(//)*(.*?)\\.(.*)/*")){
                if(oriUrl.startsWith("//")){
                    return "https:"+oriUrl;
                }else if(oriUrl.startsWith("http")){
                    return oriUrl;
                }else {
                    return "https://"+oriUrl;
                }
            }else {
                System.out.println("ori url="+oriUrl);
                return oriUrl;
            }
        }else {
            return oriUrl;
        }
    }

    /**
     * 格式化整数
     * @param oriStr
     * @return
     */
    public static String getFormatInteger(String oriStr){
        if(oriStr != null ){
            if(oriStr.isEmpty()){
                return "0";
            }else {
                return oriStr.replace(",","");
            }
        }else {
            return "0";
        }
    }

    /**
     * 提取邮箱帐号
     * @param oriMailStr    mailto:android-support@fb.com
     * @return
     */
    public static String getMail(String oriMailStr){
        if(oriMailStr != null){
            return oriMailStr.replace("mailto:","");
        }else {
            return oriMailStr;
        }
    }

    /**
     * 构建附带请求语言参数的URL &hl=en
     * @param oriUrl    原始url
     * @param langType  语言类型，如 hl=en ,hl=zh
     * @return
     */
    public static String buildGooglePlayUrlWithLang(String oriUrl,String langType){
        if(oriUrl != null && langType != null){
            if(oriUrl.matches("(.*)&hl=(.*)(&.*=.*)*")){
                String beforeUrl = ContenUtils.getRegContent(oriUrl,"(.*)&hl=(.*)(&.*=.*)*",1);
                return beforeUrl+"&hl="+langType;
            }else {
                return oriUrl+"&hl="+langType;
            }
        }else {
            return null;
        }
    }

    /**
     * 构造发布时期  20180321
     * @param dateStr March 21, 2018
     * @return
     */
    public static String getAppPublishDate(String dateStr){
        if(dateStr != null && dateStr.trim().matches("[a-zA-Z]+\\s*\\d+,\\s*\\d{4}")){
            String[] metaDataArray = dateStr.split(",");
            String[] monthDay = metaDataArray[0].split("\\s+");

            String month = monthDay[0].trim();
            String day = monthDay[1].trim();
            String year = metaDataArray[1].trim(); //不变

            month = monthItem.get(month);
            day = day.length()>1 ? day: "0"+day;
            return year+month+day;
        }else {
            return dateStr;
        }
    }

    public static String getAppId(String url){
        return getPackageName(url,"details\\?id=(.*)");
    }

//    https://dl.coolapk.com/down?pn=kh.android.dir&id=MTI4Mjk3&h=def8b9e3p6k61x&from=click
    public static String getPackageNameCoolApk(String url){
        return getPackageName(url,"down\\?pn=(.*)");
    }

    private static String getPackageName(String url,String regexStr){
        if(url != null && regexStr != null){
            String tmpUrl = url;
            if(url.contains("&")){
                String[] dataArray = url.split("&");
                tmpUrl = dataArray[0];
            }else {
                // do nothing
            }
            return getRegContent(tmpUrl,regexStr,1);
        }
        return null;
    }

    /**
     * 替换中文，编码中文，用在URL请求中含有中文
     * @param str   字符串
     * @param charset   字符集
     * @return  编码好的字符串
     * @throws UnsupportedEncodingException
     */
    public static String encodingChinese(String str,String charset) throws UnsupportedEncodingException{
        Pattern p = Pattern.compile(zhPatter);
        Matcher m = p.matcher(str);
        StringBuffer b = new StringBuffer();
        while (m.find()){
            m.appendReplacement(b, URLEncoder.encode(m.group(0),charset));
        }
        m.appendTail(b);
        return b.toString();
    }

    public static String getNowTime(){
        Date date = new Date();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return dateFormat.format(date);
    }

    public static void main(String[] args) throws Exception{
//        String ddd = "href=\"./W020180326366881142960.xls\" OLDSRC";
//        String test = getRegContent(ddd,"href\\s*=\\s*\"(.*)\"\\s*OLDSRC",1);
//        System.out.println(test);

//        System.out.println(getFormatUrl("//asdf.com"));
//        System.out.println(getFormatUrl("asdf.com/asdf"));
//        System.out.println(getFormatUrl("http://asdf.com/11"));
//        System.out.println(getFormatUrl("https://asdf.com"));
//
//        System.out.println(getAppPublishDate("March 21, 2018"));

        String ru = "https://play.google.com/store/apps/details?id=com.facebook.orca&aaa=aaa&hl=zh&asdf=aa";
//        System.out.println(buildGooglePlayUrlWithLang(ru,"en"));
//        System.out.println(getAppId(ru));
//        System.out.println(getPackageNameCoolApk("https://dl.coolapk.com/down?pn=kh.android.dir&id=MTI4Mjk3&h=def8b9e3p6k61x&from=click"));
        String ss = "https://www.coolapk.com/apk/tag/壁纸?p=14";
        System.out.println(encodingChinese(ss,"UTF-8"));


    }
}

