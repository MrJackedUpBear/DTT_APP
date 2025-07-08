import React from 'react';
import ReactDOM from 'react-dom/client';
import './index.css';
import * as app from './App';
import reportWebVitals from './reportWebVitals';
import { BrowserRouter, createBrowserRouter, RouterProvider } from 'react-router-dom';
import * as quiz from './Quiz';
import * as qfend from './QuestionsFrontEnd';

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
      element:<qfend.EditQuestions />
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
