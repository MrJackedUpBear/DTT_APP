import * as db from './Database.js';

let questions = [];
let question = '';
let correctAnswer = '';
let wrongAnswers = [];
let numQuest = 0;

export function getCorrectAnswer(){
	return correctAnswer;
}

function getQuestions(numQ){
	questions = db.getQuestions(numQ);
	numQuest = questions.length;
}

export function getQuestion(questionNum, totalQuestions){
	if (questionNum === 0){
		getQuestions(totalQuestions);
	}

	question = questions[questionNum];
	
	return question;
}

export function setCorrectAnswer(input){
	if (question === ''){
		return;
	}

	correctAnswer = input;
}

export function setQuestion(input){
	question = input;

	let questionInfo = db.getQuestionInfo(question);

	if (questionInfo === undefined){
		question = '';
		correctAnswer = '';
		wrongAnswers = new Map();
		return;
	}

	correctAnswer = questionInfo.getCorrectAnswer();
	wrongAnswers = questionInfo.getWrongAnswers();
}

export function setWrongAnswers(input){
	if (question === ''){
		return;
	}

	wrongAnswers = input;
}

export function getWrongAnswers(){
	return wrongAnswers;
}

export function getNumberQuestions(){
	return numQuest;
}