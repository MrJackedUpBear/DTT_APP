import React from 'react';
import ReactDOM from 'react-dom/client';
import './index.css';
import * as app from './App';
import reportWebVitals from './reportWebVitals';
import { createBrowserRouter, RouterProvider } from 'react-router-dom';
import * as quiz from './Quiz';
import * as qfend from './QuestionsFrontEnd';
import * as suggestions from './Suggestions';

 const router = createBrowserRouter([
    {
      path: 'Quiz',
      element: <quiz.StartQuiz />
    },
    {
      path: 'Quiz/CorrectAnswer',
      element: <quiz.CorrectAnswer />
    },
    {
      path: 'Quiz/SameQuestion',
      element: <quiz.WrongAnswer />
    },
    {
      path:'Questions',
      element:<qfend.ViewAndUpdate/>
    },
    {
      path:'Questions/Add',
      element:<qfend.AddQuestions />,
    },
    {
      path: 'Questions/Add/Submit',
      element:<qfend.SubmitAddQuestions/>,
    },
    {
      path: '',
      element:<app.MainPage/>,
    },
    {
      path: 'Questions/Next',
      element:<qfend.Next/>
    },
    {
      path: 'Questions/Back',
      element:<qfend.Back/>
    },
    {
      path: 'Questions/Edit',
      element:<qfend.Edit/>
    },
    {
      path: 'Questions/Edit/Prompt',
      element:<qfend.EditPrompt/>
    },
    {
      path: 'Questions/Edit/CorrectAnswer',
      element:<qfend.EditCorrectAnswer/>
    },
    {
      path: 'Questions/Edit/WrongAnswers',
      element:<qfend.EditWrongAnswers/>
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
      path:'Questions/Add/Verify',
      element:<qfend.VerifyQuestions/>
    },
    {
      path:'Questions/Add/Verify/Edit',
      element:<qfend.Edit/>
    },
    {
      path:'Questions/Add/Verify/Edit/Prompt',
      element:<qfend.EditPrompt/>
    },
    {
      path:'Questions/Add/Verify/Edit/CorrectAnswer',
      element:<qfend.EditCorrectAnswer/>
    },
    {
      path:'Questions/Add/Verify/Edit/WrongAnswers',
      element:<qfend.EditWrongAnswers/>
    },
    {
      path:'Questions/Add/Verify/Back',
      element:<qfend.Back/>
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
