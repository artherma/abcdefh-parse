package main.util;

import main.bean.CoolApkInfo;
import main.bean.MyWeb;
import main.parse.ParseCoolApk;
import org.apache.commons.io.FileUtils;
import org.apache.http.*;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 封装HttpClient
 */
public class HttpUtil {

    private static final String APKSAVEPARENTPATH = "E:\\test2\\";

    public static CloseableHttpClient createDefaultClient(){
        CloseableHttpClient httpClient = null;
        HttpClientBuilder hb = HttpClients.custom();
        hb.setRedirectStrategy(new LaxRedirectStrategy());
        httpClient = hb.build();
        return httpClient;
    }

    /**
     * GET 请求
     * @param requestUrl
     * @param referUrl
     * @param cookie
     * @return
     */
    public static String doGet(String requestUrl,String referUrl,String cookie){

        /*********支持302跳转*********/
        CloseableHttpClient httpClient = HttpClients.custom()
                .setRedirectStrategy(new LaxRedirectStrategy())
                .build();
        try {
            HttpGet httpGet = new HttpGet(requestUrl);
            CloseableHttpResponse response = httpClient.execute(httpGet);

            if(response.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
                HttpEntity entity = response.getEntity();
                /*************记录cookie***********/
                Header[] cookieHeaders = response.getHeaders("Set-Cookie");
                List<Header> afterHeadCookieList = parseCookieHeaders(cookieHeaders);
                System.out.println(afterHeadCookieList.toString());

                String htmlSourceCode = EntityUtils.toString(entity);
                EntityUtils.consume(entity);
                return htmlSourceCode;
            }else {
                System.out.println("[url=" +requestUrl
                        +",statusCode=" +response.getStatusLine().getStatusCode()+ "]");
            }
        }catch (IOException e){
            e.printStackTrace();
        }finally {
            try {
                httpClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /********普通请求*****/
    public static void doPrimaryGet(CloseableHttpClient httpClient,MyWeb web){
        if(web != null && web.getRequestUrl() != null){
            System.out.println("\n\n=====================开始一次访问页面，并下载apk文件,url["+web.getRequestUrl().toString()+"]======================");
            try {
                HttpGet httpGet = new HttpGet(web.getRequestUrl().toExternalForm());
                processHttpConfig(web,httpGet);
                httpGet.setHeader("Accept-Encoding","gzip, deflate");

                CloseableHttpResponse response = httpClient.execute(httpGet);
                HttpEntity entity = response.getEntity();
                if(entity != null){
                    web.setResponseHeader(response.getAllHeaders());
                    web.setResponseStatusCode(response.getStatusLine().getStatusCode());
                    web.setResponseCookieHeader(response.getHeaders("Set-Cookie"));

                    String html = EntityUtils.toString(entity);
                    web.setSourceData(html);
                    EntityUtils.consume(entity);

                    /********预处理 判断是否 是正确页面*************/
                    if(html.contains("你访问的页面不存在或已删除")){
                        System.out.println("=============预判断为错误页面，停止继续访问，未下载文件==================\n\n");
                        return;
                    }

                    /**************
                     * 下载apk 文件
                     * *****/
                    String downloadUrl = ContenUtils.getRegContent(web.getSourceData(),"window.location.href\\s*=\\s*\"(.*)\";",1);
                    String ssid = generateCoolApkCookie(web.getResponseCookieHeader());

                    /************构建 保存APK的名称************/
                    CoolApkInfo coolApkInfo = ParseCoolApk.parseFromPage(html,downloadUrl);
                    if(coolApkInfo == null){
                        return;
                    }
                    String version = coolApkInfo.getVersion();
                    StringBuilder fileName = new StringBuilder(ContenUtils.getPackageNameCoolApk(downloadUrl));
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
                    String localDate = sdf.format(new Date());
                    fileName.append("-").append(version).append("-").append(localDate);

                    File file = new File(APKSAVEPARENTPATH + fileName.toString() + ".apk");
                    MyWeb downWen = new MyWeb();
                    downWen.setReferUr(web.getRequestUrl().toExternalForm());
                    downWen.setRequestHeaders(web.getRequestHeaders());
                    downWen.setRequestCookies(ssid);
                    downWen.setRequestUrl(new URL(downloadUrl));
                    downloadFile(httpClient,downWen,file);

                }else {
                    System.out.println("[url="+web.getRequestUrl().toString()+",statusCode="+response.getStatusLine().getStatusCode()+"]");
                }

                response.close();
            }catch (URISyntaxException e){
                e.printStackTrace();
            }catch(IOException e){
                e.printStackTrace();
            }
        }
    }

    private static void processHttpConfig(MyWeb web,HttpGet httpGet){
        if(web.getRequestCookies() != null && !web.getRequestCookies().isEmpty()){
            httpGet.setHeader("Set-Cookie",web.getRequestCookies());
        }

        if(web.getReferUr() != null && !web.getReferUr().isEmpty()){
            httpGet.setHeader("Referer",web.getReferUr());
        }

        if(web.getRequestHeaders() != null && web.getRequestHeaders().length > 0){
            httpGet.setHeaders(web.getRequestHeaders());
        }

    }


    public static String generateCoolApkCookie(Header[] responseCookieHeader){
        String ssid = null;
        List<Header> cookieList = parseCookieHeaders(responseCookieHeader);
        if(cookieList.size() > 0){
            String value = cookieList.get(0).getValue();
            ssid = value.split(";")[0];
//            System.out.println("cookie value = "+value);
//            System.out.println("ssid = "+ssid);
        }
        return ssid;
    }

    public static File downloadFile(CloseableHttpClient httpClient,
                                    MyWeb web,File desFile) throws URISyntaxException {
        if(web != null && web.getRequestUrl() != null){
            HttpGet httpGet = new HttpGet(web.getRequestUrl().toExternalForm());
            processHttpConfig(web,httpGet);
            httpGet.setHeader("Accept-Encoding","gzip, deflate, br");
            try {
                CloseableHttpResponse response = httpClient.execute(httpGet);
                HttpEntity entity = response.getEntity();
                if(response.getStatusLine().getStatusCode() == 302){
                    System.out.println("==========请求下载地址返回状态码 302============");
                    String location = response.getFirstHeader("Location").getValue();
                    System.out.println("localtion = "+location);
                    System.out.println("==========\r"+EntityUtils.toString(entity));

                    MyWeb newWeb = new MyWeb();
                    newWeb.setRequestUrl(new URL(location));
                    httpGet.setURI(newWeb.getRequestUrl().toURI());

                }else if(response.getStatusLine().getStatusCode() == 404){
                    System.out.println("==========请求下载地址返回状态码404=======");
                }else if(response.getStatusLine().getStatusCode() ==200){
                    System.out.println("==========请求下载地址返回状态码200==========");
                    FileUtils.copyInputStreamToFile(entity.getContent(),desFile);
                    System.out.println("==========写到文件完成 "+desFile.getAbsolutePath()+" ===========\n");
                    Thread.sleep(1*1000);
                }

            }catch (IOException e){
                e.printStackTrace();
            }catch (InterruptedException e){
                e.printStackTrace();
            }
        }
        return null;
    }

    public static void main(String[] args) throws MalformedURLException, IOException, URISyntaxException {
        CloseableHttpClient httpClient = createDefaultClient();

        try {
            httpClient.close();
        }catch (IOException e){
            e.printStackTrace();
        }

    }

    public static void downloadCoolApk(CloseableHttpClient httpClient,List<String> apkUrlList) throws MalformedURLException{
        Header[] headers = new Header[]{
                new BasicHeader("Upgrade-Insecure-Requests","1"),
                new BasicHeader("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8"),
                new BasicHeader("Accept-Language","zh-CN,zh;q=0.9,en-GB;q=0.8,en;q=0.7"),
                new BasicHeader("User-Agent","Mozilla/5.0 (Windows NT 6.3; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/65.0.3325.181 Safari/537.36")
        };
        if(apkUrlList != null ){
            for (String tmpUrl : apkUrlList) {
                MyWeb myWeb = new MyWeb();
                myWeb.setRequestHeaders(headers);
                myWeb.setRequestUrl(new URL(tmpUrl));
                doPrimaryGet(httpClient,myWeb);
            }
        }

    }


    public static String doGetWithProxy(String requestUrl,String referUrl,
                                        String cookie,Header[] headers){
        CloseableHttpClient httpClient = HttpClients.createDefault();
        try {
            HttpHost target = new HttpHost("google.com",443,"https");
            HttpHost proxy = new HttpHost("127.0.0.1",1080,"http");
            HttpGet request = new HttpGet(requestUrl);

            RequestConfig config = RequestConfig.custom()
                    .setProxy(proxy)
                    .build();
            request.setConfig(config);

            // 请求头
            if(headers != null && headers.length > 0){
                request.setHeaders(headers);
            }

//            请求情况
            System.out.println("执行request"+request.getRequestLine()+" to "+ target+" via "+ proxy);
            CloseableHttpResponse response = httpClient.execute(target,request);

            try {
                System.out.println("------------------------");
                if(response.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
                    String html = EntityUtils.toString(response.getEntity());
//                    System.out.println(html);
                    return html;
                }else {
                    return null;
                }
            }finally {
                response.close();
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            try {
                httpClient.close();
            }catch (IOException e){
                e.printStackTrace();
            }
        }

        return null;
    }


    /**
     * 处理cookie 中间环节，丢弃垃圾 cookie
     * @param headers
     * @return
     */
    private static List<Header> parseCookieHeaders(Header[] headers){
        List<Header> headerList = new ArrayList<Header>();
        for (Header tmpHeader : headers) {
            String name = tmpHeader.getName();
            String value = tmpHeader.getValue();
//            System.out.println(name+" : "+ value);

            if (tmpHeader.getValue().contains("expires") ) {
                String[] array = tmpHeader.getValue().split(";");
                /************默认是不需要删除的cookie******************/
                boolean isDeleteCookie  = false;
                for(String tmpStr: array){
                    if(tmpStr.contains("expires") && tmpStr.contains("1970")){
                        isDeleteCookie = true;
//                        System.out.println("垃圾cookie "+tmpHeader.toString());
                    }
                }
                if(!isDeleteCookie){
                    headerList.add(tmpHeader);
                }
            } else {
              headerList.add(tmpHeader);
            }
        }
        return headerList;
    }


    static class FileDownloadResponseHandle implements ResponseHandler<File>{
        private final File target;

        public FileDownloadResponseHandle(File target){
            this.target = target;
        }

        @Override
        public File handleResponse(HttpResponse response) throws IOException,ClientProtocolException {
            if(response != null && response.containsHeader("Content-Disposition")){
                String name = response.getFirstHeader("Content-Disposition").getValue();
                System.out.println("name="+name);
            }
            HttpEntity entity = response.getEntity();
            if(entity != null){
                InputStream source = entity.getContent();
                FileUtils.copyInputStreamToFile(source,this.target);
            }
            return this.target;
        }
    }

        /**
         * 测试Google Play
         */
    private static void testGooglePlay(){

//        String ru = "https://play.google.com/store/apps/details?id=com.facebook.orca&hl=en";
//        String ru = "https://play.google.com/store/apps/details?id=stericson.busybox&hl=en";
//        String ru = "https://play.google.com/store/apps/details?id=com.ustwo.monumentvalley2&&hl=en";
            String ru = "https://play.google.com/store/apps/details?id=com.martinmagni.mekorama&hl=en";
        Header[] headers = new Header[]{
                new BasicHeader("accept-encoding","gzip, deflate, br"),
                new BasicHeader("upgrade-insecure-requests","1"),
                new BasicHeader("accept","text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8"),
        };

        String gHtml = HttpUtil.doGetWithProxy(ru,null,
                null,headers);
        if(gHtml != null && !gHtml.isEmpty()){
            try {
                ContenUtils.writeFile("E:\\uhtml.html",gHtml);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else {
            System.out.println("获取失败");
        }
    }

    /*****一个简单的http get请求，大多数时候适用*******/
    public static String doASimpleGet(CloseableHttpClient httpClient,String requestUrl){
        if(requestUrl != null){
            HttpGet httpGet = new HttpGet(requestUrl);
            try {
                CloseableHttpResponse response = httpClient.execute(httpGet);
                HttpEntity entity = response.getEntity();
                if(response.getStatusLine().getStatusCode() == 200 && entity != null){
                    String html = EntityUtils.toString(entity);
                    return html;
                }else {
                    System.out.println("访问该url="+requestUrl+"\nstatuscode="+response.getStatusLine().getStatusCode());
                }
                response.close();
            }catch (IOException e){
                e.printStackTrace();
            }
        }
        return null;
    }

}
