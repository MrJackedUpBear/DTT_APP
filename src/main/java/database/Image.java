package database;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Base64;

import logging.Log;
import logging.LogInfo;

public class Image {
    private int imageId;
    private String imageLoc;
    private String imageType;
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
    
    public Image(String imageLoc, String imageType) {
    	this.imageLoc = imageLoc;
    	this.imageType = imageType;
    }
    
    public Image() {
    	this.imageId = -1;
    	this.imageLoc = "";
    	this.questionId = -1;
    }

    // Getters and Setters
    public String getImageType() {return imageType;}
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
    
    public void setImageType(String imageType) {this.imageType = imageType;}
    public void setImage(String image) {setImageLoc(image);}
    public void setPrompt(String prompt) {
    	LogInfo logInfo = new LogInfo();
    	logInfo.setTypeOfRequest("SetPrompt");
    	logInfo.setLevel("Info");
    	
    	User user = new User();
    	user.setFirstName("System");
    	user.setLastName("System");
    	user.setUserId(-1000000);
    	
    	logInfo.setUser(user);
    	logInfo.setLogInfo("Setting prompt...");
    	
    	Log.getInstance().log(logInfo);
    	int questionId = QuestionDB.getInstance().getQuestionId(prompt, logInfo);
    	
    	this.questionId = questionId;
    }
    public void setImageLoc(String imageLoc) { this.imageLoc = imageLoc; }
    public void setQuestionId(int questionId) { this.questionId = questionId; }
}
