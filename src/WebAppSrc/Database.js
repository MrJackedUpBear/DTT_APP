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
const getQuestionsFromURL = "QuestionsFrom";
const deleteQuestionURL = "DeleteQuestion"

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
	let Json = "";
	const params = new URLSearchParams();
	params.append("NumQuestions", numQuest);

	try{
		const response = await fetch(baseURL + getRandomQuestionsURL + '?' + params);
		if (!response.ok){
			console.log("Bad response: " + response.status);
			return;
		}

		Json = await response.text();
		//questionSet = parseQuestions(JSON, numQuest);
		Json = await JSON.parse(Json);

		questionSet = parseQuestions(Json, numQuest);
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

export async function getQuestionsFrom(start, end){
	let Json = "";
	let questionSet = [];
	
	const params = new URLSearchParams();
	params.append("Start", start);
	params.append("End", end);

	try{
		const response = await fetch(baseURL + getQuestionsFromURL + '?' + params);

		if (!response.ok){
			console.log("Bad response: " + response.status);
			return;
		}

		Json = await response.text();
		Json = await JSON.parse(Json);

		let numQuest = end - start;
		questionSet = parseQuestions(Json, numQuest);

		return questionSet;

	}catch (error){
		alert("Error connecting to database: " + error);
	}
}

export async function deleteQuestion(prompt){
	try{
		const myHeaders = new Headers();
		myHeaders.append("Content-Type", "application/json");
		const response = await fetch(baseURL + deleteQuestionURL,{
			headers:myHeaders,
			method:"POST",
			body: JSON.stringify({"Prompt":prompt}),
		});

		console.log(response.status);
	}catch(error){
		alert("Error adding: " + error);
	}
}

function parseQuestions(jsonInput, numQuest){
	let questionSet = [];

	for (let i = 0; i < numQuest; i++){
		let q = new question();
		
		q.setQuestion(jsonInput["Questions"][i]["Prompt"]);
		q.setCorrectAnswer(jsonInput["Questions"][i]["Correct Answer"]);

		let numWrongQuestions = jsonInput["Questions"][i]["Wrong Answers"].length;

		for (let j = 0; j < numWrongQuestions; j++){
			q.addWrongAnswer(jsonInput["Questions"][i]["Wrong Answers"][j]);
		}

		questionSet.push(q);
	}
	return questionSet;
}