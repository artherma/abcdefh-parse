package main.bean;

import java.util.HashMap;

public class CoolApkTag {
    private String firstTagName;
    private String firstTagUrl;
    private HashMap<String,String> secondTag;

//    public CoolApkTag(String firstTagName,String firstTagUrl,HashMap<String,String> secondTag){
//        this.firstTagName = firstTagName;
//        this.firstTagUrl = firstTagUrl;
//        this.secondTag = secondTag;
//    }

    public String getFirstTagName() {
        return firstTagName;
    }

    public void setFirstTagName(String firstTagName) {
        this.firstTagName = firstTagName;
    }

    public String getFirstTagUrl() {
        return firstTagUrl;
    }

    public void setFirstTagUrl(String firstTagUrl) {
        this.firstTagUrl = firstTagUrl;
    }

    public HashMap<String, String> getSecondTag() {
        return secondTag;
    }

    public void setSecondTag(HashMap<String, String> secondTag) {
        this.secondTag = secondTag;
    }

    @Override
    public String toString() {
        return "CoolApkTag{" +
                "firstTagName='" + firstTagName + '\'' +
                ", firstTagUrl='" + firstTagUrl + '\'' +
                ", secondTag=" + secondTag.toString() +
                '}';
    }
}
