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
export async function addQuestions(question){
	let numQuestions = question.length;
	let questions = '{"Questions": [';

	for (let i = 0; i < numQuestions; i++){
		if (question[i].getCorrectAnswer().includes('"')){
			question[i].setCorrectAnswer(question[i].getCorrectAnswer().replaceAll('"', '\\"'));
		}
		if (question[i].getQuestion().includes('"')){
			question[i].setQuestion(question[i].getQuestion().replaceAll('"', '\\"'));
		}

		questions += '{"prompt":"' + question[i].getQuestion() + '",';
		questions += '"correctAnswer":"' + question[i].getCorrectAnswer() + '",';
		questions += '"wrongAnswers":[';
		let wrongAnswerSet = question[i].getWrongAnswers();
		let numWrongAnswers = wrongAnswerSet.length;

		for (let j = 0; j < numWrongAnswers; j++){
			if (j === 0){
				if (wrongAnswerSet[j].includes('"')){
					wrongAnswerSet[j] = wrongAnswerSet[j].replaceAll('"', '\\"');
				}
				questions += '"' + wrongAnswerSet[j] + '"';
				j++;
			}

			if (j === numWrongAnswers){
				questions += "]";
			}else if (j === numWrongAnswers - 1){
				if (wrongAnswerSet[j].includes('"')){
					wrongAnswerSet[j] = wrongAnswerSet[j].replaceAll('"', '\\"');
				}
				questions += ',"' + wrongAnswerSet[j] + '"]';
			}else{
				if (wrongAnswerSet[j].includes('"')){
					wrongAnswerSet[j] = wrongAnswerSet[j].replaceAll('"', '\\"');
				}
				questions += ',"' + wrongAnswerSet[j] + '"';
			}
		}
		questions += ', "justification":"' + question[i].getJustification() + '"';
		questions += ', "taskLetter": "' + question[i].getTaskLetter() + '"';
		questions += ', "hasImage": ' + question[i].getHasImage();
		questions += ', "images": [';
		questions += '{"image": "' +  question[i].getImage() + '"';
		questions += ', "imageType": "' + question[i].getImageType() + '"';
		questions += '}]';

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
	return await db.updatePrompt(oldPrompt, newPrompt);
}

export async function updateCorrectAnswer(prompt, newCorrectAnswer){
	return await db.updateCorrectAnswer(prompt, newCorrectAnswer);
}

export async function updateWrongAnswer(prompt, questionId, newWrongAnswer){
	return await db.updateWrongAnswer(prompt, questionId, newWrongAnswer);
}

export async function updateTaskLetter(prompt, taskLetter){
	return await db.updateTaskLetter(prompt, taskLetter);
}

export async function updateJustification(prompt, justification){
	return await db.updateJustification(prompt, justification);
}

export async function addWrongAnswer(prompt, wrongAnswer){
	return await db.addWrongAnswer(prompt, wrongAnswer);
}

export async function addImage(prompt, image, imageType){
	return await db.addImage(prompt, image, imageType);
}

export async function deleteWrongAnswer(prompt, wrongAnswer){
	return await db.deleteWrongAnswer(prompt, wrongAnswer);
}

export async function deleteImage(imageName){
	return await db.deleteImage(imageName);
}