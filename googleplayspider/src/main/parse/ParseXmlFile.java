package main.parse;

import main.bean.MethodItem;
import main.util.ContenUtils;
import main.util.FileNameUtil;
import org.dom4j.*;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class ParseXmlFile {

    private static final int PERBATCH = 5000;

    public static void main(String[] args) {
//        createXml();
//        parseXmlDemo();
        parseManifestXml("E:\\gexf\\xml\\f8\\ab2.HP.age_bmi_breathcount-1.xml");
//        String apkName = "app-kuan";
//        String apkVersion = "test-1.x";
//        String packageName = "com.app-kuan";
//        String gexfPath = "D:\\tools\\gexf20180426\\app-kuan.txt";
////        String oldPath = "D:\\tools\\gexf20180329\\gexf20180329";
//        Connection connection = null;
//        try {
//            connection = SaveToMysql.getDefaultConn();
//            connection.setAutoCommit(false);
//
//            parseXmlGexf(gexfPath, connection,
//                    apkVersion,apkName,packageName);
////            commProcess(oldPath,connection);
//
//        }catch (SQLException e){
//            e.printStackTrace();
//        }finally {
//            try {
//                if(!connection.isClosed()){
//                    connection.close();
//                }
//            }catch (SQLException e){
//                System.out.println("数据库连接关闭失败");
//                e.printStackTrace();
//            }
//        }
    }

    public static void commProcess(String oldPath,Connection connection){
        List<File> fileList = FileNameUtil.findFileByOldPath(oldPath,"gexf");
        if(fileList.size() > 0){
            for (File tmpFile : fileList) {
                String fileName = tmpFile.getName();
                String apkName = ContenUtils.getRegContent(fileName,"(.*)\\.gexf",1);
                String apkVersion = apkName+"-1.x";
                String packageName = "com."+apkName;


                try {
                    System.out.println("=======开始处理====="+fileName+"===========");
                    parseXmlGexf(tmpFile.getAbsolutePath(),connection,apkVersion,apkName,"");
                    System.out.println("=======处理完成====="+fileName+"===========");
                }catch (SQLException e){
                    e.printStackTrace();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 处理gexf文件
     * @param filePath
     * @param connection
     * @param apkVersion
     * @param apkName
     * @throws SQLException
     */
    public static void parseXmlGexf(String filePath,Connection connection,
                                    String apkVersion,String apkName,String recordId) throws SQLException{
        SAXReader reader = new SAXReader();
        File file = new File(filePath);
        try {
            Document document = reader.read(file);
            Element root = document.getRootElement();   // gexf
//            迭代方法处理
            Iterator<Element> elementIterator = root.elementIterator();
            while (elementIterator.hasNext()){
                Element tmpEle = elementIterator.next();
                if(tmpEle.getName().equals("graph")){   // graph
                    Iterator<Element> graphChileEle = tmpEle.elementIterator();
                    while (graphChileEle.hasNext()){
                        Element element = graphChileEle.next();
                        if(element.getName().equals("nodes")){  // 每个方法的一些信息，如
//                            @TODO 处理方法
                            System.out.println("apk文件["+apkName+"]");
                            parseNodes(element,connection,apkVersion,apkName,recordId);
                        }else if(element.getName().equals("edges")){
//                            @TODO 处理调用关系
                            System.out.println("apk文件["+apkName+"]");
                            paseEdges(element,connection,apkVersion,apkName);
                        }
                    }
                }

            }
        }catch (DocumentException e){
            e.printStackTrace();
            StoreApkGexf.commonErrorLog(new File(StoreApkGexf.error_log),e.getMessage()+"\n");
        }

    }

    /**
     * 处理Nodes
     * @param element   Nodes
     * @param connection    数据库连接
     * @param apkVersion    apk版本
     * @param apkName   apk名称
     * @throws SQLException
     */
    private static void parseNodes(Element element, Connection connection,
                                   String apkVersion,String apkName,String recordId){
        if(element != null && connection != null){
            long counter = 0;
            int batchCounter = 0;
            try {
                String sql = "INSERT  INTO  method(record_id,apk_version,apk_name,mid,type," +
                        "package_name,class_name,method_name,parameter,return_type) " +
                        "VALUES (?,?,?,?,?," +
                        "?,?,?,?,?);";

                PreparedStatement statement = connection.prepareStatement(sql);

                Iterator<Element> nodeElements = element.elementIterator(); //处理 node
                while (nodeElements.hasNext()){
                    Element tmpNode = nodeElements.next();  //迭代node

                    MethodItem methodItem = geneMethodItem(tmpNode);

                    // 重新设定值
                    methodItem.setApkVersion(apkVersion);
                    methodItem.setAppName(apkName);
                    methodItem.setRecordId(Integer.parseInt(recordId));

    //                数据库操作
                    statement.setString(1,methodItem.getRecordId()+"");
                    statement.setString(2,methodItem.getApkVersion());
                    statement.setString(3,methodItem.getAppName());
                    statement.setLong(4,methodItem.getMethodId());
                    statement.setString(5,methodItem.getType());
                    statement.setString(6,methodItem.getPackageName());
                    statement.setString(7,methodItem.getMethodClassName());
                    statement.setString(8,methodItem.getMethodName());
                    statement.setString(9,methodItem.getMethodParams());
                    statement.setString(10,methodItem.getReturyType());

    //                @TODO 用于测试参数长度 导致的插入不成功问题
    //                if(methodItem.getMethodParams().length() > maxParamsLength ){
    //                    System.out.println(methodItem.toString());
    //                }

                    statement.addBatch();
                    counter++;
                    if(counter % PERBATCH == 0){
                        statement.executeBatch();
                        connection.commit();
                        System.out.println("============Node Batch "+(batchCounter++) +" executed successful=========");
                    }

                }
    //            最后一批
                statement.executeBatch();
                connection.commit();
                System.out.println("===========Node last batch "+batchCounter+" executed successful========");

                statement.close();

            }catch (SQLException e){
                e.printStackTrace();
                String error = e.getMessage()+"\n"+"apk文件["+apkName+"]\n";
                StoreApkGexf.commonErrorLog(new File(StoreApkGexf.error_log),error);
                System.out.println("数据库表method插入出现异常，apk文件["+apkName+"]");
            }

        } else {
            System.out.println("插入节点时element为null，或数据库连接异常！！");
        }

    }


    private static MethodItem geneMethodItem(Element elementNode){
        if(elementNode != null){
            MethodItem methodItem = new MethodItem();
            String methodPackageName="";
            String methodClassName = "";
            String methodName = "";
            String methodParms = "";
            String returnType = null;

            String methodParamsAndReturnType = "";

            long methodId = Long.parseLong(elementNode.attribute("id").getValue()); // nodes-id
            Element attvaluesEle = elementNode.element("attvalues");    // nodes-attvalues
            List<Element> attvalueList = attvaluesEle.elements();
            for(Element element1: attvalueList){
                String element1Id = element1.attributeValue("id");
                String value = element1.attributeValue("value");
                if(element1Id.equals("1")){ //class_name:string;method_package_name:String;
                    // @TODO
                    methodClassName = value.replace(";","");
                    int lastSign = value.lastIndexOf("/");
                    if(value.length() > 0 && lastSign > 0){
                        methodPackageName = value.substring(0,lastSign);
                    }
                    // method type

                }else if(element1Id.equals("2")){ //method_name:string
                    methodName = value;
                } else if(element1Id.equals("3")){ //descriptor:string
                    methodParamsAndReturnType = value;
                }else if(element1Id.equals("4")){ //permissions:integer

                }else if(element1Id.equals("5")){ // permissions_level:string

                }else if(element1Id.equals("6")){ // dynamic_code:boolean

                }else if(element1Id.equals("0")){

                }else {
                    System.out.println("================spsp============methodId"+methodId);
                }
            }

            // 开始处理 @TODO 暂时不需要处理
//            if(methodClassName.startsWith("L") && methodClassName.length() > 1){
//                methodClassName = methodClassName.substring(1);
//            }else {
//                System.out.println("========spsp====未考虑到的方法所属的类名===="+methodClassName);
//            }
//
//            if(methodName.contains("&lt;") && methodName.contains("&gt;")){
//                methodName = methodName.replace("&gt;",">").replace("&lt;","<");
//            }else if(methodName.contains("&amp;")){
//                methodName.replaceAll("&amp;","&");
//            }else {
////                System.out.println("=========未考虑到的特殊方法名,id="+methodId +",methoName="+methodName+"===========");
//            }

//            参数和返回值
            if(methodParamsAndReturnType.contains(")")){
                methodParms = methodParamsAndReturnType.split("\\)")[0].replace("(","").trim();
                returnType = methodParamsAndReturnType.split("\\)")[1].replace(";","").trim();
                // @TODO 处理传入参数和返回值
//                methodParms = getParams(methodParms);
//                returnType = getReturn(returnType);
            }else {
                System.out.println("=====spsp====未考虑到的参数和返回值类型========methodId"+methodId+methodParamsAndReturnType);
            }

            methodItem.setMethodId(methodId);
            methodItem.setPackageName(methodPackageName.trim());
            methodItem.setMethodClassName(methodClassName.trim());
            methodItem.setMethodName(methodName.trim());
            methodItem.setMethodParams(methodParms.trim());
            methodItem.setReturyType(returnType.trim());

            return methodItem;
        }else { // null
            return null;
        }
    }


    // 方法签名的传入参数处理
    private static String getParams(String methodParms){
        if(methodParms != null){
            methodParms = methodParms.trim();
            StringBuilder stringBuilder = new StringBuilder("");
            if(methodParms.contains(";")){
    //                    构建一个新的参数列表
                String[] items = methodParms.replace("(","").split(";");
                for(String tmpItem: items){
                    if(!tmpItem.isEmpty()){
                        stringBuilder.append(getParamsType(tmpItem)).append(",");
                    }
                }
            }else {
                String[] items = methodParms.split("\\s");
                for(String tmpItem: items){
                    if(!tmpItem.isEmpty()){
                        stringBuilder.append(getParamsType(tmpItem.trim())).append(",");
                    }
                }
            }
            methodParms = stringBuilder.toString();
            if(methodParms.length() > 0 && methodParms.contains(",")){
                methodParms = methodParms.substring(0,methodParms.lastIndexOf(","));
            }
        }
        return methodParms;
    }

//        返回值类型处理
    private static String getReturn(String returnType){
        if(returnType != null){
            if(returnType.startsWith("L")){
                if(returnType.length() > 1){
                    returnType = getParamsType(returnType.substring(1));
                }else {
                    System.out.println("=========spsp====未考虑到的returnType=========="+returnType);
                }
            }else if(returnType.startsWith("[")){ // [Lu/aly/ct
                returnType = getParamsType(returnType);
            }else  if(returnType.equals("V")){
                returnType = "void";
            }else {
                System.out.println("=========spsp====未考虑到的returnType=========="+returnType);
            }
        }
        return returnType;
    }

    // 特殊处理参数和返回值
    private static String getParamsType(String tmpItem){
        if(tmpItem != null){
            tmpItem = tmpItem.trim();
            if(tmpItem.startsWith("L") && tmpItem.length() > 1){
                tmpItem = tmpItem.substring(1);
            }

            if(tmpItem.equals("B")){
                tmpItem = "byte";
            }else if(tmpItem.equals("C")){
                tmpItem = "char";
            }else if(tmpItem.equals("D")){
                tmpItem = "double";
            }else if(tmpItem.equals("F")){
                tmpItem = "float";
            }else if(tmpItem.equals("I")){
                tmpItem = "int";
            }else if(tmpItem.equals("J")){
                    tmpItem = "long";
            }else if(tmpItem.equals("S")){
                    tmpItem = "short";
            }else if(tmpItem.equals("Z")){
                    tmpItem = "boolean";
            }else if(tmpItem.startsWith("[")){
                tmpItem = getParamsType(tmpItem.substring(1))+"[]";
            }
            return tmpItem;
        }
        return null;
    }

    /**
     * 处理调用关系
     * @param element   edges
     */
    private static void paseEdges(Element element,Connection connection,
                                  String apkVersion,String apkName) {
        if(element != null && connection != null){ //edges
            Iterator<Element> elementIterator = element.elementIterator(); //edge

            long counter = 0;
            int batchCounter = 0;

            try {
                String sql = "Insert into invoke values (?,?,?," +
                        "?,?,?)";
                PreparedStatement statement = connection.prepareStatement(sql);

                while (elementIterator.hasNext()){
                    Element tmpEle = elementIterator.next(); //edge

                    long source = Long.parseLong(tmpEle.attributeValue("source"));
                    long target = Long.parseLong(tmpEle.attributeValue("target"));

                    statement.setString(1,apkName);
                    statement.setString(2,apkVersion);
                    statement.setLong(3,source);
                    statement.setString(4,apkName);
                    statement.setString(5,apkVersion);
                    statement.setLong(6,target);
                    statement.addBatch();
                    counter++;
                    if(counter % PERBATCH ==0){
                        statement.executeBatch();
                        connection.commit();
                        System.out.println("===========invoke batch "+(batchCounter++)+" execute successful=========");
                    }
                }
                statement.executeBatch();
                connection.commit();
                System.out.println("==========invoke batch "+(batchCounter)+" execute successful==========");

                statement.close();

            }catch (SQLException e){
                e.printStackTrace();
                String error = e.getMessage()+"\n"+"apk文件["+apkName+"]\n";
                StoreApkGexf.commonErrorLog(new File(StoreApkGexf.error_log),error);
                System.out.println("数据库表invoke插入出现异常，apk文件["+apkName+"]");
            }
        }else {
            System.out.println("插入调用关系时element为null，或数据库连接异常！！");
        }
    }

    public static void parseXmlDemo(){
        SAXReader reader = new SAXReader();
        File file = new File("E:\\books.xml");
        try {
            Document document = reader.read(file);
//            根元素
            Element root = document.getRootElement();
//            子元素 book
            List<Element> childElements = root.elements();
            for(Element tmpChild: childElements){
//                未知 子元素的属性
                List<Attribute> attributeList = tmpChild.attributes();
                for(Attribute tmpAttr: attributeList){
                    System.out.println(tmpAttr.getName()+": "+tmpAttr.getValue());
                }
//                未知子元素名称 ，如author ,title,address等
                List<Element> elementList = tmpChild.elements();
                for (Element element: elementList){
                    System.out.println(element.getName()+": "+element.getText());
                }

                System.out.println();

            }
        }catch (DocumentException e){
            e.printStackTrace();
        }
    }

    public static void createXml(){
        Document document = DocumentHelper.createDocument();
//        根元素
        Element books  = document.addElement("books");

//        子元素
        Element book1 = books.addElement("book");
        Element author = book1.addElement("author");
        Element address = book1.addElement("address");

        Element book2 = books.addElement("book");
        Element author2 = book2.addElement("author");
        Element title2 = book2.addElement("title");

//        为子节点添加属性
        book1.addAttribute("id","001");
        book2.addAttribute("id","002");

//        为元素添加内容
        author.setText("mashangzhao");
        address.setText("tinajin");

        author2.setText("zhaozhao");
        title2.setText("cscs");

        OutputFormat format = OutputFormat.createPrettyPrint();
        format.setEncoding("UTF-8");
        // 创建需要写入的File对象
        File file  = new File("E:"+File.separator+"books.xml");

        try {
            XMLWriter writer = new XMLWriter(new FileOutputStream(file),format);
            writer.write(document);
        }catch (FileNotFoundException e){
            e.printStackTrace();
        }catch (UnsupportedEncodingException e){
            e.printStackTrace();
        }catch (IOException e){
            e.printStackTrace();
        }

    }

    //解析apk的manifest.xml文件，获取version和packagename
    public static HashMap<String,String> parseManifestXml(String xmlFilePath){
        SAXReader reader = new SAXReader();
        File tmpXmlFile = new File(xmlFilePath);
        try {
            Document document = reader.read(tmpXmlFile);
            Element root = document.getRootElement();
            String version = root.attributeValue("versionName");
            String packageName = root.attributeValue("package");
            HashMap<String,String> resultMap = new HashMap<String,String>();
            resultMap.put("version",version);
            resultMap.put("packagename",packageName);
//            System.out.println("version="+version);
//            System.out.println("packagename="+packageName);

            return resultMap;
        }catch (DocumentException e){
            e.printStackTrace();
        }

        return null;
    }
}
