package questions;

import java.util.ArrayList;

public class Question {
    private String prompt = "";
    private String correctAnswer = "";
    private ArrayList<String> wrongAnswers = new ArrayList<>();

    public Question(String prompt, String correctAnswer, ArrayList<String> wrongAnswers){
        this.prompt = prompt;
        this.correctAnswer = correctAnswer;
        this.wrongAnswers = wrongAnswers;
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

