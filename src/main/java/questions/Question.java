package questions;

import java.util.ArrayList;
import java.util.HashMap;

import database.Image;
import database.ImageDB;
import database.User;
import logging.LogInfo;

public class Question {
    private String prompt;
    private String correctAnswer;
    private ArrayList<String> wrongAnswers;
    private String justification;
    private String taskLetter;
    private String taskLetterDesc;
    private boolean hasImage;
    ArrayList<Image> imgs = new ArrayList<>();
    ArrayList<HashMap<String, String>> images = new ArrayList<>();
    
    public Question() {
    	prompt = "";
    	correctAnswer = "";
    	wrongAnswers = new ArrayList<>();
    	justification = "";
    	taskLetter = "";
    	hasImage = false;
    	taskLetterDesc = "";
    }
    
    public Question(String prompt, String correctAnswer, ArrayList<String> wrongAnswers){
        this.prompt = prompt;
        this.correctAnswer = correctAnswer;
        this.wrongAnswers = wrongAnswers;
    }

    public Question(String prompt, String correctAnswer, ArrayList<String> wrongAnswers, String justification, String taskLetter){
        this.prompt = prompt;
        this.correctAnswer = correctAnswer;
        this.wrongAnswers = wrongAnswers;
        this.justification = justification;
        this.taskLetter = taskLetter;
    }
    
    public boolean getHasImage() {
    	return hasImage;
    }
    
    public String getImageType(int imageId) {
    	return imgs.get(imageId).getImageType();
    }
    
   public String getTaskLetterDesc() {
	   return taskLetterDesc;
   }
   
   public ArrayList<Image> getImages(){
	   return imgs;
   }
   
   public ArrayList<Image> getImages(int questionId){
	   ArrayList<Image> im = new ArrayList<>();
	   
	   for (Image img : imgs) {
		   if (img.getQuestionId() == questionId) {
			   im.add(img);
		   }
	   }
	   
	   return im;
   }
    
    public String getImage(int imageId) {
    	if (hasImage) {
    		return imgs.get(imageId).getImageLoc();
    	}else {
    		return "";
    	}
    }
    
    public String getTaskLetter() {
    	return taskLetter;
    }
    
    public String getJustification() {
    	return justification;
    }
    
    public String getPrompt(){
        return prompt;
    }

    public String getCorrectAnswer(){
        return correctAnswer;
    }

    public ArrayList<String> getWrongAnswers(){
        return wrongAnswers;
    }
    
    public void setTaskLetterDesc(String taskLetterDesc) {
    	this.taskLetterDesc = taskLetterDesc;
    }
    
    public void addImage(String image, int questionId, String imageType) {
    	hasImage = true;
    	imgs.add(new Image(image, questionId));
    	
    	imgs.get(imgs.size() - 1).setImageType(imageType);
    }
    
    public void setImages(ArrayList<HashMap<String, String>> images) {
    	this.images = images;
    	
    	for (HashMap<String, String> img : images) {    		
    		imgs.add(new Image(img.get("image"), img.get("imageType")));
    	}
    }
    
    public void deleteImage(Image image) {
    	if (imgs.size() == 1) {
    		this.hasImage = false;
    	}
    	
    	LogInfo logInfo = new LogInfo();
    	logInfo.setTypeOfRequest("DeleteImage");
    	logInfo.setLevel("Info");
    	
    	User user = new User();
    	user.setFirstName("System");
    	user.setLastName("System");
    	user.setUserId(-1000000);
    	
    	logInfo.setUser(user);
    	logInfo.setLogInfo("Deleting image...");
    	
    	imgs = ImageDB.getInstance().getImages(image.getQuestionId(), logInfo);
    	
    	ArrayList<Image> i = new ArrayList<>();
    	for (Image img : imgs) {
    		if (!img.equals(image)) {
    			i.add(img);
    		}
    	}
    	
    	this.imgs = i;
    }
    
    public void setTaskLetter(String taskLetter) {
    	this.taskLetter = taskLetter;
    }
    
    public void setJustification(String justification) {
    	this.justification = justification;
    }

    public void setPrompt(String prompt){
        this.prompt = prompt;
    }

    public void setCorrectAnswer(String correctAnswer){
        this.correctAnswer = correctAnswer;
    }

    public void setWrongAnswers(ArrayList<String> wrongAnswers){
        this.wrongAnswers = wrongAnswers;
    }
    
    public void setWrongAnswer(int index, String wrongAnswer) {
    	wrongAnswers.set(index, wrongAnswer);
    }
    
    @Override
    public String toString() {
    	return "Question{" + "Prompt='" + prompt +'\'' +  ",Correct Answer=" + correctAnswer + '\'' +  ",Wrong Answers=" + wrongAnswers;
    }
}

