package main.parse;

import java.sql.*;

/**
 * 解析xml文件保存到数据库
 */
public class SaveToMysql {
    private static final String driver = "com.mysql.jdbc.Driver";
    private static final String url = "jdbc:mysql://127.0.0.1:3306/callgraphy?autoReconnect=true&useSSL=true";
    private static final String user = "root";
    private static final String password = "mashangzhao1";

    public static void main(String[] args) {
        Connection connection = null;
        Statement statement = null;

        try {
            connection = getConn(url,user,password);
//            不自动提交，也就是执行事务，如果事务中断，则进行回滚操作
            connection.setAutoCommit(false);
        }catch (Exception e){
            System.out.println("数据库连接失败");
            e.printStackTrace();
        }


//        insertSqlBatch(connection,null);
//        insertSql(connection,null);
        selectSql(connection,null);
        try {
            releaseConn(connection);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static boolean insertSqlBatch(Connection connection,String sql){
        String testSql = "insert into cg values (?,?,?,?)";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(testSql);
            int count = 1;
            for (int i = 0; i < 10000; i++) {
                preparedStatement.setString(1,"z"+i);
                preparedStatement.setString(2,"x"+i);
                preparedStatement.setString(3,"y"+i);
                preparedStatement.setInt(4,i+60);
//                添加到批量操作
                preparedStatement.addBatch();
//              每次执行1000条操作，分批执行
                if(i % 1000 == 0){
                    preparedStatement.executeBatch();
                    connection.commit();
                    System.out.println("Batch"+(count++)+ "executed successful");
                }
            }
//            执行最后一次批量操作
            preparedStatement.executeBatch();
            connection.commit();
            System.out.println("最后一次批量操作执行成功");
            return true;
        }catch (SQLException e){
            e.printStackTrace();
            if(connection != null){
                try {
                    System.out.println("事务需要被回滚");
                    connection.rollback();
                }catch (Exception e1){
                    e1.printStackTrace();
                }
            }
        }

        return false;
    }


    public static boolean insertSql(Connection connection,String sql){
        String testSql = "insert into cg values (?,?,?,?)";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(testSql);
            for (int i = 0; i < 5; i++) {
                preparedStatement.setString(1,"a"+i);
                preparedStatement.setString(2,"b"+i);
                preparedStatement.setString(3,"c"+i);
                preparedStatement.setInt(4,i+50);
                int total =  preparedStatement.executeUpdate();
//                注意，一定要提交
                connection.commit();
            }

            System.out.println("插入结束");
        }catch (SQLException e){
            e.printStackTrace();
        }

        return false;
    }

    public static void selectSql(Connection connection,String sql){
        String testSql = "select * from cg";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(testSql);
            ResultSet resultSet = preparedStatement.executeQuery();

            ResultSetMetaData rsm = resultSet.getMetaData();
//            查询结果有多少列，显示每个列的详细信息，如类型，名称，大小（varchar(10)）
            int columSize = rsm.getColumnCount();
            System.out.println("===============META DATA========");
            for (int i = 1; i <= columSize; i++) {
//                列的名称
                String name = rsm.getColumnName(i); // 名称
                String typeName = rsm.getColumnTypeName(i); //类型
                int size = rsm.getColumnDisplaySize(i);    //大小
                System.out.println("Column name="+name+"\ttype ="+typeName+"\tsize="+size);
            }
            System.out.println("=============RESULT============");
            while (resultSet.next()){
                System.out.println(resultSet.getString(1)+"\t"+resultSet.getString(2)+"\t"
                +resultSet.getString(3)+"\t"+resultSet.getString(4));
            }
        }catch (SQLException e){
            e.printStackTrace();
        }

    }


    public static Connection getConn(String url,String user,String password){
        Connection connection = null;
        try {
            Class.forName(driver);
            connection = DriverManager.getConnection(url,user,password);
            if(!connection.isClosed()){
                System.out.println("数据库连接成功");
            }
        }catch (ClassNotFoundException e){
            System.out.println("数据库连接失败");
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println("数据库连接失败");
            e.printStackTrace();
        }
        return connection;
    }

    public static Connection getDefaultConn(){
        Connection connection = null;
        try {
            Class.forName(driver);
            connection = DriverManager.getConnection(url,user,password);
            if(connection != null && !connection.isClosed()){
                System.out.println("数据库连接成功");
            }
        }catch (ClassNotFoundException e){
            System.out.println("数据库连接失败");
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println("数据库连接失败");
            e.printStackTrace();
        }
        return connection;
    }


    public static void releaseConn(Connection connection){
        try {
            if(connection != null && !connection.isClosed()){
                connection.close();
                System.out.println("数据库连接关闭");
            }else {
                System.out.println("数据库连接为null或已关闭");
            }
        }catch (SQLException e){
            System.out.println("数据库关闭失败");
            e.printStackTrace();
        }
    }


}
