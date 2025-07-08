import router from './index.js';
import * as questions from './Questions.js';
import {useEffect, useState} from 'react';
import {useLocation, Form} from 'react-router-dom';

export function EditQuestions(){
    return (<div className='App'>
        <header className="App-header">
            Edit Questions:
            <button onClick={() => router.navigate("/")}>Go Home</button>
            <button onClick={() => router.navigate("Add")}>Add Questions</button>
        </header>

    </div>);
}

export function AddQuestions(){
    const handleSubmit = async (event) => {
        event.preventDefault();
        const formData = new FormData(event.target);

        await QuestionsSubmittedPage(formData);
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
        wrongAnswer2 == "" || wrongAnswer3 == ""
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

export function SubmitAddQuestions(){
    return (<div>
        Questions added.
        <button onClick={() => router.navigate("/Questions/Add")}>Add Another Question</button>
        <button onClick={() => router.navigate("/")}>Go Home</button>
    </div>);
}