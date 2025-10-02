import router from './index.js';
import * as db from './Database.js';
import { useState, useEffect } from 'react';
import home from './home-svgrepo-com.svg';
import landingPage from './landing-page-web-design-svgrepo-com.svg'
import * as User from './User.js';
import { toast, ToastContainer } from 'react-toastify';
import hamburger from './burger-menu-svgrepo-com.svg';

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

        let user = await User.getUser();

        user.setSettings(s);

        //router.navigate("/MainPage");
        showTimedMessage("question settings");
    }

    const [error, setError] = useState(null);
    const [loading, setLoading] = useState(true);

    useEffect(() =>{
        const fetchData = async () =>{
            try{
                let u = await User.getUser();
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
        </form>
            <div>
                <ToastContainer position="top-right" autoClose={3000} />
            </div>
        </span>
        )}
    </span>
    );
}

const showTimedMessage = (settingUpdated) => {
    toast.success("Successfully updated " + settingUpdated + "!", {
        position: "top-right", // Can override the default from ToastContainer
        autoClose: 5000,      // Can override the default from ToastContainer
        hideProgressBar: false,
        closeOnClick: true,
        pauseOnHover: true,
        draggable: true,
        progress: undefined,
    });
};

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
        }else{
            showTimedMessage("password");
            e.target.reset();
        }
    }

    return (<div className="Settings">
        <div className="navBar">
            <div className="hamburgerMenu">
                <button onClick={toggleHamburgerMenu}><img src={hamburger} alt="Hamburger"/></button>
            </div>

            <div className="landingPage" id="landingPage">
                <button onClick={() => router.navigate('/')}><img src={landingPage} alt="Landing Page"/></button>
            </div>
            <div className="home" id="home">
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
            <form onSubmit={changePassword} id="passwordChange">
                <label>Password Reset <br/>Enter new password:</label>
                <input type="password" id="pass1" name="pass1"/>
                <br/>
                <label>Verify Password:</label>
                <input type="password" id="pass2" name="pass2"/>
                <input type="submit"/>
            </form>
            <div>
                <ToastContainer position="top-right" autoClose={3000} />
            </div>
        </div>
    </div>);
}

function toggleHamburgerMenu(){
  const landingButton = document.getElementById("landingPage");
  const homeButton = document.getElementById("home");
  if (landingButton.style.display === 'none' || landingButton.style.display === ''){
    landingButton.style.display = 'block';
    homeButton.style.display = 'block';
  }else{
    landingButton.style.display = 'none';
    homeButton.style.display = 'none';
  }
}

export async function getNumQuestions(){
    let u = await User.getUser();

    if (u === undefined){
        return;
    }

    return u.getSettings().getNumQuestions();
}

export async function getTime(){
    let u = await User.getUser();

    if (u === undefined){
        return;
    }
    
    return u.getSettings().getTimeLimit();
}