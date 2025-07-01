//Creates an user class that can be used to handle multiple users in the database
class user{
	constructor(fName, lName, uName, mail, sett, perm){
		this.firstName = fName;
		this.lastName = lName;
		this.username = uName;
		this.email = mail;
		this.settings = sett;
		this.permissions = perm;
	}

	getFirstName(){
		return this.firstName;
	}

	getLastName(){
		return this.lastName;
	}

	getUsername(){
		return this.username;
	}

	getEmail(){
		return this.email;
	}

	getSettings(){
		return this.settings;
	}

	getPermissions(){
		return this.permissions;
	}
}

//Creates a question class to hold all the information for a question.
export class question{
	constructor(){
		this.question = "";
		this.correctAnswer = "";
		this.wrongAnswers = [];
	}

	getQuestion(){
		return this.question;
	}

	getCorrectAnswer(){
		return this.correctAnswer;
	}

	getWrongAnswers(){
		return this.wrongAnswers;
	}

	setQuestion(question){
		this.question = question;
	}

	setCorrectAnswer(answer){
		this.correctAnswer = answer;
	}

	addWrongAnswer(answer){
		this.wrongAnswers.push(answer);
	}

	removeWrongAnswers(){
		this.wrongAnswers = [];
	}
}

let users = new Map();
let questions = [];

let permissions = new Map();
let settings = new Map();

permissions.set('Users', 'T');
permissions.set('Questions', 'T');

settings.set('Timer', '10s');
settings.set('Number Questions', '20');
settings.set('Audio', 'T');

let user1 = new user("Admin", "Admin", "Admin", "admin@admin", settings, permissions);

users.set('1', user1);

export function getUserInfo(id){
	let userInfo = new Map();
	userInfo = users.get(id);
	if (userInfo === undefined){
		alert("User does not exist.");
	}

	return userInfo;
}

let quest = new question();
quest.setCorrectAnswer('4');
quest.addWrongAnswer('99');
quest.addWrongAnswer('12');
quest.addWrongAnswer('3');
quest.addWrongAnswer('21');
quest.setQuestion('What is 2+2?');

questions.push(quest);

let que = new question();
que.removeWrongAnswers();
que.setQuestion('How old are you?');
que.setCorrectAnswer('2');
que.addWrongAnswer('53');
que.addWrongAnswer('10');
que.addWrongAnswer('5');
questions.push(que);

export function getQuestionInfo(userQuestion){
	let questionInfo = new question();
	for (let i = 0; i < questions.length; i++){
		questionInfo = questions[i];

		if (userQuestion === questionInfo.getQuestion()){
			i = questions.length;
		} else if (i === questions.length - 1){
			questionInfo = undefined;
			i = questions.length;
		}
	}

	if (questionInfo === undefined){
		alert('Question does not exist.');
	}

	return questionInfo;
}

export function setUserInfo(id, userInfo){
	let user = users.get(id);

	if (user === undefined){
		users.set(id, userInfo);
	}else if (user !== userInfo){
		users.set(id, userInfo);
	}else{
		alert("No changes have been made.");
	}
}

export function setQuestions(q){
	if (typeof q !== 'object' || q.size !== 5){
		return;
	}
}

export function getQuestions(numQuest){
	let questionSet = [];
	if (numQuest > questions.length){
		numQuest = questions.length;
		alert('Too many questions requested. There are ' + numQuest + ' questions.');
	}
	for (let i = 0; i < numQuest; i++){
		questionSet.push(questions[i]);
	}

	return questionSet;
}