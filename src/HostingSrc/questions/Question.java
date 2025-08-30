package questions;

import java.util.ArrayList;

public class Question {
    private String prompt;
    private String correctAnswer;
    private ArrayList<String> wrongAnswers;
    private String justification;
    private String image;
    private String taskLetter;
    private String taskLetterDesc;
    private boolean hasImage;
    private String imageType;
    
    public Question() {
    	prompt = "";
    	correctAnswer = "";
    	wrongAnswers = new ArrayList<>();
    	justification = "";
    	image = "";
    	taskLetter = "";
    	hasImage = false;
    	taskLetterDesc = "";
    	imageType = "";
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
    
    public String getImageType() {
    	return imageType;
    }
    
   public String getTaskLetterDesc() {
	   return taskLetterDesc;
   }
    
    public String getImage() {
    	if (hasImage) {
    		return image;
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
    
    public void setImage(String image, String imageType) {
    	this.image = image;
    	this.imageType = imageType;
    	hasImage = true;
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

