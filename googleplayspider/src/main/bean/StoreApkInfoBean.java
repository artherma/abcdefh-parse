package main.bean;

/**
 * apk 存储相关的信息类
 */
public class StoreApkInfoBean {
    /******apk的文件名******/
    private String apkFileName;
    /*******apk的相对目录*****/
    private String apkFileDir;
    /********apk的包名****/
    private String apkPackageName;
    /********apk是否有gexf文件****/
    private String hasGexf;
    /*********apk是否有xml文件***/
    private String hasXml;

    private long gexfSize;
    private  long xmlSize;

    public long getGexfSize() {
        return gexfSize;
    }

    public void setGexfSize(long gexfSize) {
        this.gexfSize = gexfSize;
    }

    public long getXmlSize() {
        return xmlSize;
    }

    public void setXmlSize(long xmlSize) {
        this.xmlSize = xmlSize;
    }

    /*********apk是否有效***/
    private String valid;

    public String getApkFileName() {
        return apkFileName;
    }

    public void setApkFileName(String apkFileName) {
        this.apkFileName = apkFileName;
    }

    public String getApkFileDir() {
        return apkFileDir;
    }

    public void setApkFileDir(String apkFileDir) {
        this.apkFileDir = apkFileDir;
    }

    public String getApkPackageName() {
        return apkPackageName;
    }

    public void setApkPackageName(String apkPackageName) {
        this.apkPackageName = apkPackageName;
    }

    public String getHasGexf() {
        return hasGexf;
    }

    public void setHasGexf(String hasGexf) {
        this.hasGexf = hasGexf;
    }

    public String getHasXml() {
        return hasXml;
    }

    public void setHasXml(String hasXml) {
        this.hasXml = hasXml;
    }

    public String getValid() {
        return valid;
    }

    public void setValid(String valid) {
        this.valid = valid;
    }
}
