package main.parse;

/**
 * apk的方法调用中 的方法类
 * 方法所属的类、方法的传入参数、方法的返回值类型
 */
public class MethodItem {


    /*****对应的apk_store_info表中的id****/
    private int recordId;

    public int getRecordId() {
        return recordId;
    }

    public void setRecordId(int recordId) {
        this.recordId = recordId;
    }

    /*****方法所属的 apk的版本号***/
    private String  apkVersion = "";

    /*****方法所属的 apk的名称，例如：com.abac.ad_demo.apk***/
    private String appName = "";

    /*****方法id ****/
    private long methodId;
    /*****方法的的类型，如自身，Java，Android，第三方，own***/
    private String type = "iamnotsure";

    /*****方法的 package name***/
    private String packageName = "";

    /*****方法所属的类的名字 ****/
    private String methodClassName = "";

    /*****方法的名称****/
    private String methodName = "";

    /*****方法的参数 String表示****/
    private String methodParams = "";

    /*****方法的返回值类型***/
    private String returyType ;


    public long getMethodId() {
        return methodId;
    }

    public void setMethodId(long methodId) {
        this.methodId = methodId;
    }

    public String getMethodClassName() {
        return methodClassName;
    }

    public void setMethodClassName(String methodClassName) {
        this.methodClassName = methodClassName;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public String getMethodParams() {
        return methodParams;
    }

    public void setMethodParams(String methodParams) {
        this.methodParams = methodParams;
    }

    public String getReturyType() {
        return returyType;
    }

    public void setReturyType(String returyType) {
        this.returyType = returyType;
    }

    public String getApkVersion() {
        return apkVersion;
    }

    public void setApkVersion(String apkVersion) {
        this.apkVersion = apkVersion;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public static void main(String[] args) {
        MethodItem mi = new MethodItem();
        System.out.println(mi.packageName);
    }

    @Override
    public String toString() {
        return "MethodItem{" +
                "methodId=" + methodId +
                ", methodClassName='" + methodClassName + '\'' +
                ", methodName='" + methodName + '\'' +
                ", methodParams='" + methodParams + '\'' +
                ", returyType='" + returyType + '\'' +
                ", apkVersion='" + apkVersion + '\'' +
                ", appName='" + appName + '\'' +
                ", type='" + type + '\'' +
                ", packageName='" + packageName + '\'' +
                '}';
    }
}
