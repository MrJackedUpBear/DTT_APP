package database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import logging.LogInfo;

public class ImageDB {
	private static String createQuestion = "CREATE TABLE IF NOT EXISTS Image (ImageId INT NOT NULL AUTO_INCREMENT, ImageLoc TEXT NOT NULL, QuestionId INT NOT NULL, PRIMARY KEY(ImageId));";
	
	private static ImageDB i = new ImageDB();
	
	public static ImageDB getInstance() {
		return i;
	}
	
	private ImageDB() {
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
	
	// CREATE
    public void addImage(Image image, LogInfo logInfo) {
        String sql = "INSERT INTO Image (ImageLoc, QuestionId) VALUES (?, ?)";
        try (Connection conn = Connect.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, image.getImageLoc());
            stmt.setInt(2, image.getQuestionId());
            stmt.executeUpdate();
            logInfo.setLogInfo("Successfully added image.");
            logInfo.setLevel("Info");
        } catch (SQLException e) {
            e.printStackTrace();
            logInfo.setLogInfo("Error adding image: " + String.valueOf(e.getStackTrace()));
            logInfo.setLevel("Error");
        }
        
        logInfo.addLog(logInfo);
    }

    // READ
    public Image getImage(int questionId, String imageLoc, LogInfo logInfo) {
        String sql = "SELECT * FROM Image WHERE QuestionId = ? AND ImageLoc=?";
        try (Connection conn = Connect.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, questionId);
            stmt.setString(2, imageLoc);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
            	logInfo.setLevel("Info");
            	logInfo.setLogInfo("Successfully added image: " + imageLoc);
            	logInfo.addLog(logInfo);
                return new Image(
                    rs.getInt("ImageId"),
                    rs.getString("ImageLoc"),
                    rs.getInt("QuestionId")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
            logInfo.setLevel("Error");
            logInfo.setLogInfo("Error adding image: " + e.getStackTrace());
            logInfo.addLog(logInfo);
        }
        return null;
    }
    
    public ArrayList<Image> getImages(int questionId, LogInfo logInfo){
    	String sql = "SELECT * FROM Image WHERE QuestionId = ?";
    	
    	ArrayList<Image> images = new ArrayList<>();
        try (Connection conn = Connect.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, questionId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int imageId = rs.getInt("ImageId");
                String imageLoc = rs.getString("ImageLoc");
                questionId = rs.getInt("QuestionId");
                
                Image img = new Image(imageId, imageLoc, questionId);
                images.add(img);
                
                logInfo.setLevel("Info");
                logInfo.setLogInfo("Successfully got image at: " + imageLoc);
                logInfo.addLog(logInfo);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            logInfo.setLevel("Error");
            logInfo.setLogInfo("Error getting images: " + e.getStackTrace());
            logInfo.addLog(logInfo);
        }
        return images;
    }
    
    public Image getImage(String imageLoc, LogInfo logInfo) {
        String sql = "SELECT * FROM Image WHERE ImageLoc LIKE ?";
        
        
        
        try (Connection conn = Connect.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, "%" + imageLoc + "%");
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
            	logInfo.setLevel("Info");
            	logInfo.setLogInfo("Successfully got image.");
            	logInfo.addLog(logInfo);
                return new Image(
                    rs.getInt("ImageId"),
                    rs.getString("ImageLoc"),
                    rs.getInt("QuestionId")
                );
            }
        } catch (SQLException e) {
        	logInfo.setLevel("Error");
        	logInfo.setLogInfo("Error getting image: " + e.getStackTrace());
        	logInfo.addLog(logInfo);
            e.printStackTrace();
        }
        return null;
    }

    // READ ALL
    public List<Image> getAllImages() {
        List<Image> images = new ArrayList<>();
        String sql = "SELECT * FROM Image";
        try (Connection conn = Connect.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                images.add(new Image(
                    rs.getInt("ImageId"),
                    rs.getString("ImageLoc"),
                    rs.getInt("QuestionId")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return images;
    }

    // UPDATE
    public void updateImage(Image image, LogInfo logInfo) {
        String sql = "UPDATE Image SET ImageLoc = ?, QuestionId = ? WHERE ImageId = ?";
        try (Connection conn = Connect.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, image.getImageLoc());
            stmt.setInt(2, image.getQuestionId());
            stmt.setInt(3, image.getImageId());
            stmt.executeUpdate();
            
            logInfo.setLogInfo("Successfully updated image.");
            logInfo.setLevel("Info");
        } catch (SQLException e) {
            e.printStackTrace();
            logInfo.setLevel("Error");
            logInfo.setLogInfo("Error updating image: " + e.getStackTrace());
        }
        
        logInfo.addLog(logInfo);
    }

    // DELETE
    public void deleteImages(int questionId, LogInfo logInfo) {
        String sql = "DELETE FROM Image WHERE QuestionId = ?";
        try (Connection conn = Connect.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, questionId);
            stmt.executeUpdate();
            logInfo.setLogInfo("Successfully deleted images.");
            logInfo.setLevel("Info");
        } catch (SQLException e) {
            e.printStackTrace();
            logInfo.setLogInfo("Error deleting images: " + e.getStackTrace());
            logInfo.setLevel("Error");
        }
        
        logInfo.addLog(logInfo);
    }
    
    public void deleteImage(int imageId, LogInfo logInfo) {
    	String sql = "DELETE FROM Image WHERE ImageId = ?";
        try (Connection conn = Connect.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, imageId);
            stmt.executeUpdate();
            
            logInfo.setLevel("Info");
            logInfo.setLogInfo("Successfully deleted image.");
        } catch (SQLException e) {
            e.printStackTrace();
            logInfo.setLevel("Error");
            logInfo.setLogInfo("Error deleting image: " + e.getStackTrace());
        }
        
        logInfo.addLog(logInfo);
    }
}

