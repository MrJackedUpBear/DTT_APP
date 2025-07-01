package database;

public class Database {
	private static Database db = new Database();
	
	public static Database getInstance() {
		return db;
	}
	
	private Database() {
		try {
	        Class.forName("org.mariadb.jdbc.Driver");
	        System.out.println("Successfully loaded driver.");
	    } catch (ClassNotFoundException e) {
	        System.out.println("Error loading driver: " + e.getMessage());
	    }
	}
}
