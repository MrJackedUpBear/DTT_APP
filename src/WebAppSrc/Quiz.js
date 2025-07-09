import './App.css';
import * as user from './User.js';
import * as questions from './Questions.js';
import * as db from './Database.js';
import correctAnswerGraphic from './correct.jpg';
import wrongAnswerGraphic from './incorrect.png';
import router from './index.js';
import React, { useState, useEffect } from 'react';
import { Text, View } from 'react-native';


let correctAnswerVoice = null;
let wrongAnswerVoice = null;
let currentQuestion = new db.question();
let currentQuestionNum = 1;
let totalQuestions = 1;
let totalTime = 15;
let wrongChoice = false;
let correctChoice = false;

export function getInfo(){
  const tempNumQuestions = prompt("Enter number of questions.");
  const tempTime = prompt("Enter time");

  if (!isNum(tempNumQuestions) || !isNum(tempTime)){
    alert("Incorrect usage!");
    return;
  }

  totalQuestions = tempNumQuestions;
  totalTime = tempTime;
  wrongChoice = false;
  correctChoice = false;
  currentQuestionNum = 1;

  router.navigate("/Quiz");
}

export function StartQuiz() {
  return (
    <div className="App">
      <header className="App-header">
        <button onClick={() => router.navigate("/")}>Home</button>
        DTT Quiz 
        <br></br>
        Time Left:
      </header>
      <div className="Question" style={{wordWrap: 'break-word'}}>
        {ShowCurrentQuestion()}
      </div>
        {GetAnswerChoices()}
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
          <h1>{secondsLeft}</h1>
        ): CheckAnswer("WrongAnswer")}
      </div>
    )
  };

  return <div><CountdownTimer initialSeconds={totalTime} /></div>
}
//Below this is where the actual functions for quiz start

export function CorrectAnswer(){
  if (!wrongChoice && !correctChoice){
    return (<h1>
      Invalid redirect.
    </h1>);
  }

  currentQuestionNum++;

  if (currentQuestionNum > totalQuestions){
    currentQuestionNum = 1;
    wrongChoice = false;
    correctChoice = false;
    return (<h1>
      Well done! <br />
      <button onClick={() => router.navigate("/")}>Return Home</button>
      <button onClick={() => getInfo()}>Take Another Quiz</button>
    </h1>)
  }

  return (<h1>
    <button onClick={() => router.navigate("/")}>Return Home</button>
    <button onClick={() => router.navigate('/Quiz')}>Correct! Next Question</button>
    </h1>);
}

export function WrongAnswer(){
  if (!wrongChoice && !correctChoice){
    return (<h1>
      Invalid redirect.
    </h1>);
  }

  return (<h1>
    <button onClick={() => router.navigate("/")}>Return Home</button>
    <button onClick={() => router.navigate('/Quiz')}>Incorrect. Try Again. Try {currentQuestion.getCorrectAnswer()}</button>

  </h1>);
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
        <div>
        <View style={{justifyContent: 'center', alignItems: 'center', flexShrink: 1}}> 
          <Text style={{flex: 1, flexWrap: 'wrap', fontSize: 30, flexShrink: 1}}> 
            {data.getQuestion()}
            {Countdown()}
          </Text>
        </View>
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
  if (answer === currentQuestion.getCorrectAnswer()){
    correctChoice = true;
    router.navigate('CorrectAnswer');
  }else{
    wrongChoice = true;
    router.navigate('SameQuestion');
  }
}