package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ImageDB {
	private static String user = "mrjackedupbear";
	private static String pass = "TempPass";
	private static String DB_URL = "jdbc:mariadb://localhost:3306/DTT_APP";
	
	private static String createQuestion = "CREATE TABLE IF NOT EXISTS Image (ImageId INT NOT NULL AUTO_INCREMENT, ImageLoc TEXT NOT NULL, QuestionId INT NOT NULL, PRIMARY KEY(ImageId));";
	
	private static ImageDB i = new ImageDB();
	
	public static ImageDB getInstance() {
		return i;
	}
	
	private Connection connect() throws SQLException {
        return DriverManager.getConnection(DB_URL, user, pass);
    }
	
	private ImageDB() {
		try (Connection conn = DriverManager.getConnection(DB_URL, user, pass)){
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
        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, image.getImageLoc());
            stmt.setInt(2, image.getQuestionId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // READ
    public Image getImage(int questionId) {
        String sql = "SELECT * FROM Image WHERE QuestionId = ?";
        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, questionId);
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
        try (Connection conn = connect();
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
        try (Connection conn = connect();
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
    public void deleteImage(int id) {
        String sql = "DELETE FROM Image WHERE ImageId = ?";
        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

