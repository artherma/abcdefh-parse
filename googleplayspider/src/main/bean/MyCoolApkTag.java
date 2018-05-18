package main.bean;

import net.sf.json.JSONObject;

/************
 * 定义的tag,包括名称和链接地址
 *************/
public class MyCoolApkTag {
    private String FT_NAME;
    private String FT_URL;
    private String ST_NAME;
    private String ST_URL;

    public String getFT_NAME() {
        return FT_NAME;
    }

    public void setFT_NAME(String FT_NAME) {
        this.FT_NAME = FT_NAME;
    }

    public String getFT_URL() {
        return FT_URL;
    }

    public void setFT_URL(String FT_URL) {
        this.FT_URL = FT_URL;
    }

    public String getST_NAME() {
        return ST_NAME;
    }

    public void setST_NAME(String ST_NAME) {
        this.ST_NAME = ST_NAME;
    }

    public String getST_URL() {
        return ST_URL;
    }

    public void setST_URL(String ST_URL) {
        this.ST_URL = ST_URL;
    }

    public static void main(String[] args) {
        MyCoolApkTag mct = new MyCoolApkTag();
        mct.setFT_NAME("asdf");
        mct.setFT_URL("asdf");
        mct.setST_NAME("lll");
        mct.setST_URL("asdf");

        JSONObject jsonObject = JSONObject.fromObject(mct);
        System.out.println(jsonObject.toString());
    }
}
