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


  return (<h1>
    Welcome to the main page!
    <br/>
    <button onClick={handlClick}>Edit Questions</button>
    <button onClick={() => quiz.getInfo()}>Take Quiz</button>
  </h1>);
}

export default MainPage();