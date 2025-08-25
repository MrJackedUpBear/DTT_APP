import router from './index.js';
import * as questions from './Questions.js';
import {useEffect, useState} from 'react';
import {Form} from 'react-router-dom';
import { Text, View } from 'react-native';
import { uploadFile } from './Database.js';

let page = 1;
let numOnPage = 100;
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
        questionsFromFile = await uploadFile(formData);
        router.navigate('Verify')
    }

    return (<div className="App">
        <header className="App-header">
            <button onClick={() => router.navigate("/")}>Home</button>
            <button onClick={() => router.navigate("/Questions")}>Back</button>
            Enter prompt, correct answer, and wrong answers.
        </header>
        <Form onSubmit={handleSubmit}>
            <div>
                <label>
                    Prompt: 
                    <input type="text" id="prompt" name="prompt"/>
                </label>
                <label>
                    Correct Answer:
                    <input type="text" id="correctAnswer" name="correctAnswer"/>
                </label>
                <label>
                    Wrong Answers:
                    <input type="text" id="wrongAnswer1" name="wrongAnswer1"/>
                    <input type="text" id="wrongAnswer2" name="wrongAnswer2"/>
                    <input type="text" id="wrongAnswer3" name="wrongAnswer3"/>
                </label>
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
    let prompts = [];
    let wrongAnswers = [];
    let correctAnswers = [];

    let prompt = formData.get("prompt").trim();
    let correctAnswer = formData.get("correctAnswer").trim();
    let wrongAnswer1 = formData.get("wrongAnswer1").trim();
    let wrongAnswer2 = formData.get("wrongAnswer2").trim();
    let wrongAnswer3 = formData.get("wrongAnswer3").trim();

    if (prompt === "" || correctAnswer === "" || wrongAnswer1 === "" ||
        wrongAnswer2 === "" || wrongAnswer3 === ""
    ){
        alert("Please fill out all values.");
        return;
    }

    let wrongAnswer = [];

    prompts.push(prompt);
    correctAnswers.push(correctAnswer);
    wrongAnswer.push(wrongAnswer1);
    wrongAnswer.push(wrongAnswer2);
    wrongAnswer.push(wrongAnswer3);

    wrongAnswers.push(wrongAnswer);

    await questions.addQuestions(prompts, correctAnswers, wrongAnswers);
    router.navigate("Submit");
}

export function VerifyQuestions(){
    questionToEdit = questionsFromFile;

    const handleClick = async (event) => {
        let prompts = [];
        let answers = [];
        let wrongAnswers = [];

        for (let i = 0; i < questionToEdit.length; i++){
            prompts.push(questionToEdit[i].getQuestion());
            answers.push(questionToEdit[i].getCorrectAnswer());

            let wrongLen = questionToEdit[i].getWrongAnswers().length;

            let temp = [];
            for (let j = 0; j < wrongLen; j++){
                temp.push(questionToEdit[i].getWrongAnswers()[j]);
            }
            wrongAnswers.push(temp);
        }

        await questions.addQuestions(prompts, answers, wrongAnswers);
        alert("Successfully added questions.");
        router.navigate('/Questions');
    }

    return (
        <div>
            <div className="App-header">
                Questions From File:
            </div>
            <button onClick={() => router.navigate("/")}>Home</button>
            <button onClick={() => router.navigate(-1)}>Back</button>
            {showQuestions(questionsFromFile, false)}
            <button onClick={handleClick}>Add Questions</button>
        </div>
    );
}

export function SubmitAddQuestions(){
    return (<div>
        Questions added.
        <button onClick={() => router.navigate("/Questions/Add")}>Add Another Question</button>
        <button onClick={() => router.navigate("/")}>Go Home</button>
    </div>);
}

export function ViewAndUpdate(){

    return (<div>
        <button onClick={() => router.navigate("/")}>Home</button>
        <button onClick={() => router.navigate('Add')}>Add Questions</button>
        <h1>Prompts</h1>
        {LoadQuestions()}
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
        router.navigate('/Questions/');
    }else{
        router.navigate('/Questions/Add/Verify');
    }
}

export function Back(){
    page--;
    if (allQuestions !== undefined){
        router.navigate('/Questions/');
    }else{
        router.navigate('/Questions/Add/Verify');
    }
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
    
    //getCurrentQuestion();
    return (<h1>
        {loading && <p>Loading data...</p>}
        {error && <p>Error: {error.message}</p>}
        {data && (
        <div>
        <View style={{justifyContent: 'center', alignItems: 'center', flexShrink: 1}}> 
            <Text style={{flex: 1, flexWrap: 'wrap', fontSize: 20, flexShrink: 1}}> 
            {showQuestions(data, true)}
            </Text>
        </View>
        </div>
        )}
    </h1>);
}

function showQuestions(data, isAll = true){
    if (isAll){
        questionsFromFile = undefined;
        allQuestions = data;
        questionToEdit = allQuestions;
    }else{
        allQuestions = undefined;
        questionToEdit = questionsFromFile;
    }

    let questionPrompts = [];
    const handleClick = (event, prompt, index) => {
        const className = event.target.className;

        setQuestionPrompt(className, prompt, index);
    }

    let numQuestions = data.length;

    for (let i = 0; i < numQuestions; i++){
        questionPrompts.push(data[i].getQuestion());
    }

    return (<div>
        {questionPrompts.map((item, index) =>(
            <li style={{border: '1px solid black', padding: '8px'}}>{index + 1}: {item}<br/>
            <button className="Edit" onClick={(e) => handleClick(e, item, index)}>Edit</button>
            <button className='Delete' onClick={(e) => handleClick(e, item, index)}>Delete</button></li>
        ))}
    </div>);
}

async function setQuestionPrompt(className, prompt, index){
    questionPrompt = prompt;
    questionNum = index;

    if (className === 'Edit'){
        router.navigate('Edit');
    }else if (className === 'Delete'){
        let confirmation = window.confirm("Are you sure you want to delete: \"" + prompt + "\"?");

        if (!confirmation){
            return;
        }

        if (allQuestions !== undefined){
            questions.deleteQuestion(prompt);
        }else{
            await deleteFromFile(prompt);
        }
        alert("Successfully deleted: " + prompt);
        page++;
        router.navigate('Back');
    }
    else{
        router.navigate('/Questions/Update');
    }
}

async function deleteFromFile(prompt){
    let len = questionToEdit.length;
    let temp = [];

    for (let i = 0; i < len; i++){
        if (questionToEdit[i].getQuestion !== prompt){
            temp.push(questionToEdit[i]);
        }
    }
    questionToEdit = temp;
}

export function Edit(){
    let wrongAnswers = questionToEdit[questionNum].getWrongAnswers();
    return (<div>
        <header className="App-header">
            <button onClick={() => router.navigate("/")}>Home</button>
            <button onClick={() => router.navigate(-1)}>Back</button>
            {questionPrompt}
        </header>
        <button onClick={() => router.navigate('Prompt')}>Edit Prompt</button>: "{questionToEdit[questionNum].getQuestion()}" <br/>
        <button onClick={() => router.navigate('CorrectAnswer')}>Edit Correct Answer</button>: "{questionToEdit[questionNum].getCorrectAnswer()}" <br />
        <button onClick={() => router.navigate('WrongAnswers')}>Edit Wrong Answers</button>: {wrongAnswers.map((item, index) =>
            <div key={index}>"{item}"</div>
        )}
    </div>);
}

export function EditPrompt(){
    const handleSubmit = async (event) => {
        const formData = new FormData(event.target);

        let newPrompt = formData.get('newPrompt').trim();
        let oldPrompt = questionToEdit[questionNum].getQuestion();

        if (allQuestions !== undefined){
            await questions.updatePrompt(oldPrompt, newPrompt);
            router.navigate('/Questions/Update');
        }else{
            await updatePrompt(newPrompt);
            router.navigate('/Questions/Add/Verify');
        }
    }

    return (<div>
        <button onClick={() => router.navigate("/")}>Home</button>
        <button onClick={() => router.navigate(-1)}>Back</button>
        <Form onSubmit={handleSubmit}>
            <label>Enter new prompt: </label>
            <input type='text' id='newPrompt' name='newPrompt'/>
            <input type='submit'/>
        </Form>
    </div>);
}

async function updatePrompt(newPrompt){
    await questionsFromFile[questionNum].setQuestion(newPrompt);
}

export function EditCorrectAnswer(){
    const handleSubmit = async (event) => {
        const formData = new FormData(event.target);

        let newCorrectAnswer = formData.get('newCorrectAnswer').trim();
        let oldCorrectAnswer = questionToEdit[questionNum].getQuestion();

        if (allQuestions !== undefined){
            await questions.updateCorrectAnswer(oldCorrectAnswer, newCorrectAnswer);
            router.navigate('/Questions/Add/Verify');
        }else{
            await updateCorrectAnswer(newCorrectAnswer);
            router.navigate('/Questions/Add/Verify');
        }
    }

    return (<div>
        <button onClick={() => router.navigate("/")}>Home</button>
        <button onClick={() => router.navigate("/Questions/Update")}>Back</button>
        <Form onSubmit={handleSubmit}>
            <label>Enter new correct answer: </label>
            <input type='text' id='newCorrectAnswer' name='newCorrectAnswer'/>
            <input type='submit'/>
        </Form>
    </div>);
}

async function updateCorrectAnswer(newCorrectAnswer){
    await questionToEdit[questionNum].setCorrectAnswer(newCorrectAnswer);
}

export function EditWrongAnswers(){
    const handleSubmit = async (event) => {
        const formData = new FormData(event.target);

        let wrongAnswer = formData.get('newWrongAnswer').trim();
        let prompt = questionToEdit[questionNum].getQuestion();
        let questionId = -1;

        let oldWrongAnswer = formData.get('oldWrongAnswer').trim();

        for (let i = 0; i < questionToEdit[questionNum].getWrongAnswers().length; i++){
            if (oldWrongAnswer === questionToEdit[questionNum].getWrongAnswers()[i]){
                questionId = i;
                i = questionToEdit[questionNum].getWrongAnswers().length;
            }
        }

        if (questionId === -1){
            return;
        }

         if (allQuestions !== undefined){
            await questions.updateWrongAnswer(prompt, wrongAnswer, questionId);
            router.navigate('/Questions/Update');
        }else{
            await updateWrongAnswer(wrongAnswer, questionId);
            router.navigate('/Questions/Add/Verify');
        }
    }

    return (<div>
        <button onClick={() => router.navigate("/")}>Home</button>
        <button onClick={() => router.navigate("/Questions/Update")}>Back</button>
        <Form onSubmit={handleSubmit}>
            <select id='oldWrongAnswer' name='oldWrongAnswer' value={questionToEdit[questionNum].getWrongAnswers[0]}>
                {questionToEdit[questionNum].getWrongAnswers().map((value, index) => (
                    <option key={index}>{value}</option>
                ))}
            </select>
            <label>Enter new wrongAnswer: </label>
            <input type='text' id='newWrongAnswer' name='newWrongAnswer'/>
            <input type='submit'/>
        </Form>
    </div>);
}

async function updateWrongAnswer(wrongAnswer, questionId){
    await questionToEdit[questionNum].setWrongAnswer(wrongAnswer, questionId);
}