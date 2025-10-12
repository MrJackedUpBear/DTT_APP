import React, { useEffect, useState } from "react";
import {user, Setting} from './User.js';
import * as User from './User.js';
import router from './index.js';

//Creates an user class that can be used to handle multiple users in the database

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

	removeImage(imageId){
		if (imageId !== -1){
			this.images.splice(imageId, 1);
			this.imageNames.splice(imageId, 1);
		}
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
		while (this.wrongAnswers.length > 0){
			this.wrongAnswers.pop();
		}
	}

	removeWrongAnswer(wrongAnswer){
		if (wrongAnswer !== -1){
			this.wrongAnswers.splice(wrongAnswer, 1);
		}
	}

	setWrongAnswer(answer, id){
		this.wrongAnswers[id] = answer;
	}
}

const baseURL = "http://localhost:8080/DTT_APP/"

const authURL = baseURL + "Auth/";
const emailAuthURL = "EmailAuth";
const tokenCheckURL = "IsValid";

const getUserInfoURL = "User";

const settingsURL = baseURL + "Settings/";
const getSettingsURL = "AppSettings";
const updateSettingsURL = "Update";

const questionURL = baseURL + "Quiz/";
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

let currentUser;
let questions = [];

let permissions = new Map();
let settings = new Map();

permissions.set('Users', 'T');
permissions.set('Questions', 'T');

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

export function getBaseURL(){
	return baseURL;
}

export async function verifyTokens(){
	let token = localStorage.getItem("AccessToken");
	if (token !== null){
		return true;
	}else{
		return false;
	}
}

export async function getToken(username, password){
	try{
		const myHeaders = new Headers();
		let combined = username + ":" + password;
		myHeaders.append("Content-Type", "application/json");
		combined = btoa(combined);
		myHeaders.append("Authorization", "Basic " + combined);

		const response = await fetch(authURL, {
			headers:myHeaders,
			method: "POST",
			credentials: "include",
		});

		if (!response.ok){
			console.error("Error getting token..." + response);
			return false;
		}

		let token = await response.json();

		localStorage.setItem("AccessToken", token["AccessToken"]);
		const cookie = response.headers.getSetCookie();

		cookie.forEach(cook =>{
			alert(cook);
		});
		return true;
	}catch(e){
		console.error(e);
	}
}

export async function verifyEmailAuth(code){
	const myHeaders = new Headers();
	let combined = 'dummy' + ":" + code;
	myHeaders.append("Content-Type", "application/json");
	combined = btoa(combined);
	myHeaders.append("Authorization", "Email " + combined);

	const response = await fetch(authURL, {
		headers:myHeaders,
		method: "POST",
		credentials: "include",
	});

	if (!response.ok){
		console.error("Error getting token..." + response);
		return false;
	}

	let token = await response.json();

	localStorage.setItem("AccessToken", token["AccessToken"]);
	const cookie = response.headers;

	alert(cookie.getSetCookie());

	return true;
}

export async function getEmailAuth(username){
	try{
		const myHeaders = new Headers();
		myHeaders.append("Content-Type", "application/json");

		let req = "Username=" + username;

		const response = await fetch(authURL + emailAuthURL + "?" + req, {
			headers:myHeaders,
			method: "GET",
		});

		if (!response.ok){
			console.error("Error getting token..." + response);
			return false;
		}
	}catch(e){
		console.error(e);
	}
}

export async function verifyToken(token){
	try{
		const myHeaders = new Headers();
		myHeaders.append("Content-Type", "application/json");

		let req = "Token=" + token;

		const response = await fetch(authURL + tokenCheckURL + "?" + req, {
			headers:myHeaders,
			method: "GET",
			credentials: "include",
		});

		if (!response.ok){
			console.error("Error checking token..." + response);
			return false;
		}

		let isValid = await response.json();

		return isValid["IsValid"];
	}catch(e){
		console.error(e);
	}
}

export async function refreshAccessToken(){
	const myHeaders = new Headers();
	let combined = 'dummy:dummy';
	myHeaders.append("Content-Type", "application/json");
	combined = btoa(combined);
	myHeaders.append("Authorization", "Refresh " + combined);

	const response = await fetch(authURL, {
		headers:myHeaders,
		method: "POST",
		credentials: "include",

	});

	if (!response.ok){
		console.error("Error getting token..." + response);
		return false;
	}

	let token = await response.json();

	localStorage.setItem("AccessToken", token["AccessToken"]);

	return true;
}

export async function addQuestions(questionsToAdd, iteration = 0){
	try{
		const myHeaders = new Headers();
		myHeaders.append("Content-Type", "application/json");

		let accessToken = "Bearer " + btoa(localStorage.getItem("AccessToken"));

		myHeaders.append("Authorization", accessToken);

		const response = await fetch(questionURL + addQuestionsURL, {
			headers:myHeaders,
			method: "POST",
			body: JSON.stringify(questionsToAdd),
		});

		let responseCode = response.status;

		let u = await User.getUser();

		if (responseCode === 401 && iteration === 0){
			await refreshAccessToken();
			return await addQuestions(questionsToAdd, 1);
		}else if (responseCode === 401 && iteration === 1 && u === undefined){
			router.navigate("/Login");
			return;
		}else if (responseCode === 401){
			router.navigate('/MainPage');
		}

		console.log(response.status);
	}catch(error) {
		console.log("Error Adding: " + error);
	}
}

export async function getQuestions(numQuest, iteration = 0){
	let questionSet = [];
	let Json = "";
	const params = new URLSearchParams();
	params.append("NumQuestions", numQuest);

	const myHeaders = new Headers();

	let accessToken = "Bearer " + btoa(localStorage.getItem("AccessToken"));

	myHeaders.append("Authorization", accessToken);

	try{
		const response = await fetch(questionURL + getRandomQuestionsURL + '?' + params, {
			headers:myHeaders,
		});

		let responseCode = response.status;
		
		let u = await User.getUser();

		if (responseCode === 401 && iteration === 0){
			await refreshAccessToken();
			return await getQuestions(numQuest, 1);
		}else if (responseCode === 401 && iteration === 1 && u === undefined){
			router.navigate("/Login");
			return;
		}else if (responseCode === 401){
			router.navigate('/MainPage');
		}

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

export async function getNumberQuestions(iteration = 0){
	let numQuestions = 0;
	let JSON = "";

	const myHeaders = new Headers();

	let accessToken = "Bearer " + btoa(localStorage.getItem("AccessToken"));

	myHeaders.append("Authorization", accessToken);
	try{
		const response = await fetch(questionURL + getQuestionTotalURL, {
			headers:myHeaders,
		});

		let responseCode = response.status;

		let u = await User.getUser();

		if (responseCode === 401 && iteration === 0){
			await refreshAccessToken();
			return await getNumberQuestions(1);
		}else if (responseCode === 401 && iteration === 1 && u === undefined){
			router.navigate("/Login");
			return;
		}else if (responseCode === 401){
			router.navigate('/MainPage');
		}

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

export async function getQuestionsFrom(start, end, iteration = 0){
	let Json = "";
	let questionSet = [];

	const myHeaders = new Headers();

	let accessToken = "Bearer " + btoa(localStorage.getItem("AccessToken"));

	myHeaders.append("Authorization", accessToken);
	
	const params = new URLSearchParams();
	params.append("Start", start);
	params.append("End", end);

	try{
		const response = await fetch(questionURL + getQuestionsFromURL + '?' + params, {
			headers:myHeaders,
		});

		let responseCode = response.status;

		let u = await User.getUser();

		if (responseCode === 401 && iteration === 0){
			await refreshAccessToken();
			return await getQuestionsFrom(start, end, 1);
		}else if (responseCode === 401 && iteration === 1 && u === undefined){
			router.navigate("/Login");
			return;
		}else if (responseCode === 401){
			router.navigate('/MainPage');
		}

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

export async function deleteQuestion(prompt, iteration = 0){
	try{
		const myHeaders = new Headers();
		myHeaders.append("Content-Type", "application/json");

		let accessToken = "Bearer " + btoa(localStorage.getItem("AccessToken"));

		myHeaders.append("Authorization", accessToken);

		const response = await fetch(questionURL + deleteQuestionURL,{
			headers:myHeaders,
			method:"POST",
			body: JSON.stringify({"Prompt":prompt}),
		});

		let responseCode = response.status;

		let u = await User.getUser();

		if (responseCode === 401 && iteration === 0){
			await refreshAccessToken();
			return await deleteQuestion(prompt, 1);
		}else if (responseCode === 401 && iteration === 1 && u === undefined){
			router.navigate("/Login");
			return;
		}else if (responseCode === 401){
			router.navigate('/MainPage');
		}

		console.log(response.status);
	}catch(error){
		console.error("Error: ", error);
	}
}

export async function updatePrompt(oldPrompt, newPrompt, iteration = 0){
	try{
		const myHeaders = new Headers();
		myHeaders.append("Content-Type", "application/json");

		let accessToken = "Bearer " + btoa(localStorage.getItem("AccessToken"));

		myHeaders.append("Authorization", accessToken);

		const response = await fetch(questionURL + updateQuestionPromptURL,{
			headers:myHeaders,
			method:"POST",
			body: JSON.stringify({"Prompts":[oldPrompt, newPrompt]}),
		});

		let responseCode = response.status;

		let u = await User.getUser();

		if (responseCode === 401 && iteration === 0){
			await refreshAccessToken();
			return await updatePrompt(oldPrompt, newPrompt, 1);
		}else if (responseCode === 401 && iteration === 1 && u === undefined){
			router.navigate("/Login");
			return;
		}else if (responseCode === 401){
			router.navigate('/MainPage');
		}

		if (!response.ok){
			console.log(response);
		}
		return true;
	}catch(error){
		console.error("Error: ", error);
		return false;
	}
}

export async function updateCorrectAnswer(prompt, newCorrectAnswer, iteration = 0){
	try{
		const myHeaders = new Headers();
		myHeaders.append("Content-Type", "application/json");

		let accessToken = "Bearer " + btoa(localStorage.getItem("AccessToken"));

		myHeaders.append("Authorization", accessToken);

		const response = await fetch(questionURL + updateCorrectAnswerURL,{
			headers:myHeaders,
			method:"POST",
			body: JSON.stringify({"Prompt and Answer":[prompt, newCorrectAnswer]}),
		});

		let responseCode = response.status;

		let u = await User.getUser();

		if (responseCode === 401 && iteration === 0){
			await refreshAccessToken();
			return await updateCorrectAnswer(prompt, newCorrectAnswer, 1);
		}else if (responseCode === 401 && iteration === 1 && u === undefined){
			router.navigate("/Login");
			return;
		}else if (responseCode === 401){
			router.navigate('/MainPage');
		}

		if (!response.ok){
			console.log(response);
		}

		return true;

	}catch(error){
		console.error("Error: ", error);
		return false;
	}
}

export async function updateWrongAnswer(prompt, answerId, wrongAnswer, iteration = 0){
	try{
		const myHeaders = new Headers();
		myHeaders.append("Content-Type", "application/json");

		let accessToken = "Bearer " + btoa(localStorage.getItem("AccessToken"));

		myHeaders.append("Authorization", accessToken);

		const response = await fetch(questionURL + updateWrongAnswerURL,{
			headers:myHeaders,
			method:"POST",
			body: JSON.stringify({"Prompt":prompt, "Wrong Answer":wrongAnswer, "Answer Id":answerId}),
		});

		let responseCode = response.status;

		let u = await User.getUser();

		if (responseCode === 401 && iteration === 0){
			await refreshAccessToken();
			return await updateWrongAnswer(prompt, answerId, wrongAnswer, 1);
		}else if (responseCode === 401 && iteration === 1 && u === undefined){
			router.navigate("/Login");
			return;
		}else if (responseCode === 401){
			router.navigate('/MainPage');
		}

		if (!response.ok){
			console.log(response);
		}

		return true;
	}catch(error){
		console.error("Error: ", error);
		return false;
	}
}

export async function deleteWrongAnswer(prompt, wrongAnswer, iteration = 0){
	try{
		const myHeaders = new Headers();
		myHeaders.append("Content-Type", "application/json");

		let accessToken = "Bearer " + btoa(localStorage.getItem("AccessToken"));

		myHeaders.append("Authorization", accessToken);

		const response = await fetch(questionURL + deleteWrongAnswerURL,{
			headers:myHeaders,
			method:"POST",
			body: JSON.stringify({"Prompt and Wrong Answer": [prompt, wrongAnswer]}),
		});

		let responseCode = response.status;

		let u = await User.getUser();

		if (responseCode === 401 && iteration === 0){
			await refreshAccessToken();
			return await deleteWrongAnswer(prompt, wrongAnswer, 1);
		}else if (responseCode === 401 && iteration === 1 && u === undefined){
			router.navigate("/Login");
			return;
		}else if (responseCode === 401){
			router.navigate('/MainPage');
		}

		if (!response.ok){
			console.log(response);
		}

		return true;
	}catch(error){
		console.error("Error: ", error);
		return false;
	}
}

export async function deleteImage(imageName, iteration = 0){
	try{
		const myHeaders = new Headers();
		myHeaders.append("Content-Type", "application/json");

		let accessToken = "Bearer " + btoa(localStorage.getItem("AccessToken"));

		myHeaders.append("Authorization", accessToken);

		const response = await fetch(questionURL + deleteImageURL,{
			headers:myHeaders,
			method:"POST",
			body: JSON.stringify({"Image Name": imageName}),
		});

		let responseCode = response.status;

		let u = await User.getUser();

		if (responseCode === 401 && iteration === 0){
			await refreshAccessToken();
			return await deleteImage(imageName, 1);
		}else if (responseCode === 401 && iteration === 1 && u === undefined){
			router.navigate("/Login");
			return;
		}else if (responseCode === 401){
			router.navigate('/MainPage');
		}

		if (!response.ok){
			console.log(response);
		}

		return true;
	}catch(error){
		console.error("Error: ", error);
		return false;
	}
}

export async function addWrongAnswer(prompt, wrongAnswer, iteration = 0){
	try{
		const myHeaders = new Headers();
		myHeaders.append("Content-Type", "application/json");

		let accessToken = "Bearer " + btoa(localStorage.getItem("AccessToken"));

		myHeaders.append("Authorization", accessToken);

		const response = await fetch(questionURL + addWrongAnswerURL,{
			headers:myHeaders,
			method:"POST",
			body: JSON.stringify({"Prompt and Wrong Answer": [prompt, wrongAnswer]}),
		});

		let responseCode = response.status;

		let u = await User.getUser();

		if (responseCode === 401 && iteration === 0){
			await refreshAccessToken();
			return await getNumberQuestions(prompt, wrongAnswer, 1);
		}else if (responseCode === 401 && iteration === 1 && u === undefined){
			router.navigate("/Login");
			return;
		}else if (responseCode === 401){
			router.navigate('/MainPage');
		}

		if (!response.ok){
			console.log(response);
		}

		return true;
	}catch(error){
		console.error("Error: ", error);
		return false;
	}
}

export async function addImage(prompt, image, imageType, iteration = 0){
	try{
		const myHeaders = new Headers();
		myHeaders.append("Content-Type", "application/json");

		let accessToken = "Bearer " + btoa(localStorage.getItem("AccessToken"));

		myHeaders.append("Authorization", accessToken);

		const response = await fetch(questionURL + addImageURL,{
			headers:myHeaders,
			method:"POST",
			body: JSON.stringify({"prompt": prompt,
				"image":image,
				"imageType":imageType
			}),
		});

		let responseCode = response.status;

		let u = await User.getUser();

		if (responseCode === 401 && iteration === 0){
			await refreshAccessToken();
			return await addImage(prompt, image, imageType, 1);
		}else if (responseCode === 401 && iteration === 1 && u === undefined){
			router.navigate("/Login");
			return;
		}else if (responseCode === 401){
			router.navigate('/MainPage');
		}

		if (!response.ok){
			console.log(response);
		}

		return true;
	}catch(error){
		console.error("Error: ", error);
		return false;
	}
}

export async function updateTaskLetter(prompt, taskLetter, iteration = 0){
	try{
		const myHeaders = new Headers();
		myHeaders.append("Content-Type", "application/json");

		let accessToken = "Bearer " + btoa(localStorage.getItem("AccessToken"));

		myHeaders.append("Authorization", accessToken);

		const response = await fetch(questionURL + updateTaskLetterURL,{
			headers:myHeaders,
			method:"POST",
			body: JSON.stringify({"Prompt and Task Letter":[prompt, taskLetter]}),
		});

		let responseCode = response.status;

		let u = await User.getUser();

		if (responseCode === 401 && iteration === 0){
			await refreshAccessToken();
			return await updateTaskLetter(prompt, taskLetter, 1);
		}else if (responseCode === 401 && iteration === 1 && u === undefined){
			router.navigate("/Login");
			return;
		}else if (responseCode === 401){
			router.navigate('/MainPage');
		}

		if (!response.ok){
			console.log(response);
		}

		return true;

	}catch(error){
		console.error("Error: ", error);
		return false;
	}
}

export async function updateJustification(prompt, justification, iteration = 0){
	try{
		const myHeaders = new Headers();
		myHeaders.append("Content-Type", "application/json");

		let accessToken = "Bearer " + btoa(localStorage.getItem("AccessToken"));

		myHeaders.append("Authorization", accessToken);

		const response = await fetch(questionURL + updateJustificationURL,{
			headers:myHeaders,
			method:"POST",
			body: JSON.stringify({"Prompt and Justification":[prompt, justification]}),
		});

		let responseCode = response.status;

		let u = await User.getUser();

		if (responseCode === 401 && iteration === 0){
			await refreshAccessToken();
			return await updateJustification(prompt, justification, 1);
		}else if (responseCode === 401 && iteration === 1 && u === undefined){
			router.navigate("/Login");
			return;
		}else if (responseCode === 401){
			router.navigate('/MainPage');
		}

		if (!response.ok){
			console.log(response);
		}

		return true;

	}catch(error){
		console.error("Error: ", error);
		return false;
	}
}

export async function uploadFile(formData, iteration = 0){
	const myHeaders = new Headers();

	let accessToken = "Bearer " + btoa(localStorage.getItem("AccessToken"));

	myHeaders.append("Authorization", accessToken);

	try{
		const resp = await fetch(questionURL + fileUploadURL, {
			headers:myHeaders,
			method: 'POST',
			body: formData
		});

		let responseCode = resp.status;

		let u = await User.getUser();

		if (responseCode === 401 && iteration === 0){
			await refreshAccessToken();
			return await uploadFile(formData, 1);
		}else if (responseCode === 401 && iteration === 1 && u === undefined){
			router.navigate("/Login");
			return;
		}else if (responseCode === 401){
			router.navigate('/MainPage');
		}

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

export async function getImage(imageName, iteration = 0){
	const params = new URLSearchParams();
	params.append("ImageName", imageName);

	const myHeaders = new Headers();

	let accessToken = "Bearer " + btoa(localStorage.getItem("AccessToken"));

	myHeaders.append("Authorization", accessToken);

	try{
		const resp = await fetch (questionURL + getImagesURL + "?" + params,{
			headers:myHeaders,
		});

		let responseCode = resp.status;

		let u = await User.getUser();

		if (responseCode === 401 && iteration === 0){
			await refreshAccessToken();
			return await getImage(imageName, 1);
		}else if (responseCode === 401 && iteration === 1 && u === undefined){
			router.navigate("/Login");
			return;
		}else if (responseCode === 401){
			router.navigate('/MainPage');
		}

		if (!resp.ok){
			console.error("Error fetching...");
			return;
		}else if (responseCode === 401){
			router.navigate('/MainPage');
		}

		const blob = await resp.blob();

		const image = URL.createObjectURL(blob);

		return image;
	}catch (e){
		console.error("Error: ", e);
	}
}

export async function getUser(iteration = 0){
	const myHeaders = new Headers();

	let accessToken = "Bearer " + btoa(localStorage.getItem("AccessToken"));

	myHeaders.append("Authorization", accessToken);
	try{
		const response = await fetch(questionURL + getUserInfoURL, {
			headers:myHeaders,
		});

		let responseCode = response.status;

		if (responseCode === 401 && iteration === 0){
			await refreshAccessToken();
			return await getUser(1);
		}else if (responseCode === 401 && iteration === 1){
			router.navigate("/Login");
			return;
		}else if (responseCode === 401){
			router.navigate('/MainPage');
		}

		if (!response.ok){
			console.log("Bad response: " + response.status);
			return;
		}

		const json = await response.json();

		const settingsJson = await getSettings();

		if (settingsJson == null){
			return;
		}

		let s = new Setting(settingsJson["numQuestions"], settingsJson["timeLimit"]);
		s.setSettingId(settingsJson["settingId"]);

		currentUser = new user(json["firstName"], json["lastName"], json["email"], s);
		currentUser.getPermissions().setAddQuestions(json["permissions"]["addQuestions"]);
		currentUser.getPermissions().setDeleteQuestions(json["permissions"]["deleteQuestions"]);
		currentUser.getPermissions().setUpdateQuestions(json["permissions"]["updateQuestions"]);
		currentUser.getPermissions().setViewQuestions(json["permissions"]["viewQuestions"]);

		return currentUser;
	}catch (e){
		console.error("Error: ", e);
	}

	return currentUser;
}

export async function changePassword(password, iteration = 0){
	const myHeaders = new Headers();

	let accessToken = "Password " + btoa(localStorage.getItem("AccessToken"));
	password = btoa(password);

	myHeaders.append("Authorization", accessToken);
	myHeaders.append("Password", password);
	try{
		const response = await fetch(authURL, {
			headers:myHeaders,
			method:'POST',
			credentials: "include",
		});

		let responseCode = response.status;

		let u = await User.getUser();

		if (responseCode === 401 && iteration === 0){
			await refreshAccessToken();
			return await changePassword(password, 1);
		}else if (responseCode === 401 && iteration === 1 && u === undefined){
			router.navigate("/Login");
			return;
		}else if (responseCode === 401){
			router.navigate('/MainPage');
		}

		if (!response.ok){
			console.log("Bad response: " + response.status);
			return false;
		}

		return true;
	}catch (e){
		console.error("Error: ", e);
		return false;
	}
}

export async function getSettings(iteration = 0){
	const myHeaders = new Headers();

	let accessToken = "Bearer " + btoa(localStorage.getItem("AccessToken"));

	myHeaders.append("Authorization", accessToken);
	try{
		const response = await fetch(settingsURL + getSettingsURL, {
			headers:myHeaders,
		});

		let responseCode = response.status;

		if (responseCode === 401 && iteration === 0){
			await refreshAccessToken();
			return await getSettings(1);
		}else if (responseCode === 401 && iteration === 1){
			router.navigate("/Login");
			return;
		}

		if (!response.ok){
			console.log("Bad response: " + response.status);
			return null;
		}

		return await response.json();
	}catch (e){
		console.error("Error: ", e);
		return null;
	}
}

export async function updateSettings(s, iteration = 0){
	const myHeaders = new Headers();

	let accessToken = "Bearer " + btoa(localStorage.getItem("AccessToken"));

	myHeaders.append("Authorization", accessToken);
	try{
		const response = await fetch(settingsURL + updateSettingsURL, {
			headers:myHeaders,
			method:'POST',
			body: JSON.stringify(s),
		});

		let responseCode = response.status;

		let u = await User.getUser();

		if (responseCode === 401 && iteration === 0){
			await refreshAccessToken();
			return await updateSettings(s, 1);
		}else if (responseCode === 401 && iteration === 1 && u === undefined){
			router.navigate("/Login");
			return;
		}else if (responseCode === 401){
			router.navigate('/MainPage');
		}

		if (!response.ok){
			console.log("Bad response: " + response.status);
			return false;
		}

		return true;
	}catch (e){
		console.error("Error: ", e);
		return null;
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