import React, { useEffect, useState } from "react";

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
		this.taskLetter = "";
		this.justification = "";
		this.image = "";
		this.hasImage = false;
		this.imageType = "";
		this.taskLetterDesc = "";
		this.images = [];
		this.imageNames = [];
	}

	getImageNames(){
		if (this.hasImage){
			return this.imageNames;
		}else{
			return [];
		}
	}

	getImages(){
		if (this.hasImage){
			return this.images;
		}else{
			return [];
		}
	}

	getTaskLetterDesc(){
		return this.taskLetterDesc;
	}

	getImageType(){
		return this.imageType;
	}

	getHasImage(){
		return this.hasImage;
	}

	getImage(){
		if (this.hasImage){
			return this.image;
		}else{
			return "";
		}
	}

	getJustification(){
		return this.justification;
	}

	getTaskLetter(){
		return this.taskLetter;
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

	setTaskLetterDesc(taskLetterDesc){
		this.taskLetterDesc = taskLetterDesc;
	}

	addImage(image, imageType, imageName){
		this.hasImage = true;
		this.images.push([image, imageType]);
		this.imageNames.push(imageName);
	}

	setImage(image, imageType){
		this.image = image;
		this.imageType = imageType;
		this.hasImage = true;
	}

	setJustification(justification){
		this.justification = justification;
	}

	setTaskLetter(taskLetter){
		this.taskLetter = taskLetter;
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

	setWrongAnswer(answer, id){
		this.wrongAnswers[id] = answer;
	}
}

const baseURL = "http://localhost:8080/DTT_APP/QuizServlet/";
const getRandomQuestionsURL = "RandomQuestions";
const getSpecificQuestionURL = "SpecificQuestion";
const getQuestionTotalURL = "QuestionTotal";
const getQuestionsFromURL = "QuestionsFrom";
const deleteQuestionURL = "DeleteQuestion"
const updateQuestionPromptURL = "UpdateQuestionPrompt";
const updateCorrectAnswerURL = "UpdateQuestionAnswer";
const updateWrongAnswerURL = "UpdateWrongAnswer";
const fileUploadURL = "ReadPDF";
const getImagesURL = "Image";
const addImageURL = "AddImage";
const deleteImageURL = "DeleteImage";
const addWrongAnswerURL = "AddWrongAnswer";
const deleteWrongAnswerURL = "DeleteWrongAnswer";
const updateJustificationURL = "UpdateJustification";
const updateTaskLetterURL = "UpdateTaskLetter";

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

export function getBaseURL(){
	return baseURL;
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

		questionSet = await parseQuestions(Json, numQuest);
		return questionSet;
	}catch (e){
		console.error("Error: ", e);
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
	}catch (e){
		console.error("Error: ", e);
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
		questionSet = await parseQuestions(Json, numQuest);

		return questionSet;

	}catch (error){
		console.error("Error: ", error);
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
		console.error("Error: ", error);
	}
}

export async function updatePrompt(oldPrompt, newPrompt){
	try{
		const myHeaders = new Headers();
		myHeaders.append("Content-Type", "application/json");

		const response = await fetch(baseURL + updateQuestionPromptURL,{
			headers:myHeaders,
			method:"POST",
			body: JSON.stringify({"Prompts":[oldPrompt, newPrompt]}),
		})

		if (!response.ok){
			console.log(response);
		}
		return true;
	}catch(error){
		console.error("Error: ", error);
		return false;
	}
}

export async function updateCorrectAnswer(prompt, newCorrectAnswer){
	try{
		const myHeaders = new Headers();
		myHeaders.append("Content-Type", "application/json");

		const response = await fetch(baseURL + updateCorrectAnswerURL,{
			headers:myHeaders,
			method:"POST",
			body: JSON.stringify({"Prompt and Answer":[prompt, newCorrectAnswer]}),
		})

		if (!response.ok){
			console.log(response);
		}

		return true;

	}catch(error){
		console.error("Error: ", error);
		return false;
	}
}

export async function updateWrongAnswer(prompt, answerId, wrongAnswer){
	try{
		const myHeaders = new Headers();
		myHeaders.append("Content-Type", "application/json");

		const response = await fetch(baseURL + updateWrongAnswerURL,{
			headers:myHeaders,
			method:"POST",
			body: JSON.stringify({"Prompt":prompt, "Wrong Answer":wrongAnswer, "Answer Id":answerId}),
		})

		if (!response.ok){
			console.log(response);
		}

		return true;
	}catch(error){
		console.error("Error: ", error);
		return false;
	}
}

export async function deleteWrongAnswer(prompt, wrongAnswer){
	try{
		const myHeaders = new Headers();
		myHeaders.append("Content-Type", "application/json");

		const response = await fetch(baseURL + deleteWrongAnswerURL,{
			headers:myHeaders,
			method:"POST",
			body: JSON.stringify({"Prompt and Wrong Answer": [prompt, wrongAnswer]}),
		})

		if (!response.ok){
			console.log(response);
		}

		return true;
	}catch(error){
		console.error("Error: ", error);
		return false;
	}
}

export async function deleteImage(imageName){
	try{
		const myHeaders = new Headers();
		myHeaders.append("Content-Type", "application/json");

		const response = await fetch(baseURL + deleteImageURL,{
			headers:myHeaders,
			method:"POST",
			body: JSON.stringify({"Image Name": imageName}),
		})

		if (!response.ok){
			console.log(response);
		}

		return true;
	}catch(error){
		console.error("Error: ", error);
		return false;
	}
}

export async function addWrongAnswer(prompt, wrongAnswer){
	try{
		const myHeaders = new Headers();
		myHeaders.append("Content-Type", "application/json");

		const response = await fetch(baseURL + addWrongAnswerURL,{
			headers:myHeaders,
			method:"POST",
			body: JSON.stringify({"Prompt and Wrong Answer": [prompt, wrongAnswer]}),
		})

		if (!response.ok){
			console.log(response);
		}

		return true;
	}catch(error){
		console.error("Error: ", error);
		return false;
	}
}

export async function addImage(prompt, image, imageType){
	try{
		const myHeaders = new Headers();
		myHeaders.append("Content-Type", "application/json");

		const response = await fetch(baseURL + addImageURL,{
			headers:myHeaders,
			method:"POST",
			body: JSON.stringify({"prompt": prompt,
				"image":image,
				"imageType":imageType
			}),
		})

		if (!response.ok){
			console.log(response);
		}

		return true;
	}catch(error){
		console.error("Error: ", error);
		return false;
	}
}

export async function updateTaskLetter(prompt, taskLetter){
	try{
		const myHeaders = new Headers();
		myHeaders.append("Content-Type", "application/json");

		const response = await fetch(baseURL + updateTaskLetterURL,{
			headers:myHeaders,
			method:"POST",
			body: JSON.stringify({"Prompt and Task Letter":[prompt, taskLetter]}),
		})

		if (!response.ok){
			console.log(response);
		}

		return true;

	}catch(error){
		console.error("Error: ", error);
		return false;
	}
}

export async function updateJustification(prompt, justification){
	try{
		const myHeaders = new Headers();
		myHeaders.append("Content-Type", "application/json");

		const response = await fetch(baseURL + updateJustificationURL,{
			headers:myHeaders,
			method:"POST",
			body: JSON.stringify({"Prompt and Justification":[prompt, justification]}),
		})

		if (!response.ok){
			console.log(response);
		}

		return true;

	}catch(error){
		console.error("Error: ", error);
		return false;
	}
}

export async function uploadFile(formData){
	try{
		const resp = await fetch(baseURL + fileUploadURL, {
			method: 'POST',
			body: formData
		});

		if (!resp.ok){
			console.log(resp.body);
			return null;
		}

		let json = await resp.text();

		let file = JSON.parse(json);

		return parseQuestions(file, file["Questions"].length);
	}catch(e){
		console.error("Error: ", e);
	}
}

export async function getImage(imageName){
	const params = new URLSearchParams();
	params.append("ImageName", imageName);

	try{
		const resp = await fetch (baseURL + getImagesURL + "?" + params);

		if (!resp.ok){
			console.error("Error fetching...");
			return;
		}

		const blob = await resp.blob();

		const image = URL.createObjectURL(blob);

		return image;
	}catch (e){
		console.error("Error: ", e);
	}
}

async function parseQuestions(jsonInput, numQuest){
	let questionSet = [];

	for (let i = 0; i < numQuest; i++){
		let q = new question();
		
		q.setQuestion(jsonInput["Questions"][i]["prompt"]);
		q.setCorrectAnswer(jsonInput["Questions"][i]["correctAnswer"]);

		let numWrongQuestions = jsonInput["Questions"][i]["wrongAnswers"].length;

		for (let j = 0; j < numWrongQuestions; j++){
			q.addWrongAnswer(jsonInput["Questions"][i]["wrongAnswers"][j]);
		}

		q.setJustification(jsonInput["Questions"][i]["justification"]);
		if (jsonInput["Questions"][i]["hasImage"] === true){
			let images = jsonInput["Questions"][i]["images"];
			let numImages = images.length;

			for (let j = 0; j < numImages; j++){
				let image = await getImage(images[j]["imageLoc"]);
				q.addImage(image, images[j]["imageType"], images[j]["imageLoc"]);
			}
		}
		q.setTaskLetter(jsonInput["Questions"][i]["taskLetter"]);
		q.setTaskLetterDesc(jsonInput["Questions"][i]["taskLetterDesc"]);

		questionSet.push(q);
	}
	return questionSet;
}