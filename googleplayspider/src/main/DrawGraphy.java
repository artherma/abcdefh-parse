package main;

import main.parse.SaveToMysql;
import main.util.ContenUtils;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;

/**
 * 画图类
 * 提取一个app 相关的调用关系
 *
 * 检索 分类相同 ，不同包名的app,method,
 *      或者同包名，不同版本号
 *
 *  method :HashMap<String,HashMap<String,String>>:
 *      key(apk_file_name+SPLITER+apk_version+SPLITER+mid)
 *      value(HashMap<String,String>)
 *
 *  invoke:HashMap<String,ArrayList<String>>
 *      key(callerApkName+SPLITER+callerApkVersion+SPLITER+callerApkMid)
 *      value(calleeApkName+SPLITER+calleeApkVersion+SPLITER+calleeApkMid)
 *  注意：控制层数
 */
public class DrawGraphy {
    public static final String SPLITER = "&&&&&";
    // 获取 method
    private static HashMap<String,HashMap<String,String>> getOneAppMethod(Connection connection,String apk_file_name,
                                                                          String apk_version){
        if(connection != null){
            String sql = "select * from method where apk_version  = ? and apk_name = ?";
            PreparedStatement preparedStatement = null;
            try {
                // key(apk_name&&&&&apk_version&&&&&mid) -value(hash<key,value> ) 返回的数据集
                HashMap<String,HashMap<String,String>> mapData = new HashMap<String,HashMap<String, String>>();

                preparedStatement = connection.prepareStatement(sql);
                preparedStatement.setString(1,apk_version);
                preparedStatement.setString(2,apk_file_name);

                ResultSet resultSet = preparedStatement.executeQuery();

                ResultSetMetaData rsm = resultSet.getMetaData();
                int columenSize = rsm.getColumnCount(); //列的大小
                while ( resultSet.next()){
//                    if(count > 10){
//                        break;
//                    }
                    HashMap<String,String> rowData = new HashMap<String, String>();
                    for (int i = 1; i <= columenSize ; i++) {
                        String name = rsm.getColumnName(i);
                        String value = resultSet.getString(i);
                        rowData.put(name,value);
                    }
                    String mid = resultSet.getString(4);
                    String createOwnKey = apk_file_name+SPLITER+apk_version+SPLITER+mid;
                    mapData.put(createOwnKey,rowData);
                }
                resultSet.close();
                preparedStatement.close();

//                @TODO 返回数据
                return mapData;
            }catch (SQLException e){
                e.printStackTrace();
            }

            return null;

        }else {
            System.out.println("数据库连接失败");
            return null;
        }
    }

    /**
     * 获取调用关系
     * @param connection
     * @param apk_file_name
     * @param apk_version
     * @return
     */
    private static HashMap<String,ArrayList<String>> getOneAppInvoke(Connection connection, String apk_file_name,
                                                             String apk_version){
        if(connection != null) {
            String sql = "select * from invoke where caller_apk_name  = ? and caller_apk_version = ?";

            PreparedStatement preparedStatement = null;
            // key(caller_apk_name&&&&&apk_version_mid)-value(HashMap<key,value>)
            HashMap<String,ArrayList<String>> myInvokeData = new HashMap<String, ArrayList<String>>();

            try {
                preparedStatement = connection.prepareStatement(sql);
                preparedStatement.setString(1,apk_file_name);
                preparedStatement.setString(2,apk_version);
                ResultSet resultSet = preparedStatement.executeQuery();
//                int column = resultSet.getMetaData().getColumnCount();
                while (resultSet.next()){
                    String callerApkName = resultSet.getString(1);
                    String callerApkVersion = resultSet.getString(2);
                    String callerApkMid = resultSet.getString(3);


                    String calleeApkName = resultSet.getString(4);
                    String calleeApkVersion = resultSet.getString(5);
                    String calleeApkMid = resultSet.getString(6);

                    String tmpKey = callerApkName+SPLITER+callerApkVersion+SPLITER+callerApkMid;
                    String tmpValue = calleeApkName+SPLITER+calleeApkVersion+SPLITER+calleeApkMid;
                    if(myInvokeData.containsKey(tmpKey)){
                        ArrayList<String> tmpArray = myInvokeData.get(tmpKey);
                        tmpArray.add(tmpValue);
                        myInvokeData.replace(tmpKey,tmpArray);
                    }else {
                        ArrayList<String> newArray = new ArrayList<String>();
                        newArray.add(tmpValue);
                        myInvokeData.put(tmpKey,newArray);
                    }
                }
                return myInvokeData;
            }catch (SQLException e){
                e.printStackTrace();
                return null;
            }
        }else {
            return null;
        }
    }

    private static HashMap<String,String> getOneAppDetail(Connection connection,String apk_file_name,
                                                          String apk_version){
        if(connection != null) {
            String regPackageName = ContenUtils.getRegContent(apk_file_name,"(.*)-(\\d+)\\.apk$",1);
            String sql = "SELECT package_name,app_name,tag_info," +
                    "description,score,rate_num," +
                    "rate5,updated_time,current_version " +
                    "from apk_detail where package_name = ? ";
            PreparedStatement preparedStatement = null;
            HashMap<String,String> detailMap = new HashMap<String,String>();

            try {
                preparedStatement = connection.prepareStatement(sql);
                preparedStatement.setString(1,regPackageName);
                ResultSet resultSet = preparedStatement.executeQuery();
                String package_name = "";
                String app_name = "";
                String tag_info = "";
                String description = "";
                String score = "";
                String rate_num = "";
                String rate5 = "";
                String updated_time = "";
                String current_version = "";

                if(resultSet != null && resultSet.next()){
                    package_name = resultSet.getString(1);
                    app_name = resultSet.getString(2);
                    tag_info = resultSet.getString(3);
                    description = resultSet.getString(4);
                    score = resultSet.getString(5);
                    rate_num = resultSet.getString(6);
                    rate5 = resultSet.getString(7);
                    updated_time = resultSet.getString(8);
                    current_version = resultSet.getString(9);
                }
                detailMap.put("package_name",package_name);
                detailMap.put("app_name",app_name);
                detailMap.put("tag_info",tag_info);
                detailMap.put("description",description);
                detailMap.put("score",score);
                detailMap.put("rate_num",rate_num);
                detailMap.put("rate5",rate5);
                detailMap.put("updated_time",updated_time);
                detailMap.put("current_version",current_version);

                resultSet.close();
                return detailMap;

            }catch (SQLException e){
                e.printStackTrace();
            }
        }else {
            return  null;
        }

        return null;
    }


    // 筛选关系
    private static JSONObject getRelationJson(HashMap<String,HashMap<String,String>> refMethodData,
                                              HashMap<String,ArrayList<String>> refInvokeData,
                                              String apk_file_name,String apk_version){
        if(refInvokeData != null && refInvokeData.size() > 0 && refMethodData != null && refMethodData.size() > 0){
            // nodes, name,content
            // edges,source,target,relation
            JSONObject jsonObject = new JSONObject();
            // 找到部分关系，然后放到一个数组内，重新排序
//            从关系表中随机取一个
            int invokeSize = refInvokeData.size();
            Random random = new Random();
            int startNode = random.nextInt(invokeSize-10) + 5;

            System.out.println("startNode:"+startNode);

            // 控制层数，至多三层（假如碰到叶子节点的父节点，则共有两层），找
            int floor = 3;
            String startMethodKey = null;
            String tmpRandomStartNode = null;
            Iterator<String> iterator = refInvokeData.keySet().iterator();
            int stop = 0;
            while (iterator.hasNext()){
                tmpRandomStartNode = iterator.next();
                if(stop == startNode){
                    break;
                }
                stop++;
            }

            // 开始节点
            startMethodKey = tmpRandomStartNode;
            if(startMethodKey == null || !refMethodData.containsKey(startMethodKey)){
                return null;
            }

            // 构造json 中 nodes[]
            JSONArray nodesArray = new JSONArray();
            JSONArray edgesArray = new JSONArray();
            JSONObject rootNodeJson = new JSONObject();
            rootNodeJson.element("name",startMethodKey);
            rootNodeJson.element("content",JSONObject.fromObject(refMethodData.get(startMethodKey)));
            nodesArray.add(rootNodeJson);   //将开始节点加入

            // 构造json  假设不会出现圈
            int nodeCount = 1;
            ArrayList<String> secondNodesList = refInvokeData.get(startMethodKey);
            for (int i = 0; i < secondNodesList.size(); i++) {
                JSONObject tmpSecondNode = new JSONObject();
                String tmpKey = secondNodesList.get(i);
                tmpSecondNode.element("name",tmpKey);
                tmpSecondNode.element("content",JSONObject.fromObject(refMethodData.get(tmpKey)));
                nodesArray.add(tmpSecondNode);

                // rootNode 和 第二层 关系
                JSONObject tmpSecondInvoke = new JSONObject();
                tmpSecondInvoke.element("source",0);
                tmpSecondInvoke.element("target",nodeCount++);
                edgesArray.add(tmpSecondInvoke);
                //
            }

//            for (int i = 0; i < secondNodesList.size(); i++) {
//                String tmpKey = secondNodesList.get(i);
//
//            }



            jsonObject.element("nodes",nodesArray);
            jsonObject.element("edges",edgesArray);
            return jsonObject;
        }else {
            return null;
        }
    }

    // 获取某个app的关系图
    private static JSONObject getOneAppRelation(HashMap<String,HashMap<String,String>> refMethodData,
                                                HashMap<String,ArrayList<String>> refInvokeData,
                                                HashMap<String,String> refDatailData,
                                                String apk_file_name,String apk_version,boolean limit,int totalNodes){
        if(refMethodData != null && refInvokeData != null){
            JSONObject jsonObject = new JSONObject();
            JSONArray nodesArray = new JSONArray();
            JSONArray edgesArray = new JSONArray();
            // 重新排序的方法、序号映射
            HashMap<String,String> newOrderMap = new HashMap<String, String>();

//            取节点 nodes
            int newOrderMapSize = 0;
            Iterator<String> iterator = refMethodData.keySet().iterator();
            while (iterator.hasNext()){
                if(limit && (nodesArray.size() >= totalNodes)){
                    break;
                }
                String tmpNodeKey = iterator.next();
                if(!refInvokeData.containsKey(tmpNodeKey) || refInvokeData.get(tmpNodeKey).size() < 1){
                    continue;
                }

                JSONObject tmpNodeJO = new JSONObject();
                tmpNodeJO.element("name",tmpNodeKey);
                tmpNodeJO.element("content",JSONObject.fromObject(refMethodData.get(tmpNodeKey)));

                nodesArray.add(tmpNodeJO);  //加入
                newOrderMapSize = newOrderMap.size();
                if(!newOrderMap.containsKey(tmpNodeKey)){
                    newOrderMap.put(tmpNodeKey,newOrderMapSize+"");
                }
            }

            // 构造 edges
            Iterator<String> invokeIterator = refInvokeData.keySet().iterator();
            while (invokeIterator.hasNext()){
                 String tmpNodeKey = invokeIterator.next(); //父节点
                 ArrayList<String> tmpNodeChildList = refInvokeData.get(tmpNodeKey);// 子节点集合
                if(!newOrderMap.containsKey(tmpNodeKey)){
                    continue;
                }
                 for (int i = 0; i < tmpNodeChildList.size(); i++) {
                     String tmpChildKey = tmpNodeChildList.get(i); //子节点名称
                     String source = newOrderMap.get(tmpNodeKey);//取序号
                     if(!newOrderMap.containsKey(tmpChildKey)){ //不包括target，再次加入NodesArray and newOrderMap
                         JSONObject newTargetNotInNodesArray = new JSONObject();
                         newTargetNotInNodesArray.element("name",tmpChildKey);
                         newTargetNotInNodesArray.element("content",JSONObject.fromObject(refMethodData.get(tmpChildKey)));
                         // 再次加入NodesArray and newOrderMap
                         nodesArray.add(newTargetNotInNodesArray);
                         newOrderMapSize = newOrderMap.size();
                         newOrderMap.put(tmpChildKey,newOrderMapSize+"");
                     }

                     String target = newOrderMap.get(tmpChildKey);
                     // @TODO 如果自己调用自己，那么不加入到集合中
                     if(source.equals(target)){
                         continue;
                     }

                     JSONObject tmpEdgeJS =new JSONObject();
                     tmpEdgeJS.element("source",Integer.parseInt(source));
                     tmpEdgeJS.element("target",Integer.parseInt(target));
                     edgesArray.add(tmpEdgeJS);
                 }
            }

            // 合成 jsonObject
            jsonObject.element("nodes",nodesArray);
            jsonObject.element("edges",edgesArray);
            jsonObject.element("detail",JSONObject.fromObject(refDatailData));
            return jsonObject;
        }else {
            return null;
        }
    }



    public static void main(String[] args) {
        String apk_file_name = "eu.inmite.prj.ct.ct24.android-27641.apk";
        String apk_version = "1.6.0";
        Connection connection  = null;
        File methodFile = new File("E:\\parse\\oneMethod.txt");
        File invokeFile = new File("E:\\parse\\oneInvoke.txt");
        File graphyFile = new File("E:\\parse\\oneRelation.txt");

        HashMap<String,HashMap<String,String>> myMethodData = null;
        HashMap<String,ArrayList<String>> myInvokeData = null;
        HashMap<String,String> myDetailData = null;
        JSONObject graphyJson = null;
        try {
            connection = SaveToMysql.getDefaultConn();
            if(connection != null){
                connection.setAutoCommit(false);

                myMethodData = getOneAppMethod(connection,apk_file_name,apk_version);
                myInvokeData = getOneAppInvoke(connection,apk_file_name,apk_version);
                myDetailData = getOneAppDetail(connection,apk_file_name,apk_version);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        // 释放数据库连接
        SaveToMysql.releaseConn(connection);

        try {
            if(myMethodData != null){
                 FileUtils.writeStringToFile(methodFile,myMethodData.toString(),"UTF-8");
             }
             if(myInvokeData != null){
                FileUtils.writeStringToFile(invokeFile,myInvokeData.toString(),"utf-8");
            }

        }catch (IOException e){
            e.printStackTrace();
        }

        graphyJson = getOneAppRelation(myMethodData,myInvokeData,myDetailData,apk_file_name,apk_version,true,Integer.parseInt(args[0]));
        if(graphyJson != null){
            try {
                FileUtils.writeStringToFile(graphyFile,graphyJson.toString(),"utf-8");
//                System.out.println(graphyJson.toString());
            }catch (IOException e){
                e.printStackTrace();
            }
        }

//         System.out.println("method-size:"+myMethodData.size());
//         System.out.println("invoke-size:"+myInvokeData.size());
        System.out.println("node-size:"+graphyJson.getJSONArray("nodes").size());
        System.out.println("edges-size:"+graphyJson.getJSONArray("edges").size());

    }

}
