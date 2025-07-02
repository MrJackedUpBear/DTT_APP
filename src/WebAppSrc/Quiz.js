import './App.css';
import * as user from './User.js';
import * as questions from './Questions.js';
import * as db from './Database.js';
import correctAnswerGraphic from './correct.jpg';
import wrongAnswerGraphic from './incorrect.png';
import router from './index.js';
import React, { useState, useEffect } from 'react';


let correctAnswerVoice = null;
let wrongAnswerVoice = null;
let currentQuestion = new db.question();
let currentQuestionNum = 1;
let totalQuestions = 2;
let totalTime = 15;

export function StartQuiz() {
  const CountdownTimer = ({initialSeconds}) =>{
    const [secondsLeft, setSecondsLeft] = useState(initialSeconds);

    useEffect(() => {

      if (secondsLeft <= 0){
        return;
      }

      const intervalId = setInterval(() => {
        setSecondsLeft(prevSeconds => prevSeconds - 1);
      }, 1000);

      return () => clearInterval(intervalId);
    }, [secondsLeft]);

    return (
      <div>
        {secondsLeft > 0 ? (
          <h1>{secondsLeft}</h1>
        ): router.navigate('SameQuestion')}
      </div>
    )
  };
  return (
    <div className="App">
      <header className="App-header">
        DTT Quiz 
        <br></br>
        Time Left: <CountdownTimer initialSeconds={totalTime}/>
        {showCurrentQuestion()}
      </header>
        {GetAnswerChoices()}
    </div>
  );
} 

//Below this is where the actual functions for quiz start

export function CorrectAnswer(){
  currentQuestionNum++;

  if (currentQuestionNum > totalQuestions){
    return (<h1>
      Well done!
    </h1>)
  }

  return (<h1>
    <button onClick={() => router.navigate('/Quiz')}>Correct! Next Question</button>
    </h1>);
}

export function WrongAnswer(){
  return (<h1>
    <button onClick={() => router.navigate('/Quiz')}>Incorrect. Try Again. Try {currentQuestion.getCorrectAnswer()}</button>

  </h1>);
}

function showCurrentQuestion(){
  currentQuestion = questions.getQuestion(currentQuestionNum - 1, totalQuestions);
  if (totalQuestions !== questions.getNumberQuestions()){
    totalQuestions = questions.getNumberQuestions();
  }
  return <h1>{currentQuestion.getQuestion()}</h1>;
}

function GetAnswerChoices(){
  let correctAnswer = currentQuestion.getCorrectAnswer();
  let wrongAnswers = currentQuestion.getWrongAnswers();
  let combined = wrongAnswers;

  if (!combined.includes(correctAnswer)){
    combined.push(correctAnswer);
  }

  shuffleArray(combined);

  return (<h1>
    {combined.map((item, index) =>(
      <li><button key={index} onClick={() => CheckAnswer(item)}>{item}</button></li>
    ))}
  </h1>);
}

function shuffleArray(array){
  let currentIndex = array.length;
  let randomIndex;

  while (currentIndex !== 0){
    randomIndex = Math.floor(Math.random() * currentIndex);
    currentIndex--;
    [array[currentIndex], array[randomIndex]] = [
      array[randomIndex],
      array[currentIndex],
    ];
  }

  return array;
}

export function CheckAnswer(answer){
  if (answer === currentQuestion.getCorrectAnswer()){
    router.navigate('CorrectAnswer');
  }else{
    router.navigate('SameQuestion');
  }
}