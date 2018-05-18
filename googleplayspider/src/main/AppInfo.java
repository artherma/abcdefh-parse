package main;

/**
 * APP详细信息
 */
public class AppInfo {

    // 包名，如com.facebook.orca
    private String id;

    //    google play app url
    private String playUrl;

//    app名称
    private String idAppTitle;

//    app 头像链接
    private String coverImageUrl;

//    开发者名字
    private String author;

//    类别，分类已经产生了
//    @TODO 维护一个全部的分类列表
    private String genre;

//    @TODO 维护列表
// 适合级别
    private String contentRatingTitle;

//    是否有屏幕缩略图
    private boolean hasScreenShot;

//    说明，介绍文字
    private String description;

//    评分是多少
    private double ratingValue;

//    参与评分的数量
    private int ratingCount;

//  5星评价的数量
    private int ratingCountFive;

//  4星评价的数量
    private int ratingCountFour;

//  3星评价的数量
    private int ratingCountThree;

//  2星评价的数量
    private int ratingCountTwo;

//  1星评价的数量
    private int ratingCountOne;

//    新版变化
    private String whatsNew;

//    更新日期
    private String datePublished;

//    文件大小
    private double fileSize;

//    安装次数下界
    private long numDownloadsLeft;

    //    安装次数上界
    private long numDownloadsRight;

//    当前版本
    private String softwareVersion;

//    Android系统版本要求
    private String operatingSystem;

//    应用内售价
    private String priceInApp;

//    是否是免费的
    private boolean isFree;

//    app售价
    private double offersPrice;

//    app 评分
    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

//    app 评分
    private double score;

    //    交互元素
    private String interactive;

//    开发者网站
    private String devWebSiteUrl;

    private String devEmail;

    private String privacyPolicyUrl;

    @Override
    public String toString() {
        return "AppInfo{" +
                "id='" + id + '\'' +
                ", playUrl='" + playUrl + '\'' +
                ", idAppTitle='" + idAppTitle + '\'' +
                ", coverImageUrl='" + coverImageUrl + '\'' +
                ", author='" + author + '\'' +
                ", genre='" + genre + '\'' +
                ", contentRatingTitle='" + contentRatingTitle + '\'' +
                ", hasScreenShot=" + hasScreenShot +
                ", description='" + description + '\'' +
                ", ratingValue=" + ratingValue +
                ", ratingCount=" + ratingCount +
                ", ratingCountFive=" + ratingCountFive +
                ", ratingCountFour=" + ratingCountFour +
                ", ratingCountThree=" + ratingCountThree +
                ", ratingCountTwo=" + ratingCountTwo +
                ", ratingCountOne=" + ratingCountOne +
                ", whatsNew='" + whatsNew + '\'' +
                ", datePublished='" + datePublished + '\'' +
                ", fileSize=" + fileSize +
                ", numDownloadsLeft=" + numDownloadsLeft +
                ", numDownloadsRight=" + numDownloadsRight +
                ", softwareVersion='" + softwareVersion + '\'' +
                ", operatingSystem='" + operatingSystem + '\'' +
                ", priceInApp='" + priceInApp + '\'' +
                ", isFree=" + isFree +
                ", offersPrice=" + offersPrice +
                ", score=" + score +
                ", interactive='" + interactive + '\'' +
                ", devWebSiteUrl='" + devWebSiteUrl + '\'' +
                ", devEmail='" + devEmail + '\'' +
                ", privacyPolicyUrl='" + privacyPolicyUrl + '\'' +
                ", devAddress='" + devAddress + '\'' +
                '}';
    }

    private String devAddress;

    public String getDevWebSiteUrl() {
        return devWebSiteUrl;
    }

    public void setDevWebSiteUrl(String devWebSiteUrl) {
        this.devWebSiteUrl = devWebSiteUrl;
    }

    public String getDevEmail() {
        return devEmail;
    }

    public void setDevEmail(String devEmail) {
        this.devEmail = devEmail;
    }

    public String getPrivacyPolicyUrl() {
        return privacyPolicyUrl;
    }

    public void setPrivacyPolicyUrl(String privacyPolicyUrl) {
        this.privacyPolicyUrl = privacyPolicyUrl;
    }

    public String getDevAddress() {
        return devAddress;
    }

    public void setDevAddress(String devAddress) {
        this.devAddress = devAddress;
    }


    public String getInteractive() {
        return interactive;
    }

    public void setInteractive(String interactive) {
        this.interactive = interactive;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPlayUrl() {
        return playUrl;
    }

    public void setPlayUrl(String playUrl) {
        this.playUrl = playUrl;
    }

    public String getIdAppTitle() {
        return idAppTitle;
    }

    public void setIdAppTitle(String idAppTitle) {
        this.idAppTitle = idAppTitle;
    }

    public String getCoverImageUrl() {
        return coverImageUrl;
    }

    public void setCoverImageUrl(String coverImageUrl) {
        this.coverImageUrl = coverImageUrl;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getContentRatingTitle() {
        return contentRatingTitle;
    }

    public void setContentRatingTitle(String contentRatingTitle) {
        this.contentRatingTitle = contentRatingTitle;
    }

    public boolean isHasScreenShot() {
        return hasScreenShot;
    }

    public void setHasScreenShot(boolean hasScreenShot) {
        this.hasScreenShot = hasScreenShot;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getRatingValue() {
        return ratingValue;
    }

    public void setRatingValue(double ratingValue) {
        this.ratingValue = ratingValue;
    }

    public int getRatingCount() {
        return ratingCount;
    }

    public void setRatingCount(int ratingCount) {
        this.ratingCount = ratingCount;
    }

    public int getRatingCountFive() {
        return ratingCountFive;
    }

    public void setRatingCountFive(int ratingCountFive) {
        this.ratingCountFive = ratingCountFive;
    }

    public int getRatingCountFour() {
        return ratingCountFour;
    }

    public void setRatingCountFour(int ratingCountFour) {
        this.ratingCountFour = ratingCountFour;
    }

    public int getRatingCountThree() {
        return ratingCountThree;
    }

    public void setRatingCountThree(int ratingCountThree) {
        this.ratingCountThree = ratingCountThree;
    }

    public int getRatingCountTwo() {
        return ratingCountTwo;
    }

    public void setRatingCountTwo(int ratingCountTwo) {
        this.ratingCountTwo = ratingCountTwo;
    }

    public int getRatingCountOne() {
        return ratingCountOne;
    }

    public void setRatingCountOne(int ratingCountOne) {
        this.ratingCountOne = ratingCountOne;
    }

    public String getWhatsNew() {
        return whatsNew;
    }

    public void setWhatsNew(String whatsNew) {
        this.whatsNew = whatsNew;
    }

    public String getDatePublished() {
        return datePublished;
    }

    public void setDatePublished(String datePublished) {
        this.datePublished = datePublished;
    }

    public double getFileSize() {
        return fileSize;
    }

    public void setFileSize(double fileSize) {
        this.fileSize = fileSize;
    }

    public long getNumDownloadsLeft() {
        return numDownloadsLeft;
    }

    public void setNumDownloadsLeft(long numDownloadsLeft) {
        this.numDownloadsLeft = numDownloadsLeft;
    }

    public long getNumDownloadsRight() {
        return numDownloadsRight;
    }

    public void setNumDownloadsRight(long numDownloadsRight) {
        this.numDownloadsRight = numDownloadsRight;
    }

    public String getSoftwareVersion() {
        return softwareVersion;
    }

    public void setSoftwareVersion(String softwareVersion) {
        this.softwareVersion = softwareVersion;
    }

    public String getOperatingSystem() {
        return operatingSystem;
    }

    public void setOperatingSystem(String operatingSystem) {
        this.operatingSystem = operatingSystem;
    }

    public String getPriceInApp() {
        return priceInApp;
    }

    public void setPriceInApp(String priceInApp) {
        this.priceInApp = priceInApp;
    }

    public boolean isFree() {
        return isFree;
    }

    public void setFree(boolean free) {
        isFree = free;
    }

    public double getOffersPrice() {
        return offersPrice;
    }

    public void setOffersPrice(double offersPrice) {
        this.offersPrice = offersPrice;
    }


}
