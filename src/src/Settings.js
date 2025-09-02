import router from './index.js';
import { useState } from 'react';
import home from './home-svgrepo-com.svg';
import landingPage from './landing-page-web-design-svgrepo-com.svg'

let totalQuestions = 10;
let totalTime = 15;

export function Settings(){
    const [numQuestions, setNumQuestions] = useState(totalQuestions);
    const [time, setTime] = useState(totalTime);

    function handleNumQuestionsChange(e){
        setNumQuestions(e.target.value);
    }

    function handleTimeChange(e){
        setTime(e.target.value);
    }

    const handleSubmit = async (e) => {
        e.preventDefault();
        let formData = new FormData(e.target);

        totalQuestions = formData.get("numQuestions");
        totalTime = formData.get("time");
        router.navigate("/MainPage")
    }

    return (<div className="Settings">
        <h1 className="navBar">
            <div className="landingPage">
                <button onClick={() => router.navigate('/')}><img src={landingPage} alt="Landing Page"/></button>
            </div>
            <div className="home">
                <button onClick={() => router.navigate("/MainPage")}><img src={home} alt="Home"/></button>
            </div>
            DTT Quiz App - Settings
        </h1>

        <div className="settings">
            <form onSubmit={handleSubmit}>
                <label className="numQuestionsSetting">Number of Questions: </label>
                <input
                    type="number"
                    value={numQuestions}
                    onChange={handleNumQuestionsChange}
                    name="numQuestions"
                    id="numQuestions"
                />
                <br/>
                <label className="timeSetting">Time: </label>
                <input
                    type="number"
                    value={time}
                    onChange={handleTimeChange}
                    name="time"
                    id="time"    
                />
                    <br/>
                <input
                    type="submit" value="Apply"/>
            </form>
        </div>
    </div>);
}

export function getNumQuestions(){
    return totalQuestions
}

export function getTime(){
    return totalTime;
}