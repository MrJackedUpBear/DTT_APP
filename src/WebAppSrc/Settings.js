import router from './index.js';
import * as db from './Database.js';
import { useState, useEffect } from 'react';
import home from './home-svgrepo-com.svg';
import landingPage from './landing-page-web-design-svgrepo-com.svg'
import * as User from './User.js';

let totalQuestions;
let totalTime;

function GetUserSettings(){

    const [numQuestions, setNumQuestions] = useState(null);
    const [time, setTime] = useState(null);
    const [settingId, setSettingId] = useState(0)

    function handleNumQuestionsChange(e){
        setNumQuestions(e.target.value);
    }

    function handleTimeChange(e){
        setTime(e.target.value);
    }

    const handleSubmit = async (e) => {
        e.preventDefault();
        let formData = new FormData(e.target);

        let numQuest = formData.get("numQuestions");
        let timeLimit = formData.get("time");

        if (numQuest === "" || timeLimit === ""){
            alert("Must enter all values");
            return;
        }

        if (numQuest <= 0 || timeLimit <= 0){
            alert("Values must be greater than 0");
            return;
        }

        setNumQuestions(numQuest);
        setTime(timeLimit);

        let s = new User.Setting(numQuestions, time);
        s.setSettingId(settingId);

        await db.updateSettings(s);
        router.navigate("/MainPage")
    }
    const [error, setError] = useState(null);
    const [loading, setLoading] = useState(true);

    useEffect(() =>{
    const fetchData = async () =>{
        try{
            let u = await User.getUser(true);
            setTime(u.getSettings().getTimeLimit());
            setNumQuestions(u.getSettings().getNumQuestions());
            setSettingId(u.getSettings().getSettingId());
        }catch(e){
            setError(e);
        }finally{
            setLoading(false);
        }
    };

    fetchData();
    }, []);

    return (
    <span>
        {loading && <span></span>}
        {error && <span>Error: {error.message}</span>}
        {(time !== null && numQuestions != null) && (<span><form onSubmit={handleSubmit}>
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
        </form></span>
        )}
    </span>
    );
}

async function verifyTokens(){
  if (!await db.verifyTokens()){
    router.navigate("/Login");
  }
}

export function Settings(){
    const changePassword = async (e) =>{
        e.preventDefault();
        let formData = new FormData(e.target);

        let pass1 = formData.get("pass1");
        let pass2 = formData.get("pass2");

        if (pass1 !== pass2){
            alert("Passwords do not match.");
            return;
        }

        if (!await db.changePassword(pass1)){
            alert("Error changing password.");
        }

        router.navigate("/MainPage");
    }

    return (<div className="Settings">
        <div className="navBar">
            <div className="landingPage">
                <button onClick={() => router.navigate('/')}><img src={landingPage} alt="Landing Page"/></button>
            </div>
            <div className="home">
                <button onClick={() => router.navigate("/MainPage")}><img src={home} alt="Home" className="home"/></button>
            </div>
            <h1 className="title">
                DTT Quiz App - Settings
            </h1>
        </div>

        <div className="settings">
            Quiz Settings
            <GetUserSettings/>

            <br/>

            User Settings
            <form onSubmit={changePassword}>
                <label>Password Reset <br/>Enter new password:</label>
                <input type="password" id="pass1" name="pass1"/>
                <br/>
                <label>Verify Password:</label>
                <input type="password" id="pass2" name="pass2"/>
                <input type="submit"/>
            </form>
        </div>
    </div>);
}

export async function getNumQuestions(){
    let u = await User.getUser(true);

    if (u === undefined){
        return;
    }

    return u.getSettings().getNumQuestions();
}

export async function getTime(){
    let u = await User.getUser(true);

    if (u === undefined){
        return;
    }
    
    return u.getSettings().getTimeLimit();
}