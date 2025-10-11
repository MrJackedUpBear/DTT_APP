package win.servername;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Connect {
	private static String user = System.getenv("DB_USER");
	private static String pass = System.getenv("DB_PASSWORD");
	private static String DB_URL = "jdbc:mariadb://localhost:3306/DTT_APP";
	
	public static Connection connect() throws SQLException {
        return DriverManager.getConnection(DB_URL, user, pass);
    }
}
