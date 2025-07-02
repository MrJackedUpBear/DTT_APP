import * as db from './Database.js';

let firstName = '';
let lastName = '';
let id = '';
let username = '';
let email = '';
let permissions = new Map();
let settings = new Map();


export function getFirstName(){
	return firstName;
}

export function getLastName(){
	return lastName;
}

export function getId(){
	return id;
}

export function getUsername(){
	return username;
}

export function getEmail(){
	return email;
}

export function getPermissions(){
	return permissions;
}

export function getSettings(){
	return settings;
}

export function setFirstName(name){
	if (id === ''){
		return;
	}
	firstName = name;
}

export function setLastName(name){
	if (id === ''){
		return;
	}
	lastName = name;
}

export function setId(input){
	id = input;

	let userInfo = db.getUserInfo(id);

	if (userInfo === undefined){
		id = '';
		firstName = '';
		lastName = '';
		username = '';
		email = '';
		permissions = new Map();
		settings = new Map();
		return;
	}

	firstName = userInfo.getFirstName();
	lastName = userInfo.getLastName();
	username = userInfo.getUsername();
	email = userInfo.getEmail();
	permissions = userInfo.getPermissions();
	settings = userInfo.getSettings();
}

export function setUsername(input){
	if (id === ''){
		return;
	}
	username = input;
}

export function setEmail(input){
	if (id === ''){
		return;
	}
	email = input;
}

export function setPermissions(input){
	if (id === ''){
		return;
	}
	permissions = input;
}

export function setSettings(input){
	if (id === ''){
		return;
	}

	settings = input;
}

export function updateUserInfo(userInfo){
	if ((typeof userInfo) !== 'object' || id === ''){
		return;
	}

	let valueTypes = ['First Name', 'Last Name', 'Username', 
		'Email', 'Permissions', 'Settings'];

	for (let i = 0; i < valueTypes.length; i++){
		if (userInfo.get(valueTypes[i]) !== undefined){
			switch(valueTypes[i]){
				case 'First Name':
					firstName = userInfo.get(valueTypes[i]);
					break;
				case 'Last Name':
					lastName = userInfo.get(valueTypes[i]);
					break;
				case 'Username':
					username = userInfo.get(valueTypes[i]);
					break;
				case 'Email':
					email = userInfo.get(valueTypes[i]);
					break;
				case 'Permissions':
					permissions = userInfo.get(valueTypes[i]);
					break;
				case 'Settings':
					settings = userInfo.get(valueTypes[i]);
					break;	
				default:
					return;
			}
		}
	}

	userInfo.set('First Name', firstName);
	userInfo.set('Last Name', lastName);
	userInfo.set('Username', username);
	userInfo.set('Email', email);
	userInfo.set('Permissions', permissions);
	userInfo.set('Settings', settings);

	db.setUserInfo(id, userInfo);
}