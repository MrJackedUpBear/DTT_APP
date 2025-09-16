import './App.css';
import React from 'react';
import {useState, useEffect} from 'react';
import router from './index.js';
import * as quiz from './Quiz.js';
import {setPage} from './QuestionsFrontEnd.js';
import settings from './settings-svgrepo-com.svg';
import landingPage from './landing-page-web-design-svgrepo-com.svg';
import * as db from './Database.js';
import * as User from './User.js';

export function MainPage(){
  const handleClick = () => {
    setPage(1);
    router.navigate("Questions")
  }
  document.body.style = 'background: white;';

  verifyTokens();

  return (<div className="MainPage">
    <div className="navBar">
      <div className="landingPage">
        <button onClick={() => router.navigate('/')}><img src={landingPage} alt="Landing Page"/></button>
      </div>
      <h1 className="title">
        DTT Quiz App - Main Page
      </h1>
      <div className="settings">
        <button onClick={() => router.navigate('Settings')}><img src={settings} alt="Settings"/></button>
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

async function verifyTokens(){
  if (!await db.verifyTokens()){
    router.navigate("/");
  }
}

export function LoginPage(){
  const [emailAuth, setEmailAuth] = useState(false);
  const [emailCode, setEmailCode] = useState(false);

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
      router.navigate("/MainPage");
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
      router.navigate('/MainPage');
      setEmailAuth(false);
      setEmailCode(false);
    }else{
      alert("Incorrect code. Please try again...");
    }
  }

  return (<div className="MainPage">
    <div className="navBar">
      <div className="landingPage">
        <button onClick={() => router.navigate('/')}><img src={landingPage} alt="Landing Page"/></button>
      </div>
      <h1 className="title">
        DTT Quiz App - Login
      </h1>
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
  const handleClick = async (e) =>{
    //Need to add logic to check if there is already a valid token set up. If there is, let the user get in. If there is not, check if there is a refresh token available and see if
    //we can get a new access token. If there is no refresh token, send them to the login page.
    if (e == null){
      return;
    }
    
    if (await db.verifyTokens()){
      router.navigate("/MainPage");
    }else{
      router.navigate("/Login");
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

export default MainPage();