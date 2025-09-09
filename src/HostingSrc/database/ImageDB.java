package database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

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
    public void addImage(Image image) {
        String sql = "INSERT INTO Image (ImageLoc, QuestionId) VALUES (?, ?)";
        try (Connection conn = Connect.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, image.getImageLoc());
            stmt.setInt(2, image.getQuestionId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // READ
    public Image getImage(int questionId, String imageLoc) {
        String sql = "SELECT * FROM Image WHERE QuestionId = ? AND ImageLoc=?";
        try (Connection conn = Connect.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, questionId);
            stmt.setString(2, imageLoc);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new Image(
                    rs.getInt("ImageId"),
                    rs.getString("ImageLoc"),
                    rs.getInt("QuestionId")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public ArrayList<Image> getImages(int questionId){
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
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return images;
    }
    
    public Image getImage(String imageLoc) {
        String sql = "SELECT * FROM Image WHERE ImageLoc LIKE ?";
        
        
        
        try (Connection conn = Connect.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, "%" + imageLoc + "%");
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new Image(
                    rs.getInt("ImageId"),
                    rs.getString("ImageLoc"),
                    rs.getInt("QuestionId")
                );
            }
        } catch (SQLException e) {
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
    public void updateImage(Image image) {
        String sql = "UPDATE Image SET ImageLoc = ?, QuestionId = ? WHERE ImageId = ?";
        try (Connection conn = Connect.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, image.getImageLoc());
            stmt.setInt(2, image.getQuestionId());
            stmt.setInt(3, image.getImageId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // DELETE
    public void deleteImages(int questionId) {
        String sql = "DELETE FROM Image WHERE QuestionId = ?";
        try (Connection conn = Connect.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, questionId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public void deleteImage(int imageId) {
    	String sql = "DELETE FROM Image WHERE ImageId = ?";
        try (Connection conn = Connect.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, imageId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

