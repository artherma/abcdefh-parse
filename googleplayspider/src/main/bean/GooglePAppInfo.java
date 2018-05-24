package main.bean;

/**
 * 谷歌商店 app 对象类
 * 数据来源 curl 得到的html
 */
public class GooglePAppInfo {
    private String packageName;
    private String appName;
    private String devErName;
    private String devErUrl;
    private String tagInfos;
    private String tagInfoUrls;
    private String description;
    private String whatsNew;
    private String score;
    private int rateNum;
    private int rate5;
    private int rate4;
    private int rate3;
    private int rate2;
    private int rate1;
    private String Updated;
    private String Size;
    private String Installs;
    private String Current_Version;
    private String Requires_Android;
    private String Content_Rating;
    private String Interactive_Elements;
    private String In_app_Products;
    private String Offered_By;

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getDevErName() {
        return devErName;
    }

    public void setDevErName(String devErName) {
        this.devErName = devErName;
    }

    public String getDevErUrl() {
        return devErUrl;
    }

    public void setDevErUrl(String devErUrl) {
        this.devErUrl = devErUrl;
    }

    public String getTagInfos() {
        return tagInfos;
    }

    public void setTagInfos(String tagInfos) {
        this.tagInfos = tagInfos;
    }

    public String getTagInfoUrls() {
        return tagInfoUrls;
    }

    public void setTagInfoUrls(String tagInfoUrls) {
        this.tagInfoUrls = tagInfoUrls;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getWhatsNew() {
        return whatsNew;
    }

    public void setWhatsNew(String whatsNew) {
        this.whatsNew = whatsNew;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public int getRateNum() {
        return rateNum;
    }

    public void setRateNum(int rateNum) {
        this.rateNum = rateNum;
    }

    public int getRate5() {
        return rate5;
    }

    public void setRate5(int rate5) {
        this.rate5 = rate5;
    }

    public int getRate4() {
        return rate4;
    }

    public void setRate4(int rate4) {
        this.rate4 = rate4;
    }

    public int getRate3() {
        return rate3;
    }

    public void setRate3(int rate3) {
        this.rate3 = rate3;
    }

    public int getRate2() {
        return rate2;
    }

    public void setRate2(int rate2) {
        this.rate2 = rate2;
    }

    public int getRate1() {
        return rate1;
    }

    public void setRate1(int rate1) {
        this.rate1 = rate1;
    }

    public String getUpdated() {
        return Updated;
    }

    public void setUpdated(String updated) {
        Updated = updated;
    }

    public String getSize() {
        return Size;
    }

    public void setSize(String size) {
        Size = size;
    }

    public String getInstalls() {
        return Installs;
    }

    public void setInstalls(String installs) {
        Installs = installs;
    }

    public String getCurrent_Version() {
        return Current_Version;
    }

    public void setCurrent_Version(String current_Version) {
        Current_Version = current_Version;
    }

    public String getRequires_Android() {
        return Requires_Android;
    }

    public void setRequires_Android(String requires_Android) {
        Requires_Android = requires_Android;
    }

    public String getContent_Rating() {
        return Content_Rating;
    }

    public void setContent_Rating(String content_Rating) {
        Content_Rating = content_Rating;
    }

    public String getInteractive_Elements() {
        return Interactive_Elements;
    }

    public void setInteractive_Elements(String interactive_Elements) {
        Interactive_Elements = interactive_Elements;
    }

    public String getIn_app_Products() {
        return In_app_Products;
    }

    public void setIn_app_Products(String in_app_Products) {
        In_app_Products = in_app_Products;
    }

    public String getOffered_By() {
        return Offered_By;
    }

    public void setOffered_By(String offered_By) {
        Offered_By = offered_By;
    }

    @Override
    public String toString() {
        return "GooglePAppInfo{" +
                "packageName='" + packageName + '\'' +
                ", appName='" + appName + '\'' +
                ", devErName='" + devErName + '\'' +
                ", devErUrl='" + devErUrl + '\'' +
                ", tagInfos='" + tagInfos + '\'' +
                ", tagInfoUrls='" + tagInfoUrls + '\'' +
                ", description='" + description + '\'' +
                ", whatsNew='" + whatsNew + '\'' +
                ", score='" + score + '\'' +
                ", rateNum='" + rateNum + '\'' +
                ", rate5='" + rate5 + '\'' +
                ", rate4='" + rate4 + '\'' +
                ", rate3='" + rate3 + '\'' +
                ", rate2='" + rate2 + '\'' +
                ", rate1='" + rate1 + '\'' +
                ", Updated='" + Updated + '\'' +
                ", Size='" + Size + '\'' +
                ", Installs='" + Installs + '\'' +
                ", Current_Version='" + Current_Version + '\'' +
                ", Requires_Android='" + Requires_Android + '\'' +
                ", Content_Rating='" + Content_Rating + '\'' +
                ", Interactive_Elements='" + Interactive_Elements + '\'' +
                ", In_app_Products='" + In_app_Products + '\'' +
                ", Offered_By='" + Offered_By + '\'' +
                '}';
    }
}
