import React from 'react';
import ReactDOM from 'react-dom/client';
import './index.css';
import * as app from './App';
import reportWebVitals from './reportWebVitals';
import { createBrowserRouter, RouterProvider } from 'react-router-dom';
import * as quiz from './Quiz';
import * as qfend from './QuestionsFrontEnd';
import * as suggestions from './Suggestions';
import * as settings from './Settings';

 const router = createBrowserRouter([
    {
      path: 'MainPage/Quiz',
      element: <quiz.StartQuiz />
    },
    {
      path: 'MainPage/Quiz/CorrectAnswer',
      element: <quiz.CorrectAnswer />
    },
    {
      path: 'MainPage/Quiz/SameQuestion',
      element: <quiz.WrongAnswer />
    },
    {
      path:'MainPage/Questions',
      element:<qfend.ViewAndUpdate/>
    },
    {
      path:'MainPage/Questions/Add',
      element:<qfend.AddQuestions />,
    },
    {
      path: 'MainPage/Questions/Add/Submit',
      element:<qfend.SubmitAddQuestions/>,
    },
    {
      path: 'MainPage',
      element:<app.MainPage/>,
    },
    {
      path: 'MainPage/Questions/Next',
      element:<qfend.Next/>
    },
    {
      path: 'MainPage/Questions/Back',
      element:<qfend.Back/>
    },
    {
      path:'Suggestions',
      element:<suggestions.Suggestions/>
    },
    {
      path:'Suggestions/Sent',
      element:<suggestions.Sent/>
    },
    {
      path:'MainPage/Questions/Add/Verify',
      element:<qfend.VerifyQuestions/>
    },
    {
      path:'MainPage/Questions/Add/Verify/Back',
      element:<qfend.Back/>
    },
    {
      path:'/',
      element:<app.LandingPage/>
    },
    {
      path: 'MainPage/Settings',
      element: <settings.Settings/>
    },
    {
      path: 'Login',
      element: <app.LoginPage/>
    },
    {
      path: 'MainPage/Questions/Edit',
      element: <qfend.EditQuestions/>
    },
    {
      path: 'MainPage/Quiz/FinishedQuiz',
      element: <quiz.FinishedQuiz/>
    }
  ]);

const root = ReactDOM.createRoot(document.getElementById('root'));
root.render(
  <RouterProvider router={router} />
);

export default router;

// If you want to start measuring performance in your app, pass a function
// to log results (for example: reportWebVitals(console.log))
// or send to an analytics endpoint. Learn more: https://bit.ly/CRA-vitals
reportWebVitals();
