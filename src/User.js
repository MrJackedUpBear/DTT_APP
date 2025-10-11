import * as db from './Database';

let currentUser = undefined;

async function getCurrentUser(){
	return await db.getUser();
}

export async function getUser(){
	console.trace("Called from: ");

	if (currentUser === undefined){
		currentUser = await getCurrentUser();
	}

	return currentUser;
}

export class Setting{
	constructor(numQuestions, timeLimit){
		this.numQuestions = numQuestions;
		this.timeLimit = timeLimit;
		this.settingId = -1;
	}

	setNumQuestions(numQuestions){
		this.numQuestions = numQuestions;
	}

	setTimeLimit(timeLimit){
		this.timeLimit = timeLimit;
	}

	setSettingId(settingId){
		this.settingId = settingId;
	}

	getNumQuestions(){
		return this.numQuestions;
	}

	getTimeLimit(){
		return this.timeLimit;
	}

	getSettingId(){
		return this.settingId;
	}
}

export class user{
	constructor(fName, lName, uName, sett, perm){
		this.firstName = fName;
		this.lastName = lName;
		this.username = uName;
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

	setSettings(settings){
		this.settings = settings;
	}
}