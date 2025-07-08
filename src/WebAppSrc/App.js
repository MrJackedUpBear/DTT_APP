import './App.css';
import router from './index.js';
import * as quiz from './Quiz.js';

export function MainPage(){
  return (<h1>
    Welcome to the main page!
    <br/>
    <button onClick={() => router.navigate("Questions")}>Edit Questions</button>
    <button onClick={() => quiz.getInfo()}>Take Quiz</button>
  </h1>);
}

export default MainPage();