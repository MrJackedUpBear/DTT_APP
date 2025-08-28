import router from './index.js';
import { useState } from 'react';

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
        <button onClick={() => router.navigate("/MainPage")}>Return Home</button>

        <h1>DTT Quiz App - Settings</h1>
        <form onSubmit={handleSubmit}>
            <label>Number of Questions: </label>
            <input
                type="number"
                value={numQuestions}
                onChange={handleNumQuestionsChange}
                name="numQuestions"
                id="numQuestions"
            />
            <br/>
            Time:
            <input
                type="number"
                value={time}
                onChange={handleTimeChange}
                name="time"
                id="time"    
            />
                <br/>
            <input
                type="submit" value="Submit"/>
        </form>
    </div>);
}

export function getNumQuestions(){
    return totalQuestions
}

export function getTime(){
    return totalTime;
}