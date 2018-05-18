package main.util;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 文件名相关工具类
 */
public class FileNameUtil {

    /**
     * 从 url 中提取 app id
     * @param requestUrl
     * @return
     */
    public static String geneAppIdByUrl(String requestUrl){
        if(requestUrl != null && !requestUrl.isEmpty()){
            if(requestUrl.contains("&")){
                String[] sss = requestUrl.split("&");
                requestUrl = sss[0];
            }
            return ContenUtils.getRegContent(requestUrl,"id=(.*)",1);

        }else {
            return null;
        }
    }

    /**
     * 从URL 中 提取信息生成文件名，如 com.facebook.orca-20180326-205748
     * @param requestUrl
     * @return
     */
    public static String getFileNameByUrlAndDateTime(String requestUrl){
        if(requestUrl != null && !requestUrl.isEmpty()){
            StringBuilder stringBuilder = new StringBuilder(geneAppIdByUrl(requestUrl));
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd-HHmmss");
            stringBuilder.append("-").append(simpleDateFormat.format(new Date()));
            return stringBuilder.toString();
        }else {
            return null;
        }
    }


    /**
     *
     * @param oldPath
     * @return
     */
    public static List<File>  findFileByOldPath(String oldPath,String fileNameSelector){
        List<File> fileList = new ArrayList<File>();
        if(oldPath != null && !oldPath.isEmpty()){
            File file = new File(oldPath);
            if(file.exists() ){
                if(file.isDirectory()){
                    File[] files = file.listFiles();
                    for(File tmpFile: files){
                        String name = tmpFile.getName();
                        if(fileNameSelector != null && name.endsWith(fileNameSelector)){
                            fileList.add(tmpFile);
                            System.out.println("file name "+name);
                            System.out.println("file parent path "+tmpFile.getParent());
                            System.out.println("file own absolute path "+tmpFile.getAbsolutePath());
                            System.out.println(""+tmpFile.getPath());
                        }
                    }
                }else {
                    fileList.add(file);
                }
            }
        }
        return fileList;
    }


    public static void main(String[] args) {
//        String ru = "https://play.google.com/store/apps/details?id=com.facebook.orca&hl=en&asdf=aaa";
//        String ru1 = "https://play.google.com/store/apps/details?id=com.facebook.orca";
//
//        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd-HHmmss");
//        System.out.println(simpleDateFormat.format(new Date()));
//
//        System.out.println(getFileNameByUrlAndDateTime(ru));
//        System.out.println(getFileNameByUrlAndDateTime(ru1));

        String test = "D:\\tools\\gexf20180329\\gexf20180329";
        List<File> fileList = findFileByOldPath(test,"gexf");
        for (File tmpFile :
                fileList) {
            System.out.println(tmpFile.getAbsolutePath());
        }


    }
}
