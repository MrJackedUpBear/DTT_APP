import './App.css';
import React from 'react';
import {useState, useEffect} from 'react';
import * as quiz from './Quiz.js';
import {setPage} from './QuestionsFrontEnd.js';
import settings from './settings-svgrepo-com.svg';
import landingPage from './landing-page-web-design-svgrepo-com.svg';
import * as db from './Database.js';
import * as User from './User.js';
import { useNavigate } from 'react-router-dom';
import hamburger from './burger-menu-svgrepo-com.svg'

export function MainPage(){
  console.log("Rendering main page...");
  const navigate = useNavigate();
  const handleClick = () => {
    setPage(1);
    navigate("/MainPage/Questions");
  }
  document.body.style = 'background: white;';

   useEffect(() => {
  const check = async () => {
    if (!await db.verifyTokens()) {
      navigate("/");
    }
  };
  check();
}, [navigate]);

  return (<div className="MainPage">
    <div className="navBar">
      <div className="hamburgerMenu">
        <button onClick={toggleHamburgerMenu}><img src={hamburger} alt="Hamburger"/></button>
      </div>
      <div className="landingPage" id="landingPage">
        <button onClick={() => navigate('/')}><img src={landingPage} alt="Landing Page"/></button>
      </div>
      <h1 className="title">
        DTT Quiz App - Main Page
      </h1>
      <div className="settings" id="settings">
        <button onClick={() => navigate('Settings')}><img src={settings} alt="Settings"/></button>
      </div>
    </div>

    <h1 className="greeting">
      Welcome <GetUserName/>!
    </h1>
    <button onClick={() => quiz.getInfo()} className="takeQuiz">Take Quiz</button>
    <br/>
    <button onClick={handleClick} className="editQuestions">View Questions</button>
  </div>);
}

function toggleHamburgerMenu(){
  const landingButton = document.getElementById("landingPage");
  const settingButton = document.getElementById("settings");
  if (landingButton.style.display === 'none' || landingButton.style.display === ''){
    landingButton.style.display = 'block';
    settingButton.style.display = 'block';
  }else{
    landingButton.style.display = 'none';
    settingButton.style.display = 'none';
  }
}

function GetUserName(){
  const [user, setUser] = useState(null);
  const [error, setError] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() =>{
    const fetchData = async () =>{
      try{
        let u = await User.getUser();
        setUser(u);
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
      {user && (<span>{user.getFirstName()}</span>
      )}
    </span>
  );
}

export function LoginPage(){
  const [emailAuth, setEmailAuth] = useState(false);
  const [emailCode, setEmailCode] = useState(false);
  const navigate = useNavigate();

  const changeEmailAuthValue = async () => {
    if (emailAuth){
      setEmailAuth(false);
      setEmailCode(false);
    }else{

      setEmailAuth(true);
    }
  }

  const handlePasswordLogin = async (event) =>{
    event.preventDefault();
    const formData = new FormData(event.target);

    let username = formData.get("usernameInput");
    let password = formData.get("passwordInput");

    if (await db.getToken(username, password)){
      navigate("/MainPage");
    }else{
      alert("Wrong username or password... Try again.");
    }
  }

  const handleEmailAuth = (event) =>{
    event.preventDefault();
    const formData = new FormData(event.target);

    let email = formData.get("email");
    db.getEmailAuth(email);
    setEmailCode(true);
  }

  const handleEmailLogin = async (event) =>{
    event.preventDefault();
    const formData = new FormData(event.target);

    let emailCode = formData.get("emailCode");
    
    if (await db.verifyEmailAuth(emailCode)){
      navigate("/MainPage");
      setEmailAuth(false);
      setEmailCode(false);
    }else{
      alert("Incorrect code. Please try again...");
    }
  }

  return (<div className="MainPage">
    <div className="navBar">
      <div className="landingPage">
        <button onClick={() => navigate('/')}><img src={landingPage} alt="Landing Page"/></button>
      </div>
      <h1 className="title">
        DTT Quiz App - Login
      </h1>
    </div>
    <div className="hamburgerMenu">
      <button><img src={hamburger} alt="Hamburger"/></button>
    </div>
    
    <div className="login">
      <form onSubmit={handlePasswordLogin}>
        <input type="text" className='usernameInput' name="usernameInput" placeholder="Enter Username..."/>
        <br/>
        <input type="password" className='passwordInput' name="passwordInput" placeholder="Enter Password..."/>
        <br/>
        <input type="submit" value="Login"/>
      </form>

      <br/>
      <br/>
      {emailAuth && !emailCode ? 
        <div>
          <form onSubmit={handleEmailAuth}>
            <input type="text" className="email" name="email" placeHolder="Enter Your Email Address..."/>
            <input type="submit" value="Submit"/>
          </form>
          <button onClick={changeEmailAuthValue}>Cancel</button>
        </div> : 
        <div>
          <button onClick={changeEmailAuthValue}>Email Auth</button>
        </div>
      }

      {emailAuth && emailCode ?
        <div>
          <form onSubmit={handleEmailLogin}>
            <input type='text' className='emailCode' name='emailCode' placeHolder="Enter Code From Email..."/>
            <input type="submit" value="Login"/>
          </form>
          <button onClick={changeEmailAuthValue}>Cancel</button>
        </div>:
        <div>
        </div>
      }
    </div>
  </div>);
}

export function LandingPage(){
  console.log("Rendering landing page...");
  const navigate = useNavigate();
  const handleClick = async (e) =>{
    //Need to add logic to check if there is already a valid token set up. If there is, let the user get in. If there is not, check if there is a refresh token available and see if
    //we can get a new access token. If there is no refresh token, send them to the login page.
    if (e == null){
      return;
    }
    
    if (await db.verifyTokens()){
      navigate("/MainPage");
    }else{
      navigate("/Login");
    }
  }

  return (<div className="LandingPage">
    <div className="navBar">
      <h1 className="title">
        DTT Quiz App
      </h1>
    </div>
    <br/>
    <br/>
    <h1 className="loginButton">
      <button onClick={handleClick} className="loginButton">Login To Start Quizzin</button>
    </h1>
  </div>);
}

export default LandingPage;