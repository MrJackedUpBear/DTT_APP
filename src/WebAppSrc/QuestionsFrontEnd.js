import router from './index.js';
import * as questions from './Questions.js';
import {useEffect, useState} from 'react';
import {Form} from 'react-router-dom';
import { Text, View } from 'react-native';
import * as db from './Database.js';

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

export function setPage(num){
    page = num;
}

export function AddQuestions(){
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
    return (<div>
        Questions added.
        <button onClick={() => router.navigate("/MainPage/Questions/Add")}>Add Another Question</button>
        <button onClick={() => router.navigate("/MainPage")}>Go Home</button>
    </div>);
}

export function ViewAndUpdate(){

    return (<div>
        <button onClick={() => router.navigate("/MainPage")}>Home</button>
        <button onClick={() => router.navigate('Add')}>Add Questions</button>
        <div className="loadQuestions">
            <h1>All Questions</h1>
            Total Questions: {qTotal}
            {LoadQuestions()}
        </div>
        <footer>Page:{page} of {Math.ceil(qTotal/numOnPage)} {verifyBackPageButton()}{verifyNextPageButton()}</footer>
    </div>);
}

function verifyBackPageButton(){
    const handlClick = () => {
        page = 2;
        router.navigate('Back');
    }

    if (page > 1){
        if (page > 2){
            return (<div>
                <button id="first" onClick={handlClick}>First</button>
                <button onClick={() => router.navigate('Back')}>Back</button>
            </div>);
        }else{
            return (<div>
                <button onClick={() => router.navigate('Back')}>Back</button>
            </div>);
        }
    }
}

function verifyNextPageButton(){
    let numQuestionsOnThisPage = end - start;

    if (numQuestionsOnThisPage !== numOnPage){
        return;
    }else{
        return (<div>
        <button onClick={() => router.navigate('Next')}>Next</button>
        </div>);
    }
}

export function Next(){
    page++;
    if (allQuestions !== undefined){
        router.navigate('/MainPage/Questions/');
    }else{
        router.navigate('/MainPage/Questions/Add/Verify');
    }
}

export function Back(){
    page--;
    router.navigate('/MainPage/Questions/');
}

function LoadQuestions(){
    questionToEdit = allQuestions;
    const [data, setData] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    useEffect(() => {
        const fetchData = async () => {
            try {
                end = page * numOnPage;
                start = end - numOnPage;
                qTotal = -1;
                
                qTotal = await questions.getNumberQuestions();

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

        fetchData();
    }, []);

    const handleClick = async (event, prompt, index, wrongAnswer = '') => {
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
            alert('Deleting image?');
        }else if (className === 'addWrongAnswer'){
            if (addWrongAnswer){
                resetAll();
                setAddWrongAnswer(false);
            }else{
                resetAll();
                setAddWrongAnswer(true);
            }
        }else if (className === 'deleteWrongAnswer'){
            alert('Deleting wrong answer: ' + (wrongAnswer + 1));
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
            alert(newTaskLetter);
            setEditTaskLetter(false);
        }else if (className === 'submitEditPrompt'){
            let newPrompt = formData.get("newPrompt").trim();
            if (await questions.updatePrompt(prompt, newPrompt)){
                alert("Successfully updated.");
            }else{
                alert("Error updating");
            }

            page++;
            router.navigate('Back');
            setEditPrompt(false);
        }else if (className === 'submitEditWrongAnswer'){
            let newWrongAnswer = formData.get("newWrongAnswer").trim();
            if (await questions.updateWrongAnswer(prompt, wrongAnswer, newWrongAnswer)){
                alert("Successfully updated.")
            }else{
                alert('Error updating.');
            }

            page++;
            router.navigate('Back');
            setEditWrongAnswer(false);
        }else if (className === 'submitEditCorrectAnswer'){
            let newCorrectAnswer = formData.get("newCorrectAnswer").trim();
            if (await questions.updateCorrectAnswer(prompt, newCorrectAnswer)){
                alert("Successfully updated.");
            }else{
                alert("Error updating.");
            }
            
            page++;
            router.navigate('Back');
            setEditCorrectAnswer(false);
        }else if (className === 'submitEditJustification'){
            let newJustification = formData.get("newJustification").trim();
            alert(newJustification);
            setEditJustification(false);
        }else if (className === 'submitAddImage'){
            let newImage = formData.get("newImage");
            alert(newImage);
            setAddImage(false);
        }else if (className === 'submitAddWrongAnswer'){
            let newWrongAnswer = formData.get("newWrongAnswer").trim();
            alert(newWrongAnswer);
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
    const [currentq, setCurrentq] = useState('');
    
    //getCurrentQuestion();
    return (<h1>
        {loading && <p>Loading data...</p>}
        {error && <p>Error: {error.message}</p>}
        {data && (
        <div>
        <View style={{justifyContent: 'center', alignItems: 'center', flexShrink: 1}}> 
            <Text style={{flex: 1, flexWrap: 'wrap', fontSize: 20, flexShrink: 1}}> 
            <div className="questions">
                <div>
                    {data.map((item, index) =>(
                        <li style={{border: '1px solid black', padding: '8px'}}>
                        <div className="taskLetter">
                            <h3>Task Letter:</h3>
                            {item.getTaskLetter()} - {item.getTaskLetterDesc()}
                            <br/>
                            {editTaskLetter && (currentq === item.getQuestion()) ? 
                            <div>
                                <form onSubmit={(e) => handleSubmit(e, item.getQuestion())} className="submitEditTaskLetter">
                                    <label>Enter New Task Letter: </label> 
                                    <input type="text" id="newTaskLetter" name="newTaskLetter"></input>
                                    <br/>
                                    <button className="editTaskLetter" onClick={(e) => handleClick(e, item.getQuestion(), index)}>Cancel</button> 
                                    <input type="submit"></input>
                                </form>
                            </div>:
                            <button className="editTaskLetter" onClick={(e) => handleClick(e, item.getQuestion(), index)}>Edit Task Letter</button>}
                        </div>
                        <div className="prompt">
                            <h3>Prompt:</h3>
                            {item.getQuestion()} {' '}
                            <br/>
                            {editPrompt && (currentq === item.getQuestion()) ? 
                            <div>
                                <form onSubmit={(e) => handleSubmit(e, item.getQuestion())} className="submitEditPrompt">
                                    <label>Enter New Prompt: </label>
                                    <input type="text" id="newPrompt" name="newPrompt"></input>
                                    <br/>
                                    <button className="editPrompt" onClick={(e) => handleClick(e, item.getQuestion(), index)}>Cancel</button>
                                    <input type="submit"></input>
                                </form>
                            </div> : 
                            <button className="editPrompt" onClick={(e) => handleClick(e, item.getQuestion(), index)}>Edit Prompt</button>}
                        </div>
                        <div className="wrongAnswers">
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
                                            <button className="editWrongAnswer" onClick={(e) => handleClick(e, item.getQuestion(), index, wrongAnswerIndex)}>Cancel</button>
                                            <input type="submit"></input>
                                        </form>
                                    </div> : 
                                    <button className="editWrongAnswer" onClick={(e) => handleClick(e, item.getQuestion(), index, wrongAnswerIndex)}>Edit Wrong Answer {wrongAnswerIndex + 1}</button>}
                                    <button className="deleteWrongAnswer" onClick={(e) => handleClick(e, item.getQuestion(), index, wrongAnswerIndex)}>Delete Wrong Answer?</button>
                                </div>
                            ))}
                            {addWrongAnswer && (currentq === item.getQuestion()) ? 
                            <div>
                                <form onSubmit={(e) => handleSubmit(e, item.getQuestion())} className="submitAddWrongAnswer">
                                    <label>Enter New Wrong Answer: </label>
                                    <input type='text' id="newWrongAnswer" name="newWrongAnswer"></input>
                                    <br/>
                                    <button className="addWrongAnswer" onClick={(e) => handleClick(e, item.getQuestion(), index)}>Cancel</button>
                                    <input type="submit"></input> 
                                </form>
                            </div>:
                            <button className="addWrongAnswer" onClick={(e) => handleClick(e, item.getQuestion(), index)}>Add Wrong Answer</button>}
                        </div>
                        <div className="correctAnswer">
                            <h3>Correct Answer:</h3>
                            {item.getCorrectAnswer()}
                            <br/>
                            {editCorrectAnswer && (currentq === item.getQuestion()) ? 
                            <div>
                                <form onSubmit={(e) => handleSubmit(e, item.getQuestion())} className="submitEditCorrectAnswer">
                                    <label>Enter New Correct Answer: </label>
                                    <input type="text" id="newCorrectAnswer" name="newCorrectAnswer"></input>
                                    <br/>
                                    <button className="editCorrectAnswer" onClick={(e) => handleClick(e, item.getQuestion(), index)}>Cancel</button>
                                    <input type="submit"></input>
                                </form>
                            </div> : 
                            <button className="editCorrectAnswer" onClick={(e) => handleClick(e, item.getQuestion(), index)}>Edit Correct Answer</button>}
                        </div>
                        <div className="justification">
                            <h3>Justification:</h3>
                            {item.getJustification()}
                            <br/>
                            {editJustification && (currentq === item.getQuestion()) ? 
                            <div>
                                <form onSubmit={(e) => handleSubmit(e, item.getQuestion())} className="submitEditJustification">
                                    <label>Enter New Justification: </label>
                                    <input type="text" id="newJustification" name="newJustification"></input>
                                    <br/>
                                    <button className="editJustification" onClick={(e) => handleClick(e, item.getQuestion(), index)}>Cancel</button>
                                    <input type="submit"></input>
                                </form>
                            </div> : 
                            <button className="editJustification" onClick={(e) => handleClick(e, item.getQuestion(), index)}>Edit Justification</button>}
                        </div>
                        <div className="image">
                            <h3>Image:</h3>
                            <img src={item.getImage()} alt=""/>
                            <br/>
                            <button className="deleteImage" onClick={(e) => handleClick(e, item.getQuestion(), index)}>Delete Image?</button>
                            <br/>
                            {addImage && (currentq === item.getQuestion()) ? 
                            <div>
                                <form onSubmit={(e) => handleSubmit(e, item.getQuestion())} className="submitAddImage">
                                    <label>Add Image: </label>
                                    <input type="file" id="newImage" name="newImage"></input>
                                    <br/>
                                    <button className="addImage" onClick={(e) => handleClick(e, item.getQuestion(), index)}>Cancel</button>
                                    <input type="submit"></input>
                                </form>
                            </div> : 
                            <button className="addImage" onClick={(e) => handleClick(e, item.getQuestion(), index)}>Add Image</button>}
                        </div>

                        <button className='Delete' onClick={(e) => handleClick(e, item.getQuestion(), index)}>Delete Question?</button></li>
                    ))}
                    {ShowQuestions()}
                </div>
            </div>
            </Text>
        </View>
        </div>
        )}
    </h1>);
}

function ShowQuestions(data, isAll = true){
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

        return (<div>
        {data.map((item, index) =>(
            <li style={{border: '1px solid black', padding: '8px'}}>
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
                <img src={item.getImage()}/>
                <br/>
                <button className="deleteImage" onClick={handleClick}>Delete Image?</button>
                <button className="addImage" onClick={handleClick}>Add Image</button>
            </div>

            <button className='Delete' onClick={(e) => handleClick(e, item.getQuestion(), index)}>Delete Question?</button></li>
        ))}
    </div>);
    }
}