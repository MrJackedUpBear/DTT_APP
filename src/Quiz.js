import './App.css';
import * as questions from './Questions.js';
import * as db from './Database.js';
import router from './index.js';
import React, { useState, useEffect } from 'react';
import * as Settings from './Settings.js';
import hamburger from './burger-menu-svgrepo-com.svg';
import landingPage from './landing-page-web-design-svgrepo-com.svg';
import home from './home-svgrepo-com.svg';
import settings from './settings-svgrepo-com.svg';
import correctRecording from './recordings/CorrectLow.m4a';
import incorrectRecording from './recordings/IncorrectLow.m4a';

/*
Might set up answer voices on this later on...
*/
let currentQuestion = new db.question();
let currentQuestionNum = 1;
let totalQuestions;
let totalTime;
let wrongChoice = false;
let correctChoice = false;
let totalCorrect = 0;
let correctLetter = '';
let correctlyAnswered = [];
let correctAnswers = [];
let incorrectlyAnswered = [];
let incorrectAnswers = [];
let trueAnswers = [];
let answer = "";

async function verifyTokens(){
  if (!await db.verifyTokens()){
    router.navigate("/Login");
  }
}

export async function getInfo(){
  totalQuestions = await Settings.getNumQuestions();
  totalTime = await Settings.getTime();
  wrongChoice = false;
  correctChoice = false;
  currentQuestionNum = 1;
  totalCorrect = 0;
  correctlyAnswered = [];
  incorrectlyAnswered = [];
  correctAnswers = [];
  trueAnswers = [];
  answer = "";

  router.navigate("/MainPage/Quiz");
}

function confirmExit(){
  const response = window.confirm("Are you sure you want to exit? You will lose all progress.");

  if (!response){
    return;
  }
  router.navigate("/MainPage/Quiz/FinishedQuiz");
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
    correctlyAnswered.push(currentQuestion);
    correctAnswers.push(answer);
  }

  wrongChoice = false;
  correctChoice = false;

  if (currentQuestionNum > totalQuestions){
    currentQuestionNum = 1;
    wrongChoice = false;
    correctChoice = false;
    router.navigate("/MainPage/Quiz/FinishedQuiz");
  }

  return (<div>
    <h1 className="navBar">
        <button onClick={confirmExit} className="exit">EXIT</button>
        <h1 className="title">
          DTT Quiz App - Quiz
        </h1>
    </h1>
    <div className="correct">
      <audio src={correctRecording} autoPlay/>
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

  incorrectlyAnswered.push(currentQuestion);
  incorrectAnswers.push(answer);

  return (<div>
    <div className="navBar">
        <button onClick={confirmExit} className="exit">EXIT</button>
        <h1 className="title">
          DTT Quiz App - Quiz
        </h1>
    </div>
    <div className="wrong">
      <audio src={incorrectRecording} autoPlay/>
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

export function FinishedQuiz(){
  let q = questions.getQuizQuestions();

  let results = [];

  for (let i = 0; i < q.length; i++){
    if (results.length === 0){
      let r = new Result(q[i].getTaskLetter());

      if (correctlyAnswered.includes(q[i])){
        r.addCorrectlyAnswered();
      }else{
        r.addIncorrectlyAnswered();
      }
      results.push(r);
    }else{
      let resultContains = false;
      for (let j = 0; j < results.length; j++){
        if (q[i].getTaskLetter() === results[j].getTaskLetter()){
          resultContains = true;
          if (correctlyAnswered.includes(q[i])){
            results[j].addCorrectlyAnswered();
          }else{
            results[j].addIncorrectlyAnswered();
          }
        }
      }

      if (!resultContains){
        let r = new Result(q[i].getTaskLetter());

        if (correctlyAnswered.includes(q[i])){
          r.addCorrectlyAnswered();
        }else{
          r.addIncorrectlyAnswered();
        }
        results.push(r);
      }
    }
  }

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
      <div className="finishedQuiz">
        <audio src={correctRecording} autoPlay/>
        Well done! You got {totalCorrect} out of {totalQuestions}<br />
      </div>
      <div className="questions">

      Correctly Answered Questions:
      <ol>
        {correctlyAnswered.map((item, index) => (
          <div>
            <li>
              <span key={index}>{item.getTaskLetter()} {item.getQuestion()}</span>
              <br/>
              <span key={index}>Answer given: {correctAnswers[index]}</span>
            </li>
          </div>
          
        ))}
      </ol>

        <br/>
        Incorrectly Answered Questions:
        <ol>
          {incorrectlyAnswered.map((item, index) => (
            <div>
              <li>
                <span key={index}>{item.getTaskLetter()} {item.getQuestion()}</span>
                <br/>
                <span key={index}>Answer given: {incorrectAnswers[index]}</span>
                <br/>
                <span>Answer should have been:  {trueAnswers[index]}</span>
              </li>
              <br/>
            </div>
          ))}
        </ol>

        <br/>

        Score by Task Letter:
        <ol>
          {results.map((item, index) =>(
            <div>
              <li key={index}>
                Task Letter: {item.getTaskLetter()}
                <br/>
                {(item.getCorrectlyAnswered() / (item.getCorrectlyAnswered() + item.getIncorrectlyAnswered())) * 100}%
              </li>
            </div>
          ))}
        </ol>

        <br/>

        All Questions:

        <ol>
          {q.map((item, index) => (
            <li key={index}>{item.getTaskLetter()} {item.getQuestion()}</li>
          ))}
        </ol>
      </div>
    </div>);
}

class Result{
  constructor(taskLetter){
    this.taskLetter = taskLetter;
    this.correctlyAnswered = 0;
    this.incorrectlyAnswered = 0;
  }

  getTaskLetter(){return this.taskLetter;}
  getCorrectlyAnswered(){return this.correctlyAnswered;}
  getIncorrectlyAnswered(){return this.incorrectlyAnswered;}

  addCorrectlyAnswered(){this.correctlyAnswered++;}
  addIncorrectlyAnswered(){this.incorrectlyAnswered++;}
}

function toggleHamburgerMenu(){
  const landingButton = document.getElementById("landingPage");
  const settingButton = document.getElementById("Settings");
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
    case 6:
      letter = 'G';
      break;
    case 7:
      letter = 'H';
      break;
    default:
      letter = 'Z';
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

export function CheckAnswer(ans){
  answer = ans;
  window.scrollTo(0, 0);
  if (ans === currentQuestion.getCorrectAnswer()){
    correctChoice = true;
    router.navigate('CorrectAnswer');
  }else{
    wrongChoice = true;
    trueAnswers.push(currentQuestion.getCorrectAnswer());
    router.navigate('SameQuestion');
  }
}