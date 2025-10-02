package database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import logging.LogInfo;

public class TaskList {
	private static String createQuestion = "CREATE TABLE IF NOT EXISTS TaskList (TaskLetter CHAR(2) NOT NULL PRIMARY KEY, Description TEXT NOT NULL);";
	
	private static TaskList tl = new TaskList();
	
	public static TaskList getInstance() {
		return tl;
	}
	
	private TaskList() {
		try (Connection conn = Connect.connect()){
			Statement stmt = conn.createStatement();
			if (stmt.execute(createQuestion)) {
				System.out.println("Table created successfully.");
			}else {
				System.out.println("Table already exists.");
			}
		} catch (SQLException e) {
			System.out.println("Error establishing connection: " + e.getMessage());
		}
	}
	
	private Connection connect() throws SQLException {
        return Connect.connect();
    }
	
	String getTask(String tl, LogInfo logInfo) {
		String task = "";
		String sql = "SELECT Description FROM TaskList WHERE TaskLetter = ?;";
		
		try (Connection conn = connect();
				PreparedStatement pstmt = conn.prepareStatement(sql)){
			pstmt.setString(1, tl);
			ResultSet s = pstmt.executeQuery();
			
			while (s.next()) {
				task = s.getString(1);
			}
			
			logInfo.setLevel("Info");
			logInfo.setLogInfo("Successfully got task: " + task);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			System.out.println("Error: " + e.getMessage());
			logInfo.setLevel("Error");
			logInfo.setLogInfo("Error getting task: " + e.getStackTrace());
		}
		
		logInfo.addLog(logInfo);
		
		return task;
	}
}
