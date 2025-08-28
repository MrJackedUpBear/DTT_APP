import './App.css';
import router from './index.js';
import * as quiz from './Quiz.js';
import {setPage} from './QuestionsFrontEnd.js';

export function MainPage(){
  const handlClick = () => {
    setPage(1);
    router.navigate("Questions")
  }
  document.body.style = 'background: white;';


  return (<h1 className="MainPage">
    Welcome to the main page!
    <br/>
    <button onClick={handlClick}>Edit Questions</button>
    <button onClick={() => quiz.getInfo()}>Take Quiz</button>
    <button onClick={() => router.navigate('Settings')}>Settings</button>
  </h1>);
}

export function LandingPage(){
  return (<div className="LandingPage">
    <h1 className="DTTQuizApp">DTT Quiz App</h1>
    <b1 className="Login Button"><button onClick={() => router.navigate("MainPage")}>Login To Start Quizzin</button></b1>
  </div>);
}

export default MainPage();