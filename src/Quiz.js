import './App.css';
import * as user from './User.js';
import * as questions from './Questions.js';
import * as db from './Database.js';
import router from './index.js';
import React, { useState, useEffect } from 'react';
import { Text, View } from 'react-native';
import * as settings from './Settings.js';
import settingsMenu from './settings-svgrepo-com.svg';
import landingPage from './landing-page-web-design-svgrepo-com.svg';
import home from './home-svgrepo-com.svg';
import hamburger from './burger-menu-svgrepo-com.svg';


let correctAnswerVoice = null;
let wrongAnswerVoice = null;
let currentQuestion = new db.question();
let currentQuestionNum = 1;
let totalQuestions;
let totalTime;
let wrongChoice = false;
let correctChoice = false;
let totalCorrect = 0;
let correctLetter = '';

async function verifyTokens(){
  if (!await db.verifyTokens()){
    router.navigate("/Login");
  }
}

export async function getInfo(){
  totalQuestions = await settings.getNumQuestions();
  totalTime = await settings.getTime();
  wrongChoice = false;
  correctChoice = false;
  currentQuestionNum = 1;
  totalCorrect = 0;

  router.navigate("/MainPage/Quiz");
}

function confirmExit(){
  const response = window.confirm("Are you sure you want to exit? You will lose all progress.");

  if (!response){
    return;
  }
  router.navigate('/MainPage')
}

export function StartQuiz() {
  verifyTokens();
  document.body.style = 'background: white;';
  return (
    <div>
      <h1 className="navBar">
        <button onClick={confirmExit} className="exit">EXIT</button>
        <h1 className="title">
          DTT Quiz App - Quiz
        </h1>
    </h1>
    <div className="quiz">
      <h1 className="quizHeader">
        Question: {currentQuestionNum} of {totalQuestions}
      </h1>

      <div className="quizSection">
        <div className="quizQuestion" style={{wordWrap: 'break-word'}}>
          {ShowCurrentQuestion()}
        </div>
        <div className="quizAnswers">
          {GetAnswerChoices()}
        </div>
      </div>
    </div>
  </div>
  );
} 

function Countdown(){
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
          <div>{secondsLeft}</div>
        ) : 
          CheckAnswer("WrongAnswer")}
      </div>
    )
  };

  return <div><CountdownTimer initialSeconds={totalTime} /></div>
}
//Below this is where the actual functions for quiz start

export function CorrectAnswer(){
  verifyTokens();

  if (!wrongChoice && !correctChoice){
    return (<h1>
      Invalid redirect.
    </h1>);
  }

  currentQuestionNum++;

  let beforeTotalCorrect = totalCorrect;

  if (!wrongChoice && correctChoice){
    totalCorrect++;
  }

  wrongChoice = false;
  correctChoice = false;

  if (currentQuestionNum > totalQuestions){
    currentQuestionNum = 1;
    wrongChoice = false;
    correctChoice = false;
    return (<div>
      <h1 className="navBar">
          <button onClick={confirmExit} className="exit">EXIT</button>
          <h1 className="title">
            DTT Quiz App - Quiz
          </h1>
      </h1>
      <div className="finishedQuiz">
        Well done! You got {totalCorrect} out of {totalQuestions}<br />
      </div>
    </div>)
  }

  return (<div>
    <h1 className="navBar">
        <button onClick={confirmExit} className="exit">EXIT</button>
        <h1 className="title">
          DTT Quiz App - Quiz
        </h1>
    </h1>
    <div className="correct">
      Correct!
      <br/>
      {(currentQuestion.getJustification() !== '') ? <div>Justification: {currentQuestion.getJustification()}</div> :
      <div></div>}
      <br/>
      {(currentQuestion.getTaskLetter() !== '') ? <div>Category: {currentQuestion.getTaskLetter()} - {currentQuestion.getTaskLetterDesc()}</div> :
      <div></div>}
      Points for this question: {totalCorrect - beforeTotalCorrect} / 1
      <br/>
      <button onClick={() => router.navigate('/MainPage/Quiz')} className='nextButton'>Next Question</button>
    </div>
    </div>);
}

export function WrongAnswer(){
  verifyTokens();

  if (!wrongChoice && !correctChoice){
    return (<h1>
      Invalid redirect.
    </h1>);
  }

  return (<div>
    <div className="navBar">
        <button onClick={confirmExit} className="exit">EXIT</button>
        <h1 className="title">
          DTT Quiz App - Quiz
        </h1>
    </div>
    <div className="wrong">
      Incorrect!
      <br/>
      {(currentQuestion.getJustification() !== '') ? <div>Justification: {currentQuestion.getJustification()}</div> :
      <div></div>}
      <br/>
      {(currentQuestion.getTaskLetter() !== '') ? <div>Category: {currentQuestion.getTaskLetter()} - {currentQuestion.getTaskLetterDesc()}</div> :
      <div></div>}
      <button onClick={() => router.navigate('/MainPage/Quiz')} className='tryAgainButton'>Try Again... Try {correctLetter}</button>
    </div>
  </div>);
}

async function getCurrentQuestion(numQuestions){
  currentQuestion = await questions.getQuestion(currentQuestionNum - 1, numQuestions, wrongChoice);
}

function ShowCurrentQuestion(){
  const [data, setData] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    const fetchData = async () => {
      try {
        let qTotal = -1;
        if (currentQuestionNum - 1 === 0 && !wrongChoice){
          qTotal = await questions.getNumberQuestions();
        }

        if (totalQuestions > qTotal && qTotal !== -1){
          alert("Could not find " + totalQuestions + " questions. Could only find " + qTotal + " questions.");
          totalQuestions = qTotal;
          await getCurrentQuestion(totalQuestions);
        }else{
          await getCurrentQuestion(totalQuestions);
        }
        setData(currentQuestion);
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
        <div className="quizQuestion">
            {data.getQuestion()}
            <br/>
            {data.getImages().map((image, imageIndex) => (
                <div>
                    <img src={image[0]} alt=""/>
                    <br/>
                </div>
            ))}
        </div>
      )}
    </h1>);
}

function GetAnswerChoices(){
  let correctAnswer = currentQuestion.getCorrectAnswer();
  let wrongAnswers = currentQuestion.getWrongAnswers();
  let combined = wrongAnswers;

  if (!combined.includes(correctAnswer)){
    combined.push(correctAnswer);
  }

  if (!wrongChoice){
    shuffleArray(combined);
  }

  return (<div>
    {combined.map((item, index) =>(
      <div><button key={index} onClick={() => CheckAnswer(item)} className="quizAnswer">{letterAt(index, item)}) {item}</button></div>
    ))}
    <div className="timer">
      Time Left: {Countdown()}
    </div>
  </div>);
}

function letterAt(index, value){
  let isCorrect = false;
  let letter = '';
  if (value === currentQuestion.getCorrectAnswer()){
    isCorrect = true;
  }

  switch(index){
    case 0:
      letter = 'A';
      break;
    case 1:
      letter = 'B';
      break;
    case 2:
      letter = 'C';
      break;
    case 3:
      letter = 'D';
      break;
    case 4:
      letter = 'E';
      break;
    case 5:
      letter = 'F';
      break;
  }

  if (isCorrect){
    correctLetter = letter;
  }

  return letter;
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

function isNum(input){
  let len = input.length;

  for (let i = 0; i < len; i++){
    switch(input[i]){
      case '0':
        break;
      case '1':
        break;
      case '2':
        break;
      case '3':
        break;
      case '4':
        break;
      case '5':
        break;
      case '6':
        break;
      case '7':
        break;
      case '8':
        break;
      case '9':
        break;
      default:
        return false;
    }
  }

  return true;
}

export function CheckAnswer(answer){
  window.scrollTo(0, 0);
  if (answer === currentQuestion.getCorrectAnswer()){
    correctChoice = true;
    router.navigate('CorrectAnswer');
  }else{
    wrongChoice = true;
    router.navigate('SameQuestion');
  }
}