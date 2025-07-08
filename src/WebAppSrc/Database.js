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

const baseURL = "http://localhost:8080/DTT_APP/QuizServlet/";
const getRandomQuestionsURL = "RandomQuestions";
const getSpecificQuestionURL = "SpecificQuestion";
const getQuestionTotalURL = "QuestionTotal";

const addQuestionsURL = "AddQuestions";

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

export async function addQuestions(questionsToAdd){
	try{
		const myHeaders = new Headers();
		myHeaders.append("Content-Type", "application/json");
		const response = await fetch(baseURL + addQuestionsURL, {
			headers:myHeaders,
			method: "POST",
			body: JSON.stringify(questionsToAdd),
		});

		console.log(response.status);
	}catch(error) {
		alert("Error Adding" + error);
		console.log("Error Adding: " + error);
	}
}

export async function getQuestions(numQuest){
	let questionSet = [];
	let JSON = "";
	const params = new URLSearchParams();
	params.append("NumQuestions", numQuest);

	try{
		const response = await fetch(baseURL + getRandomQuestionsURL + '?' + params);
		if (!response.ok){
			console.log("Bad response: " + response.status);
			return;
		}

		JSON = await response.text();
		questionSet = parseQuestions(JSON, numQuest);
		return questionSet;
	}catch{
		alert("Error connecting to database.");
	}
}

export async function getNumberQuestions(){
	let numQuestions = 0;
	let JSON = "";
	try{
		const response = await fetch(baseURL + getQuestionTotalURL);
		if (!response.ok){
			console.log("Bad response: " + response.status);
			return;
		}

		JSON = await response.text();

		let split = JSON.split("{\"Question Total\":\"")[1];

		let stringNum = split.split("\"}")[0];

		numQuestions = parseInt(stringNum);
	}catch{
		alert("Error connecting to the database.");
	}
	return numQuestions;
}

function parseQuestions(jsonInput, numQuest){
	let questionInfo = jsonInput.split("}},");
	let questionSet = [];

	for (let i = 0; i < numQuest; i++){
		let q = new question();
		
		let wrongAnswers = questionInfo[i].split("\",\"Wrong Answers\":{")[1];
		questionInfo[i] = questionInfo[i].split("\",\"Wrong Answers\":")[0];

		let correctAnswer = questionInfo[i].split("\",\"Correct Answer\":\"")[1];
		questionInfo[i] = questionInfo[i].split("\",\"Correct Answer\":")[0];

		let prompt = questionInfo[i].split(":{\"Prompt\":\"")[1];

		wrongAnswers = wrongAnswers.split(",")
		let numWrongAnswers = wrongAnswers.length;

		for (let j = 0; j < numWrongAnswers; j++){
			let hasQuote = false;
			if (wrongAnswers[j].includes("\\\"")){
				wrongAnswers[j] = wrongAnswers[j].replaceAll("\\\"", "-99191299");
				hasQuote = true;
			}

			if (wrongAnswers[j].includes("}")){
				wrongAnswers[j] = wrongAnswers[j].replaceAll("}", "");
			}
			wrongAnswers[j] = wrongAnswers[j].split(":\"")[1];
			wrongAnswers[j] = wrongAnswers[j].split("\",")[0];

			wrongAnswers[j] = wrongAnswers[j].replaceAll('"', '');

			if (hasQuote){
				wrongAnswers[j] = wrongAnswers[j].replaceAll("-99191299", "\"");
			}

			q.addWrongAnswer(wrongAnswers[j]);
		}

		q.setCorrectAnswer(correctAnswer);
		q.setQuestion(prompt);

		questionSet.push(q);
	}
	return questionSet;
}