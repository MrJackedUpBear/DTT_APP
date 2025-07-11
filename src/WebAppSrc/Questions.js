import * as db from './Database.js';

let questions = [];
let question = '';
let correctAnswer = '';
let wrongAnswers = [];
let numQuest = 0;

export function getCorrectAnswer(){
	return correctAnswer;
}

async function getQuestions(numQ){
	questions = await db.getQuestions(numQ);
	numQuest = questions.length;
}

export async function getQuestion(questionNum, totalQuestions, wrongChoice){
	if (questionNum === 0 && !wrongChoice){
		await getQuestions(totalQuestions);
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

export async function getNumberQuestions(){
	numQuest = await db.getNumberQuestions();
	return numQuest;
}

//prompts is an array, correctAnswers is an array, and wrongAnswers is a nested array(ie. [[1, 2],['yes', 'no']])
export async function addQuestions(prompts, correctAnswers, wrongAnswers){
	let numQuestions = prompts.length;
	let questions = '{"Questions": [{';

	for (let i = 0; i < numQuestions; i++){
		if (correctAnswers[i].includes('"')){
			correctAnswers[i] = correctAnswers[i].replaceAll('"', '\\"');
		}
		if (prompts[i].includes('"')){


			prompts[i] = prompts[i].replaceAll('"', '\\"');


		}

		questions += '"Prompt":"' + prompts[i] + '",';
		questions += '"Correct Answer":"' + correctAnswers[i] + '",';
		questions += '"Wrong Answers":[';
		let wrongAnswerSet = wrongAnswers[i];
		let numWrongAnswers = wrongAnswerSet.length;

		for (let j = 0; j < numWrongAnswers; j++){
			if (j === 0){
				questions += '"' + wrongAnswerSet[j] + '"';
				j++;
			}

			if (j === numWrongAnswers){
				questions += "]";
			}else if (j === numWrongAnswers - 1){
				questions += ',"' + wrongAnswerSet[j] + '"]';
			}else{
				questions += ',"' + wrongAnswerSet[j] + '"';
			}
		}
		if (i === numQuestions - 1){
			questions += '}';
		}else{
			questions += '},';
		}
	}

	questions += ']';
	questions += '}';
	questions = JSON.parse(questions);
	await db.addQuestions(questions);
}

export async function getQuestionsFrom(start, end){
	let questionsFrom = await db.getQuestionsFrom(start, end);
	return questionsFrom;
}

export async function deleteQuestion(prompt){
	await db.deleteQuestion(prompt);
}

export async function updatePrompt(oldPrompt, newPrompt){
	await db.updatePrompt(oldPrompt, newPrompt);
}

export async function updateCorrectAnswer(oldCorrectAnswer, newCorrectAnswer){
	await db.updateCorrectAnswer(oldCorrectAnswer, newCorrectAnswer);
}

export async function updateWrongAnswer(prompt, wrongAnswer, questionId){
	await db.updateWrongAnswer(prompt, wrongAnswer, questionId);
}