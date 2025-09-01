import './App.css';
import router from './index.js';
import * as quiz from './Quiz.js';
import {setPage} from './QuestionsFrontEnd.js';
import settings from './settings-svgrepo-com.svg';
import landingPage from './landing-page-web-design-svgrepo-com.svg';

export function MainPage(){
  const handleClick = () => {
    setPage(1);
    router.navigate("Questions")
  }
  document.body.style = 'background: white;';


  return (<div className="MainPage">
    <h1 className="navBar">
      <div className="landingPage">
        <button onClick={() => router.navigate('/')}><img src={landingPage} alt="Landing Page"/></button>
      </div>
      DTT Quiz App - Main Page
      <div className="settings">
        <button onClick={() => router.navigate('Settings')}><img src={settings} alt="Settings"/></button>
      </div>
    </h1>
    <h1 className="greeting">
      Welcome User!
    </h1>
    <button onClick={() => quiz.getInfo()} className="takeQuiz">Take Quiz</button>
    <br/>
    <button onClick={handleClick} className="editQuestions">View Questions</button>
  </div>);
}

export function LandingPage(){
  return (<div className="LandingPage">
    <h1 className="navBar">
      DTT Quiz App
    </h1>
    <br/>
    <br/>
    <h1 className="loginButton">
      <button onClick={() => router.navigate("MainPage")} className="loginButton">Login To Start Quizzin</button>
    </h1>
  </div>);
}

export default MainPage();