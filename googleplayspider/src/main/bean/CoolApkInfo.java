package main.bean;

public class CoolApkInfo {
    private String packageName;
    private String appName;
    private String version;
    private String appFileSizeShowInPage;
    private String description;
    private String mainTag;
    private String nextTag;
    private String otherTag;
    private String permission;
    private String downloadNum;
    private String focusUserNum;
    private String language;
    private String apkUrl;
    private String newVersionDes;
    private String score;
    private String scoreUserNum;
    private String updateTime;
    private String supportRom;
    private String develop;
    private boolean hasDownload;


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

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getAppFileSizeShowInPage() {
        return appFileSizeShowInPage;
    }

    public void setAppFileSizeShowInPage(String appFileSizeShowInPage) {
        this.appFileSizeShowInPage = appFileSizeShowInPage;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getMainTag() {
        return mainTag;
    }

    public void setMainTag(String mainTag) {
        this.mainTag = mainTag;
    }

    public String getNextTag() {
        return nextTag;
    }

    public void setNextTag(String nextTag) {
        this.nextTag = nextTag;
    }

    public String getPermission() {
        return permission;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }


    public boolean isHasDownload() {
        return hasDownload;
    }

    public void setHasDownload(boolean hasDownload) {
        this.hasDownload = hasDownload;
    }

    public String getOtherTag() {
        return otherTag;
    }

    public void setOtherTag(String otherTag) {
        this.otherTag = otherTag;
    }

    public String getDownloadNum() {
        return downloadNum;
    }

    public void setDownloadNum(String downloadNum) {
        this.downloadNum = downloadNum;
    }

    public String getFocusUserNum() {
        return focusUserNum;
    }

    public void setFocusUserNum(String focusUserNum) {
        this.focusUserNum = focusUserNum;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getApkUrl() {
        return apkUrl;
    }

    public void setApkUrl(String apkUrl) {
        this.apkUrl = apkUrl;
    }

    public String getNewVersionDes() {
        return newVersionDes;
    }

    public void setNewVersionDes(String newVersionDes) {
        this.newVersionDes = newVersionDes;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String getScoreUserNum() {
        return scoreUserNum;
    }

    public void setScoreUserNum(String scoreUserNum) {
        this.scoreUserNum = scoreUserNum;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public String getSupportRom() {
        return supportRom;
    }

    public void setSupportRom(String supportRom) {
        this.supportRom = supportRom;
    }

    public String getDevelop() {
        return develop;
    }

    public void setDevelop(String develop) {
        this.develop = develop;
    }

    @Override
    public String toString() {
        return "CoolApkInfo{" +
                "packageName='" + packageName + '\'' +
                ", appName='" + appName + '\'' +
                ", version='" + version + '\'' +
                ", appFileSizeShowInPage='" + appFileSizeShowInPage + '\'' +
                ", description='" + description + '\'' +
                ", mainTag='" + mainTag + '\'' +
                ", nextTag='" + nextTag + '\'' +
                ", otherTag='" + otherTag + '\'' +
                ", permission='" + permission + '\'' +
                ", downloadNum='" + downloadNum + '\'' +
                ", focusUserNum='" + focusUserNum + '\'' +
                ", language='" + language + '\'' +
                ", apkUrl='" + apkUrl + '\'' +
                ", newVersionDes='" + newVersionDes + '\'' +
                ", score='" + score + '\'' +
                ", scoreUserNum='" + scoreUserNum + '\'' +
                ", updateTime='" + updateTime + '\'' +
                ", supportRom='" + supportRom + '\'' +
                ", develop='" + develop + '\'' +
                ", hasDownload=" + hasDownload +
                '}';
    }
}
