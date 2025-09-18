import router from './index.js';
import * as questions from './Questions.js';
import {useEffect, useState} from 'react';
import {Form} from 'react-router-dom';
import { Text, View } from 'react-native';
import * as db from './Database.js';
import settings from './settings-svgrepo-com.svg';
import landingPage from './landing-page-web-design-svgrepo-com.svg';
import home from './home-svgrepo-com.svg';
import back from './back-svgrepo-com.svg';
import { toast, ToastContainer } from 'react-toastify';
import hamburger from './burger-menu-svgrepo-com.svg';

let page = 1;
let numOnPage = 15;
let end = 0;
let start = 0;
let questionPrompt = "";
let allQuestions = [];
let questionNum = -1;
let questionsFromFile;
let questionToEdit = [];
let qTotal = 0;
let numPages = 0;
let currentQuestion;

async function verifyTokens(){
  if (!await db.verifyTokens()){
    router.navigate("/Login");
  }
}

export function setPage(num){
    page = num;
}

export function AddQuestions(){
    verifyTokens();

    const [file, setFile] = useState(null);

    const handleSubmit = async (event) => {
        event.preventDefault();
        const formData = new FormData(event.target);

        await QuestionsSubmittedPage(formData);
    }

    const handleFileUpload = async (event) =>{
        event.preventDefault();
        const formData = new FormData();
        formData.append("PDF", file);
        questionsFromFile = await db.uploadFile(formData);

        if (questionsFromFile !== null){
            router.navigate('Verify')
        }else{
            alert("Unsupported file type. Must be a pdf file.");
        }
    }

    return (<div className="App">
        <header className="App-header">
            <button onClick={() => router.navigate("/MainPage")}>Home</button>
            <button onClick={() => router.navigate("/MainPage/Questions")}>Back</button>
            Enter prompt, correct answer, and wrong answers.
        </header>
        <Form onSubmit={handleSubmit}>
            <div>
                <label>
                    Prompt: 
                    <input type="text" id="prompt" name="prompt"/>
                </label>
                <br/>
                <label>
                    Correct Answer:
                    <input type="text" id="correctAnswer" name="correctAnswer"/>
                </label>
                <br/>
                <label>
                    Wrong Answers:
                    <input type="text" id="wrongAnswer1" name="wrongAnswer1"/>
                    <input type="text" id="wrongAnswer2" name="wrongAnswer2"/>
                    <input type="text" id="wrongAnswer3" name="wrongAnswer3"/>
                </label>
                <br/>
                <label>
                    Justification: 
                    <input type="text" id="description" name="description"/>
                </label>
                <br/>
                <label>
                    Tasklist Domain Letter:
                    <input type="text" id="taskLetter" name="taskLetter"/>
                </label>
                <br/>
                <label>
                    Image Upload:
                    <input type="file" id="image" name="image"/>
                </label>
                <br/>
                <input type="submit" value="Submit"/>
            </div>
        </Form>
        <form onSubmit={handleFileUpload}>
            <h1>IBT Exam PDF Upload</h1>
            <input type="file" onChange={(e) => setFile(e.target.files[0])}/>
            <button type="submit">
                Upload file
            </button>
        </form>
    </div>);
}

export async function QuestionsSubmittedPage(formData){
    verifyTokens();

    const readFile = (file) => {
        return new Promise((resolve, reject) => {
            const reader = new FileReader();

            //.replace('data:', '').replace(/^.+,/, '')
            reader.onload = () => resolve(reader.result);
            reader.onerror = () => reject(reader.error);

            reader.readAsDataURL(file);
        });
    };

    let prompts = [];
    let wrongAnswers = [];
    let correctAnswers = [];

    let prompt = formData.get("prompt").trim();
    let correctAnswer = formData.get("correctAnswer").trim();
    let wrongAnswer1 = formData.get("wrongAnswer1").trim();
    let wrongAnswer2 = formData.get("wrongAnswer2").trim();
    let wrongAnswer3 = formData.get("wrongAnswer3").trim();
    let taskLetter = formData.get("taskLetter").trim();
    let description = formData.get("description").trim();
    let image = formData.get("image");

    if (prompt === "" || correctAnswer === "" || wrongAnswer1 === "" ||
        wrongAnswer2 === "" || wrongAnswer3 === ""
    ){
        alert("Please fill out all values.");
        return;
    }

    let question = new db.question();

    question.setQuestion(prompt);
    question.setCorrectAnswer(correctAnswer);
    question.addWrongAnswer(wrongAnswer1);
    question.addWrongAnswer(wrongAnswer2);
    question.addWrongAnswer(wrongAnswer3);
    question.setJustification(description);
    question.setTaskLetter(taskLetter);

    const reader = new FileReader();
    let fileData = await readFile(image);

    let typeOfPhoto = fileData.substring(0, fileData.indexOf(";base64"));
    fileData = fileData.replace('data:', '').replace(/^.+,/, '');

    if (fileData !== ""){
        typeOfPhoto = typeOfPhoto.split("data:image/")[1];
        question.setImage(fileData, typeOfPhoto);
    }

    let ques = [];
    ques.push(question);

    await questions.addQuestions(ques);
    router.navigate("Submit");
}

export function VerifyQuestions(){
    verifyTokens();
    questionToEdit = questionsFromFile;

    const handleClick = async (event) => {
        let prompts = [];
        let answers = [];
        let wrongAnswers = [];
        let que = [];

        for (let i = 0; i < questionToEdit.length; i++){
            let question = new db.question();
            question.setQuestion(questionToEdit[i].getQuestion());
            question.setCorrectAnswer(questionToEdit[i].getCorrectAnswer());

            let wrongLen = questionToEdit[i].getWrongAnswers().length;

            let temp = [];
            for (let j = 0; j < wrongLen; j++){
                question.addWrongAnswer(questionToEdit[i].getWrongAnswers()[j]);
            }

            que.push(question);
        }

        await questions.addQuestions(que);
        alert("Successfully added questions.");
        router.navigate('/MainPage/Questions');
    }

    return (
        <div>
            <div className="App-header">
                Questions From File:
            </div>
            <button onClick={() => router.navigate("/MainPage")}>Home</button>
            <button onClick={() => router.navigate(-1)}>Back</button>
            {ShowQuestions(questionsFromFile, false)}
            <button onClick={handleClick}>Add Questions</button>
        </div>
    );
}

export function SubmitAddQuestions(){
    verifyTokens();
    return (<div>
        Questions added.
        <button onClick={() => router.navigate("/MainPage/Questions/Add")}>Add Another Question</button>
        <button onClick={() => router.navigate("/MainPage/Questions")}>Back to Questions</button>
    </div>);
}

export function ViewAndUpdate(){
    verifyTokens();

    return (<div>
        <div className="navBar">
            <div className="hamburgerMenu">
                <button onClick={toggleHamburgerMenu}><img src={hamburger} alt="Hamburger"/></button>
            </div>

            <div className="landingPage" id="landingPage">
                <button onClick={() => router.navigate('/')}><img src={landingPage} alt="Landing Page"/></button>
            </div>
            <div className="home" id="home">
                <button onClick={() => router.navigate("/MainPage")}><img src={home} alt="Home"/></button>
            </div>
            <h1 className="title">
                DTT Quiz App - Questions
            </h1>
            <div className="settings" id="settings">
                <button onClick={() => router.navigate('/MainPage/Settings')}><img src={settings} alt="Settings"/></button>
            </div>
        </div>
        <div className="Pages">{addPageNums()}</div>
        <button onClick={() => router.navigate('Add')}>Add Questions</button>
        <div className="loadQuestions">
            <h1>All Questions</h1>
            Total Questions: {qTotal}
            {LoadQuestions()}
        </div>
        <footer className="Pages">{addPageNums()}</footer>
    </div>);
}

function toggleHamburgerMenu(){
  const landingButton = document.getElementById("landingPage");
  const settingButton = document.getElementById("settings");
  const homeButton = document.getElementById("home");
  if (landingButton.style.display === 'none' || landingButton.style.display === ''){
    landingButton.style.display = 'block';
    settingButton.style.display = 'block';
    homeButton.style.display = 'block';
  }else{
    landingButton.style.display = 'none';
    settingButton.style.display = 'none';
    homeButton.style.display = 'none';
  }
}

function addPageNums(){
    let pages = [];

    for (let i = 0; i < numPages; i++){
        pages.push((i + 1));
    }

    const handleClick = (pageNum) =>{
        page = pageNum + 1;

        router.navigate("Back");
    }

    return (
        <div className="pages" id="pages">
            {pages.map((item, index) =>(
                <span key={index}>
                    <button onClick={() => handleClick(item)}>{item}</button>
                </span>
            ))}
        </div>
    );
}

export function Next(){
    page++;
    router.navigate('/MainPage/Questions/');
}

export function Back(){
    page--;
    router.navigate('/MainPage/Questions/');
}

function LoadQuestions(){
    verifyTokens();
    questionToEdit = allQuestions;
    const [data, setData] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    useEffect(() => {
        const fetchData = async () => {
            try {
                //For testing auth servlet
                //await db.getEmailAuth("mrjackedupbear@gmail.com");
                end = page * numOnPage;
                start = end - numOnPage;
                qTotal = -1;
                
                qTotal = await questions.getNumberQuestions();
                numPages = Math.ceil(qTotal/numOnPage);

                if (qTotal < end){
                    end = qTotal;
                    setData(await questions.getQuestionsFrom(start, qTotal));
                }else{
                    setData(await questions.getQuestionsFrom(start, end));
                }
            }catch(error){
                setError(error);
            }finally{
                setLoading(false);
            }
        };

        if (data === null){
            fetchData();
        }
    }, []);

    const handleClick = async (event, prompt, index, wrongAnswer = '', wrongAnswerInput = '', imageName = '') => {
        allQuestions = " ";

        const className = event.target.className;

        setCurrentq(prompt);

        questionPrompt = prompt;
        questionNum = index;

        if (className === 'Delete'){
            let confirmation = window.confirm("Are you sure you want to delete: \"" + prompt + "\"?");

            if (!confirmation){
                return;
            }

            questions.deleteQuestion(prompt);
            alert("Successfully deleted: " + prompt);
            page++;
            router.navigate('Back');
        }else if (className === 'Edit'){
            currentQuestion = prompt;
            router.navigate('Edit');
        }
        else{
            alert("Button doesn't work yet.");
        }
    }

    const [currentq, setCurrentq] = useState(new db.question());
    
    //getCurrentQuestion();
    return (<h1>
        {loading && <p>Loading data...</p>}
        {error && <p>Error: {error.message}</p>}
        {data && (
        <div className="questions">
            {data.map((item, index) =>(
                <div className="question">
                    <h1 className="questionNum">
                        Question: {(index + 1) + (numOnPage * (page - 1))}
                    </h1>
                    <div className="PromptAndTask">
                        <td className="taskLetter">
                        <h3>Category:</h3>
                        {item.getTaskLetter()} - {item.getTaskLetterDesc()}
                        <br/>
                        </td>

                    <td className="prompt">
                            <h3>Prompt:</h3>
                        {item.getQuestion()} {' '}
                    </td>
                    </div>
                    <br/>
                    <div className="Buttons">
                        <button className='Edit' onClick={(e) => handleClick(e, item, index)}>Edit</button>                    
                        <button className='Delete' onClick={(e) => handleClick(e, item.getQuestion(), index)}>Delete Question?</button>
                    </div>
                
                <div className="afterQuestion"> </div>
                <div>
                    <ToastContainer position="top-right" autoClose={3000} />
                </div>
                </div>
                
            ))}
            {ShowQuestions()}
        </div>
        )}
    </h1>);
}

const showTimedMessage = (settingUpdated) => {
    toast.success("Successfully updated " + settingUpdated + "!", {
        position: "top-right", // Can override the default from ToastContainer
        autoClose: 5000,      // Can override the default from ToastContainer
        hideProgressBar: false,
        closeOnClick: true,
        pauseOnHover: true,
        draggable: true,
        progress: undefined,
    });
};

const showDeleteMessage = (settingUpdated) => {
    toast.success("Successfully deleted " + settingUpdated + "!", {
        position: "top-right", // Can override the default from ToastContainer
        autoClose: 5000,      // Can override the default from ToastContainer
        hideProgressBar: false,
        closeOnClick: true,
        pauseOnHover: true,
        draggable: true,
        progress: undefined,
    });
};

const showAddedMessage = (settingUpdated) => {
    toast.success("Successfully added " + settingUpdated + "!", {
        position: "top-right", // Can override the default from ToastContainer
        autoClose: 5000,      // Can override the default from ToastContainer
        hideProgressBar: false,
        closeOnClick: true,
        pauseOnHover: true,
        draggable: true,
        progress: undefined,
    });
};

export function EditQuestions(){
    const handleClick = async (event, prompt, wrongAnswer = '', wrongAnswerInput = '', imageName = '', imageId = -1) => {
        allQuestions = " ";
        const resetAll = () =>{
            setEditTaskLetter(false);
            setEditPrompt(false);
            setEditWrongAnswer(false);
            setEditCorrectAnswer(false);
            setEditJustification(false);
            setAddImage(false);
        }

        const className = event.target.className;

        questionPrompt = prompt;

        if (className === 'Delete'){
            let confirmation = window.confirm("Are you sure you want to delete: \"" + prompt + "\"?");

            if (!confirmation){
                return;
            }

            questions.deleteQuestion(prompt);
            showTimedMessage(prompt);
            page++;
            router.navigate('Back');
        }else if (className === 'editTaskLetter'){
            if (editTaskLetter === true){
                resetAll();
                setEditTaskLetter(false);
            }else{
                resetAll();
                setEditTaskLetter(true);
            }
        }else if (className === 'editPrompt'){
            if (editPrompt === true){
                resetAll();
                setEditPrompt(false);
            }else{
                resetAll();
                setEditPrompt(true);
            }
        }else if (className === 'editWrongAnswer'){
            if (editWrongAnswer === true){
                setCurrentWrongAnswer(wrongAnswer);
                resetAll();
                setEditWrongAnswer(false);
            }else{
                setCurrentWrongAnswer(wrongAnswer);
                resetAll();
                setEditWrongAnswer(true);
            }
        }else if (className === 'editCorrectAnswer'){
            if (editCorrectAnswer === true){
                resetAll();
                setEditCorrectAnswer(false);
            }else{
                resetAll();
                setEditCorrectAnswer(true);
            }
        }else if (className === 'editJustification'){
            if (editJustification === true){
                resetAll();
                setEditJustification(false);
            }else{
                resetAll();
                setEditJustification(true);
            }
        }else if (className === 'addImage'){
            if (addImage === true){
                resetAll();
                setAddImage(false);
            }else{
                resetAll();
                setAddImage(true);
            }
        }else if (className === 'deleteImage'){
            if (imageName === ""){
                alert("It seems you have just added this image. Please try leaving this question and coming back to delete it.");
                return;
            }

            let confirmation = window.confirm("Are you sure you want to delete: \"" + imageName + "\"?");

            if (!confirmation){
                return;
            }

            if (!await questions.deleteImage(imageName)){
                alert("Error deleting.");
            }else{
                currentQuestion.removeImage(imageId);
                let temp = [];

                for (let i = 0; i < currentQuestion.getImages().length; i++){
                    temp.push(currentQuestion.getImages()[i]);
                }

                setImages(temp);
                showDeleteMessage(imageName);
            }
        }else if (className === 'addWrongAnswer'){
            if (addWrongAnswer){
                resetAll();
                setAddWrongAnswer(false);
            }else{
                resetAll();
                setAddWrongAnswer(true);
            }
        }else if (className === 'deleteWrongAnswer'){
            let confirmation = window.confirm("Are you sure you want to delete: \"" + wrongAnswerInput + "\"?");

            if (!confirmation){
                return;
            }

            if (!await questions.deleteWrongAnswer(prompt, wrongAnswerInput)){
                alert("Error updating");
            }else{
                currentQuestion.removeWrongAnswer(wrongAnswer);
                let newWrongAnswers = [];

                for (let i = 0; i < currentQuestion.getWrongAnswers().length; i++){
                    newWrongAnswers.push(currentQuestion.getWrongAnswers()[i]);
                }

                showDeleteMessage(wrongAnswerInput);
                setWrongAnswers(newWrongAnswers);
            }
        }else if (className === 'Edit'){
            router.navigate('Edit');
        }
        else{
            alert("Button doesn't work yet.");
        }
    }

    const handleSubmit = async (event, prompt, wrongAnswer = '') => {
        event.preventDefault();
        const formData = new FormData(event.target);
        const className = event.target.className;

        if (className === 'submitEditTaskLetter'){
            let newTaskLetter = formData.get("newTaskLetter").trim();
            if (!await questions.updateTaskLetter(prompt, newTaskLetter)){
                alert("Error updating.");
            }else{
                currentQuestion.setTaskLetter(newTaskLetter);
                currentQuestion.setTaskLetterDesc("");
                showTimedMessage("task letter");
            }
            
            setEditTaskLetter(false);
        }else if (className === 'submitEditPrompt'){
            let newPrompt = formData.get("newPrompt").trim();
            if (!await questions.updatePrompt(prompt, newPrompt)){
                alert("Error updating");
            }else{
                currentQuestion.setQuestion(newPrompt);
                showTimedMessage("prompt");
            }

            setEditPrompt(false);
        }else if (className === 'submitEditWrongAnswer'){
            let newWrongAnswer = formData.get("newWrongAnswer").trim();
            if (!await questions.updateWrongAnswer(prompt, wrongAnswer, newWrongAnswer)){
                alert('Error updating.');
            }else{
                currentQuestion.removeWrongAnswer(wrongAnswer);
                currentQuestion.addWrongAnswer(newWrongAnswer);
                
                let newWrongAnswers = [];

                for (let i = 0; i < currentQuestion.getWrongAnswers().length; i++){
                    newWrongAnswers.push(currentQuestion.getWrongAnswers()[i]);
                }

                setWrongAnswers(newWrongAnswers);

                showTimedMessage("wrong answer");
            }

            setEditWrongAnswer(false);
        }else if (className === 'submitEditCorrectAnswer'){
            let newCorrectAnswer = formData.get("newCorrectAnswer").trim();
            if (!await questions.updateCorrectAnswer(prompt, newCorrectAnswer)){
                alert("Error updating.");
            }else{
                currentQuestion.setCorrectAnswer(newCorrectAnswer);
                showTimedMessage("correct answer");
            }
            setEditCorrectAnswer(false);
        }else if (className === 'submitEditJustification'){
            let newJustification = formData.get("newJustification").trim();
            if (!await questions.updateJustification(prompt, newJustification)){
                alert("Error updating.");
            }else{
                currentQuestion.setJustification(newJustification);
                showTimedMessage("justification");
            }
            setEditJustification(false);
        }else if (className === 'submitAddImage'){

            const readFile = (file) => {
                return new Promise((resolve, reject) => {
                    const reader = new FileReader();

                    //.replace('data:', '').replace(/^.+,/, '')
                    reader.onload = () => resolve(reader.result);
                    reader.onerror = () => reject(reader.error);

                    reader.readAsDataURL(file);
                });
            };
            let newImage = formData.get("newImage");
            const reader = new FileReader();
            let fileData = await readFile(newImage);
            let img = fileData;

            let typeOfPhoto = fileData.substring(0, fileData.indexOf(";base64"));
            fileData = fileData.replace('data:', '').replace(/^.+,/, '');

            if (fileData !== ""){
                typeOfPhoto = typeOfPhoto.split("data:image/")[1];
                if (!await questions.addImage(prompt, fileData, typeOfPhoto)){
                    alert("Error adding image.")
                }else{
                    currentQuestion.addImage(img);
                    let temp = [];

                    for (let i = 0; i < currentQuestion.getImages().length; i++){
                        temp.push(currentQuestion.getImages()[i]);
                    }

                    setImages(temp);

                    showAddedMessage("image");
                }
            }

            setAddImage(false);
        }else if (className === 'submitAddWrongAnswer'){
            let newWrongAnswer = formData.get("newWrongAnswer").trim();
            if (!await questions.addWrongAnswer(prompt, newWrongAnswer)){
                alert("Error updating.");
            }else{
                currentQuestion.addWrongAnswer(newWrongAnswer);

                let newWrongAnswers = [];

                for (let i = 0; i < currentQuestion.getWrongAnswers().length; i++){
                    newWrongAnswers.push(currentQuestion.getWrongAnswers()[i]);
                }

                setWrongAnswers(newWrongAnswers);
                showAddedMessage("wrong answer");
            }

            setAddWrongAnswer(false);
        }
    }

    const [editTaskLetter, setEditTaskLetter] = useState(false);

    const [editPrompt, setEditPrompt] = useState(false);
    const [editWrongAnswer, setEditWrongAnswer] = useState(false);
    const [currentWrongAnswer, setCurrentWrongAnswer] = useState(false);
    const [addWrongAnswer, setAddWrongAnswer] = useState(false);

    const [editCorrectAnswer, setEditCorrectAnswer] = useState(false);
    const [editJustification, setEditJustification] = useState(false);
    const [addImage, setAddImage] = useState(false);

    const [wrongAnswers, setWrongAnswers] = useState(currentQuestion.getWrongAnswers());
    const [images, setImages] = useState(currentQuestion.getImages());

    if (currentQuestion === undefined){
        router.navigate("/MainPage/Questions");
        return;
    }

    return (
        <div>
            <div className="navBar">
                <div className="hamburgerMenu">
                    <button onClick={toggleHamburgerMenu}><img src={hamburger} alt="Hamburger"/></button>
                </div>

                <div className="landingPage" id="landingPage">
                    <button onClick={() => router.navigate('/')}><img src={landingPage} alt="Landing Page"/></button>
                </div>
                <div className="home" id="home">
                    <button onClick={() => router.navigate("/MainPage")}><img src={home} alt="Home"/></button>
                </div>
                <div className="back" id="back">
                    <button onClick={() => router.navigate("/MainPage/Questions")}><img src={back} alt="Back"/></button>
                </div>
                <h1 className="title">
                    DTT Quiz App - Questions
                </h1>
                <div className="settings" id="settings">
                    <button onClick={() => router.navigate('/MainPage/Settings')}><img src={settings} alt="Settings"/></button>
                </div>
            </div>

        <div className="question">
            <table>
                <tr>
                    <td className="taskLetter">
                        <h3>Category:</h3>
                        {currentQuestion.getTaskLetter()} - {currentQuestion.getTaskLetterDesc()}
                        <br/>
                        {editTaskLetter ? 
                        <div>
                            <form onSubmit={(e) => handleSubmit(e, currentQuestion.getQuestion())} className="submitEditTaskLetter">
                                <label>Enter New Task Letter: </label> 
                                <input type="text" id="newTaskLetter" name="newTaskLetter"></input>
                                <br/>
                                <input type="submit" className="submit"></input>
                            </form>
                            <button className="editTaskLetter" onClick={(e) => handleClick(e, currentQuestion.getQuestion())}>Cancel</button> 
                        </div>:
                        <button className="editTaskLetter" onClick={(e) => handleClick(e, currentQuestion.getQuestion())}>Edit Task Letter</button>}
                    </td>

                    <td className="prompt">
                            <h3>Prompt:</h3>
                        {currentQuestion.getQuestion()} {' '}
                        <br/>
                        {editPrompt ? 
                        <div>
                            <form onSubmit={(e) => handleSubmit(e, currentQuestion.getQuestion())} className="submitEditPrompt">
                                <label>Enter New Prompt: </label>
                                <input type="text" id="newPrompt" name="newPrompt"></input>
                                <br/>
                                <input type="submit" className="submit"></input>
                            </form>
                            <button className="editPrompt" onClick={(e) => handleClick(e, currentQuestion.getQuestion())}>Cancel</button>
                        </div> : 
                        <button className="editPrompt" onClick={(e) => handleClick(e, currentQuestion.getQuestion())}>Edit Prompt</button>}
                    </td>
                </tr>

                <tr className="topRow">
                    <td className="wrongAnswers">
                            <h3>Wrong Answers:</h3>
                        {wrongAnswers.map((wrongAnswer, wrongAnswerIndex) => (
                            <div>{wrongAnswerIndex + 1}: {' '} {wrongAnswer}
                                <br/>
                                
                                {editWrongAnswer && (currentWrongAnswer === wrongAnswerIndex) ? 
                                <div>
                                    <form onSubmit={(e) => handleSubmit(e, currentQuestion.getQuestion(), wrongAnswer)} className="submitEditWrongAnswer">
                                        <label>Enter New Wrong Answer: </label>
                                        <input type="text" id="newWrongAnswer" name="newWrongAnswer"></input>
                                        <br/>
                                        <input type="submit" className="submit"></input>
                                    </form>
                                    <button className="editWrongAnswer" onClick={(e) => handleClick(e, currentQuestion.getQuestion(), wrongAnswerIndex)}>Cancel</button>
                                </div> : 
                                <button className="editWrongAnswer" onClick={(e) => handleClick(e, currentQuestion.getQuestion(), wrongAnswerIndex)}>Edit Wrong Answer {wrongAnswerIndex + 1}</button>}
                                <button className="deleteWrongAnswer" onClick={(e) => handleClick(e, currentQuestion.getQuestion(), wrongAnswerIndex, wrongAnswer)}>Delete Wrong Answer?</button>
                            </div>
                        ))}
                        {addWrongAnswer ? 
                        <div>
                            <form onSubmit={(e) => handleSubmit(e, currentQuestion.getQuestion())} className="submitAddWrongAnswer">
                                <label>Enter New Wrong Answer: </label>
                                <input type='text' id="newWrongAnswer" name="newWrongAnswer"></input>
                                <br/>
                                <input type="submit" className="submit"></input> 
                            </form>
                            <button className="addWrongAnswer" onClick={(e) => handleClick(e, currentQuestion.getQuestion())}>Cancel</button>
                        </div>:
                        <button className="addWrongAnswer" onClick={(e) => handleClick(e, currentQuestion.getQuestion())}>Add Wrong Answer</button>}
                    </td>

                    <td className="correctAnswer">
                        <h3>Correct Answer:</h3>
                        {currentQuestion.getCorrectAnswer()}
                        <br/>
                        {editCorrectAnswer ? 
                        <div>
                            <form onSubmit={(e) => handleSubmit(e, currentQuestion.getQuestion())} className="submitEditCorrectAnswer">
                                <label>Enter New Correct Answer: </label>
                                <input type="text" id="newCorrectAnswer" name="newCorrectAnswer"></input>
                                <br/>
                                <input type="submit" className="submit"></input>
                            </form>
                            <button className="editCorrectAnswer" onClick={(e) => handleClick(e, currentQuestion.getQuestion())}>Cancel</button>
                        </div> : 
                        <button className="editCorrectAnswer" onClick={(e) => handleClick(e, currentQuestion.getQuestion())}>Edit Correct Answer</button>}
                    </td>

                    <td className="justification">
                        <h3>Justification:</h3>
                        {currentQuestion.getJustification()}
                        <br/>
                        {editJustification ? 
                        <div>
                            <form onSubmit={(e) => handleSubmit(e, currentQuestion.getQuestion())} className="submitEditJustification">
                                <label>Enter New Justification: </label>
                                <input type="text" id="newJustification" name="newJustification"></input>
                                <br/>
                                <input type="submit" className="submit"></input>
                            </form>
                            <button className="editJustification" onClick={(e) => handleClick(e, currentQuestion.getQuestion())}>Cancel</button>
                        </div> : 
                        <button className="editJustification" onClick={(e) => handleClick(e, currentQuestion.getQuestion())}>Edit Justification</button>}
                    </td>

                    <td className="image">
                        <h3>Image:</h3>
                        {images.map((image, imageIndex) => (
                            <div>
                                <img src={image[0]} alt=""/>
                                <button className="deleteImage" onClick={(e) => handleClick(e, currentQuestion.getQuestion(), '', '', currentQuestion.getImageNames()[imageIndex], imageIndex)}>Delete Image?</button>
                                <br/>
                                <br/>
                            </div>
                        ))}
                        {addImage ? 
                        <div>
                            <form onSubmit={(e) => handleSubmit(e, currentQuestion.getQuestion())} className="submitAddImage">
                                <label>Add Image: </label>
                                <input type="file" id="newImage" name="newImage" className="submit"></input>
                                <br/>
                                <input type="submit" className="submit"></input>
                            </form>
                            <button className="addImage" onClick={(e) => handleClick(e, currentQuestion.getQuestion())}>Cancel</button>
                        </div> : 
                        <button className="addImage" onClick={(e) => handleClick(e, currentQuestion.getQuestion())}>Add Image</button>}
                    </td>
                </tr>
            </table>
            </div>
            <div>
                <ToastContainer position="top-right" autoClose={3000} />
            </div>
        </div>
    );
}

function ShowQuestions(data, isAll = true){
    verifyTokens();

    const handleClick = async (event, prompt, index) => {
        const className = event.target.className;

        questionPrompt = prompt;
        questionNum = index;

        if (className === 'Delete'){
            let confirmation = window.confirm("Are you sure you want to delete: \"" + prompt + "\"?");

            if (!confirmation){
                return;
            }

            if (allQuestions !== undefined){
                questions.deleteQuestion(prompt);
            }

            alert("Successfully deleted: " + prompt);
            page++;
            router.navigate('Back');
        }else if (className === 'editTaskLetter'){

        }
        else{
            alert("Button doesn't work yet.");
        }
    }

    if (isAll){
        questionsFromFile = undefined;
        allQuestions = data;
        questionToEdit = allQuestions;
    }else{
        allQuestions = undefined;
        questionToEdit = questionsFromFile;

        return (<div className="questions">
        {data.map((item, index) =>(
            <li className="question">
                <div className="questionNum">
                    Question: {(index + 1) * page}
                </div>
            <div className="taskLetter">
                <h3>Task Letter:</h3>
                {item.getTaskLetter()} - {item.getTaskLetterDesc()} {' '}
                <br/>
                <button className="editTaskLetter" onClick={handleClick}>Edit Task Letter</button>
            </div>
            <div className="prompt">
                <h3>Prompt:</h3>
                {item.getQuestion()} {' '}
                <br/>
                <button className="editPrompt" onClick={handleClick}>Edit Prompt</button>
            </div>
            <div className="wrongAnswers">
                <h3>Wrong Answers:</h3>
                {item.getWrongAnswers().map((wrongAnswer, wrongAnswerIndex) => (
                    <div>{wrongAnswerIndex + 1}: {' '} {wrongAnswer}
                        <br/>
                        <button className="editWrongAnswer" onClick={handleClick}>Edit Wrong Answer {wrongAnswerIndex + 1}</button>
                    </div>
                ))}
            </div>
            <div className="correctAnswer">
                <h3>Correct Answer:</h3>
                {item.getCorrectAnswer()}
                <br/>
                <button className="editCorrectAnswer" onClick={handleClick}>Edit Correct Answer</button>
            </div>
            <div className="justification">
                <h3>Justification:</h3>
                {item.getJustification()}
                <br/>
                <button className="editJustification" onClick={handleClick}>Edit Justification</button>
            </div>
            <div className="image">
                <h3>Image:</h3>
                <img src={item.getImage()} alt=""/>
                <br/>
                <button className="deleteImage" onClick={handleClick}>Delete Image?</button>
                <button className="addImage" onClick={handleClick}>Add Image</button>
            </div>

            <button className='Delete' onClick={(e) => handleClick(e, item.getQuestion(), index)}>Delete Question?</button></li>
        ))}
    </div>);
    }
}

/* Need to put the following code on a separate screen.
<tr className="topRow">
    <td className="wrongAnswers">
            <h3>Wrong Answers:</h3>
        {item.getWrongAnswers().map((wrongAnswer, wrongAnswerIndex) => (
            <div>{wrongAnswerIndex + 1}: {' '} {wrongAnswer}
                <br/>
                
                {editWrongAnswer && (currentq === item.getQuestion() && (currentWrongAnswer === wrongAnswerIndex)) ? 
                <div>
                    <form onSubmit={(e) => handleSubmit(e, item.getQuestion(), wrongAnswerIndex)} className="submitEditWrongAnswer">
                        <label>Enter New Wrong Answer: </label>
                        <input type="text" id="newWrongAnswer" name="newWrongAnswer"></input>
                        <br/>
                        <input type="submit" className="submit"></input>
                    </form>
                    <button className="editWrongAnswer" onClick={(e) => handleClick(e, item.getQuestion(), index, wrongAnswerIndex)}>Cancel</button>
                </div> : 
                <button className="editWrongAnswer" onClick={(e) => handleClick(e, item.getQuestion(), index, wrongAnswerIndex)}>Edit Wrong Answer {wrongAnswerIndex + 1}</button>}
                <button className="deleteWrongAnswer" onClick={(e) => handleClick(e, item.getQuestion(), index, '', wrongAnswer)}>Delete Wrong Answer?</button>
            </div>
        ))}
        {addWrongAnswer && (currentq === item.getQuestion()) ? 
        <div>
            <form onSubmit={(e) => handleSubmit(e, item.getQuestion())} className="submitAddWrongAnswer">
                <label>Enter New Wrong Answer: </label>
                <input type='text' id="newWrongAnswer" name="newWrongAnswer"></input>
                <br/>
                <input type="submit" className="submit"></input> 
            </form>
            <button className="addWrongAnswer" onClick={(e) => handleClick(e, item.getQuestion(), index)}>Cancel</button>
        </div>:
        <button className="addWrongAnswer" onClick={(e) => handleClick(e, item.getQuestion(), index)}>Add Wrong Answer</button>}
    </td>

    <td className="correctAnswer">
        <h3>Correct Answer:</h3>
        {item.getCorrectAnswer()}
        <br/>
        {editCorrectAnswer && (currentq === item.getQuestion()) ? 
        <div>
            <form onSubmit={(e) => handleSubmit(e, item.getQuestion())} className="submitEditCorrectAnswer">
                <label>Enter New Correct Answer: </label>
                <input type="text" id="newCorrectAnswer" name="newCorrectAnswer"></input>
                <br/>
                <input type="submit" className="submit"></input>
            </form>
            <button className="editCorrectAnswer" onClick={(e) => handleClick(e, item.getQuestion(), index)}>Cancel</button>
        </div> : 
        <button className="editCorrectAnswer" onClick={(e) => handleClick(e, item.getQuestion(), index)}>Edit Correct Answer</button>}
    </td>

    <td className="justification">
        <h3>Justification:</h3>
        {item.getJustification()}
        <br/>
        {editJustification && (currentq === item.getQuestion()) ? 
        <div>
            <form onSubmit={(e) => handleSubmit(e, item.getQuestion())} className="submitEditJustification">
                <label>Enter New Justification: </label>
                <input type="text" id="newJustification" name="newJustification"></input>
                <br/>
                <input type="submit" className="submit"></input>
            </form>
            <button className="editJustification" onClick={(e) => handleClick(e, item.getQuestion(), index)}>Cancel</button>
        </div> : 
        <button className="editJustification" onClick={(e) => handleClick(e, item.getQuestion(), index)}>Edit Justification</button>}
    </td>

    <td className="image">
        <h3>Image:</h3>
        {item.getImages().map((image, imageIndex) => (
            <div>
                <img src={image[0]} alt=""/>
                <button className="deleteImage" onClick={(e) => handleClick(e, item.getQuestion(), index, '', '', item.getImageNames()[imageIndex])}>Delete Image?</button>
                <br/>
                <br/>
            </div>
        ))}
        {addImage && (currentq === item.getQuestion()) ? 
        <div>
            <form onSubmit={(e) => handleSubmit(e, item.getQuestion())} className="submitAddImage">
                <label>Add Image: </label>
                <input type="file" id="newImage" name="newImage" className="submit"></input>
                <br/>
                <input type="submit" className="submit"></input>
            </form>
            <button className="addImage" onClick={(e) => handleClick(e, item.getQuestion(), index)}>Cancel</button>
        </div> : 
        <button className="addImage" onClick={(e) => handleClick(e, item.getQuestion(), index)}>Add Image</button>}
    </td>
</tr>

                        
*/