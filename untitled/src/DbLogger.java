import java.net.InetAddress;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
import com.microsoft.sqlserver.jdbc.*;

public class DbLogger {

    Connection connection;

    static String Ip;

    public DbLogger(){

        if (Ip == null){

            Path path = FileSystems.getDefault().getPath("Db.txt");

            try {
                Ip = Files.readAllLines(path).get(0);
            }
            catch (Exception e){
                int a =5;
            }
        }

        String connectionString =
                "jdbc:sqlserver://"+ Ip + ";"
                        + "database=DbForJava;"
                        + "user=sa;"
                        + "password=sa0123Roma;"
                        + "trustServerCertificate=false;"
                        + "loginTimeout=30;";

        try {
            connection = DriverManager.getConnection(connectionString);

        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void Log(InetAddress ip, String message, long count){

        Log(ip.toString(), message, count);
    }

    public void Log(String ip, String message, long count){

        PreparedStatement ps = null;

        try {
            String insertSql = "INSERT INTO Logs (Ip, Message, Date, Count) VALUES "
                    + "(?, ?, ?, ?);";

            ps = connection.prepareStatement(insertSql);

            ps.setString(1, ip);
            ps.setString(2, message);
            ps.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
            ps.setLong(4, count);

            ps.execute();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void IntermediateLog(InetAddress ip, String message, long count){

        IntermediateLog(ip.toString(), message, count);
    }

    public void IntermediateLog(String ip, String message, long count){

        PreparedStatement ps = null;

        try {
            String insertSql = "INSERT INTO IntermediateLogs (Ip, Message, Date, Count) VALUES "
                    + "(?, ?, ?, ?);";

            ps = connection.prepareStatement(insertSql);

            ps.setString(1, ip);
            ps.setString(2, message);
            ps.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
            ps.setLong(4, count);

            ps.execute();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
