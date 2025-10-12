import * as db from './Database';

let currentUser = undefined;

async function getCurrentUser(){
	return await db.getUser();
}

export async function getUser(){
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

export class Permission{
	constructor(){
		this.addQuestions = false;
		this.deleteQuestions = false;
		this.updateQuestions = false;
		this.viewQuestions = false;
	}

	setAddQuestions(addQuestions){
		this.addQuestions = addQuestions;
	}
	
	getAddQuestions(){
		return this.addQuestions;
	}

	setDeleteQuestions(deleteQuestions){
		this.deleteQuestions = deleteQuestions;
	}
	
	getDeleteQuestions(){
		return this.deleteQuestions;
	}

	setUpdateQuestions(updateQuestions){
		this.updateQuestions = updateQuestions;
	}

	getUpdateQuestions(){
		return this.updateQuestions;
	}

	setViewQuestions(viewQuestions){
		this.viewQuestions = viewQuestions;
	}

	getViewQuestions(){
		return this.viewQuestions;
	}
}

export class user{
	constructor(fName, lName, uName, sett){
		this.firstName = fName;
		this.lastName = lName;
		this.username = uName;
		this.settings = sett;
		this.permissions = new Permission();
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