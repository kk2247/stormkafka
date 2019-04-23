package util;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
/**
 * @author KGZ
 * @date 2019/4/20 16:25
 */
public class MysqlUtil {
    private static final String DRIVER_NAME="jdbc:mysql://192.168.47.244:3306/storm?user=root&password=root";
    private static Connection connection;
    private static PreparedStatement pstm;
    private static ResultSet resultSet;

    public static Connection getConnection(){
        try {
            Class.forName("com.mysql.jdbc.Driver");
            connection=DriverManager.getConnection(DRIVER_NAME);
        }catch (Exception e){
            e.printStackTrace();
        }
        return connection;
    }
    public static void release(){
        try {
            if(resultSet!=null) {
                resultSet.close();
            }
            if (pstm != null) {
                pstm.close();
            }
            if(connection!=null){
                connection.close();
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if(connection!=null){
                connection=null;    //help GC
            }
        }
    }

}
