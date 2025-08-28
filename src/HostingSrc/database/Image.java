package database;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Base64;

public class Image {
    private int imageId;
    private String imageLoc;
    private int questionId;

    public Image(int imageId, String imageLoc, int questionId) {
        this.imageId = imageId;
        this.imageLoc = imageLoc;
        this.questionId = questionId;
    }

    public Image(String imageLoc, int questionId) {
        this.imageLoc = imageLoc;
        this.questionId = questionId;
    }
    
    public Image() {
    	this.imageId = -1;
    	this.imageLoc = "";
    	this.questionId = -1;
    }

    // Getters and Setters
    public int getImageId() { return imageId; }
    public String getImageLoc() { return imageLoc; }
    public int getQuestionId() { return questionId; }

    public String convertImageFileToString() {
    	if (imageLoc.isEmpty()) {
    		return "";
    	}
    	
    	File file = new File(imageLoc);
    	
    	byte[] fileContent = new byte[(int) file.length()];
    	
    	try (FileInputStream fileInputStream = new FileInputStream(file)){
    		fileInputStream.read(fileContent);
    	}catch (IOException e) {
    		System.out.println("Error: " + e.getMessage());
    	}
    	
    	return Base64.getEncoder().encodeToString(fileContent);
    }
    
    public void setImageLoc(String imageLoc) { this.imageLoc = imageLoc; }
    public void setQuestionId(int questionId) { this.questionId = questionId; }
}
