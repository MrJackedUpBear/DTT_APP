package questions;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import logging.Log;
import logging.LogInfo;

	/**
	 * Hello world!
	 *
	 */
	public class QuestionGenerator 
	{
	    static String linesToAvoid[] = {"Score: ",
	    "Percentage: ",
	    "Duration: ",
	    "Date started: ",
	    "Date finished: ",
	    "Category Percentage Points",
	    "Section A ",
	    "Section B ",
	    "Section C ",
	    "Section D ",
	    "Section E ",
	    "Section F ",
	    "Section G ",
	    "Section H ",
	    "Section I ",
	    "Integrated Behavioral Technologies Mail - Your Exam Results",
	    "https://mail.google.com/mail",
	    "Answers",
	    "Key: A tick or cross will show next to chosen answers.",
	    "Correctly answered",
	    "Incorrectly answered",
	    "Missed correct option",
	    "Question 1 of 175"};

	    String linesToCheck[] = {"Question",
	    "Correct answer:",
	    "Points"};

	    static char letters[] = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};

	    public static void showQuestions(ArrayList<Question> questions){
	        int len = questions.size();
	        System.out.println("There are " + len + " questions.");
	        for (int i = 0; i < len; i++){
	            String prompt = questions.get(i).getPrompt();
	            String correctAnswer = questions.get(i).getCorrectAnswer();
	            ArrayList<String> wrongAnswers = questions.get(i).getWrongAnswers();

	            System.out.println("Question " + (i + 1) + ":");
	            System.out.println("Prompt: " + prompt);
	            System.out.println("Correct Answer: " + correctAnswer);
	            for (String wrongAnswer : wrongAnswers){
	                System.out.println("Wrong Answer: " + wrongAnswer);
	            }
	        }
	    }

	    public static ArrayList<Question> readPDF(Path file, LogInfo logInfo){
	        ArrayList<Question> questions = new ArrayList<>();
	        Scanner scanner = new Scanner(System.in);
	        try{
	            PDDocument document = Loader.loadPDF(file.toFile());
	            PDFTextStripper pdfStripper = new PDFTextStripper();
	            String text = pdfStripper.getText(document);
	            List<String> lines = Arrays.asList(text.split("\r?\n"));
	             char correctAnswerLetter;
	            for (int i = 0; i < lines.size(); i++){
	                if (lines.get(i).contains("Question")){
	                    String prompt = "";
	                    i++;
	                    while (!lines.get(i).contains("Correct answer:")){
	                        for (int j = 0; j < linesToAvoid.length; j++){
	                            if (lines.get(i).contains(linesToAvoid[j])){
	                                System.out.println("\n\nLine at " + i + ": " + lines.get(i) + "\nContains: " + linesToAvoid[j]);
	                                j = linesToAvoid.length;
	                            }else if (j == (linesToAvoid.length - 1)){
	                                if (prompt.isEmpty()){
	                                    prompt += lines.get(i);
	                                }else{
	                                    prompt += " " + lines.get(i);
	                                }
	                            }
	                        }
	                        i++;
	                    }
	                    correctAnswerLetter = lines.get(i).substring(("Correct answer: ").length()).charAt(0);
	                    
	                    int letterLoc = 0;
	                    while (lines.get(i).charAt(0) != letters[letterLoc]){
	                        i++;
	                    }

	                    ArrayList<String> wrongAnswers = new ArrayList<>();
	                    String correctAnswer = "";

	                    Boolean wrongAnswer = false;
	                    String line = "";
	                    while(!lines.get(i).contains("Points: ")){
	                        if (correctAnswerLetter == (lines.get(i).charAt(0))){
	                            if (!line.isEmpty()){
	                                wrongAnswers.add(line);
	                                line = new String("");
	                            }
	                            wrongAnswer = false;
	                            for (int j = 0; j < linesToAvoid.length; j++){
	                                if (lines.get(i).contains(linesToAvoid[j])){
	                                    j = linesToAvoid.length;
	                                }else if (j == (linesToAvoid.length - 1)){
	                                    if (correctAnswer.isEmpty()){
	                                        correctAnswer += lines.get(i).split("\\) ")[1];
	                                    }else{
	                                        correctAnswer += " " + lines.get(i).split("\\) ")[1];
	                                    }
	                                }
	                            }
	                        }else if (lines.get(i).contains(") ")){
	                            if (!line.isEmpty()){
	                                wrongAnswers.add(line);
	                                line = new String("");
	                            }
	                            wrongAnswer = true;

	                            for (int j = 0; j < linesToAvoid.length; j++){
	                                if (lines.get(i).contains(linesToAvoid[j])){
	                                    j = linesToAvoid.length;
	                                }else if (j == (linesToAvoid.length - 1)){
	                                    if (line.isEmpty()){
	                                        line += lines.get(i).split("\\) ")[1];
	                                    }else{
	                                        line += " " + lines.get(i).split("\\) ")[1];
	                                    }
	                                }
	                            }
	                        }else{
	                            if (!wrongAnswer){
	                                for (int j = 0; j < linesToAvoid.length; j++){
	                                if (lines.get(i).contains(linesToAvoid[j])){
	                                    j = linesToAvoid.length;
	                                }else if (j == (linesToAvoid.length - 1)){
	                                    correctAnswer += lines.get(i);
	                                }
	                            }
	                            }else{
	                                for (int j = 0; j < linesToAvoid.length; j++){
	                                    if (lines.get(i).contains(linesToAvoid[j])){
	                                        j = linesToAvoid.length;
	                                    }else if (j == (linesToAvoid.length - 1)){
	                                        line += lines.get(i);
	                                    }
	                                }
	                            }
	                        }   
	                        letterLoc++;
	                        i++;
	                    }

	                    if (!line.isEmpty()){
	                        wrongAnswers.add(line);
	                    }

	                    questions.add(new Question(prompt, correctAnswer, wrongAnswers));
	                    logInfo.setLevel("Info");
	                    logInfo.setLogInfo("Successfully added question to list: " + prompt);
	                    Log.getInstance().log(logInfo);
	                }
	            }
	        }catch(IOException e){
	            System.out.println("Error: " + e.getMessage());
	            logInfo.setLevel("Error");
	            logInfo.setLogInfo("Error getting questions from file: " + e.getStackTrace());
	            Log.getInstance().log(logInfo);
	        }
	        scanner.close();
	        return questions;
	    }
	}
