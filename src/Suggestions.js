import { useState } from 'react';
import * as db from './Database';
import router from './index';

export function Suggestions(){
    const [inputVal, setInputVal] = useState("");

    const handleChange = (event) => {
        setInputVal(event.target.value);
    };
    
    return (
        <div>
            <h1>
                Suggestions
            </h1>
            <b1>Enter suggestion:</b1>
            <input
            type="text"
            id="suggestion"
            input={inputVal}
            onChange={handleChange}
            placeholder="Enter here."/>
            <button onClick={() => submit(inputVal)}>Submit</button>
        </div>
    );
}

async function submit(message){
    const baseURL = db.getBaseURL();
    const sendEmailURL = "Email";

    try{
        const myHeaders = new Headers();
		myHeaders.append("Content-Type", "application/json");
        let accessToken = "Bearer " + btoa(localStorage.getItem("AccessToken"));

	    myHeaders.append("Authorization", accessToken);
		const response = await fetch(baseURL + 'Quiz/' + sendEmailURL,{
			headers:myHeaders,
			method:"POST",
			body: JSON.stringify({"Message":message}),
		});

        if (!response.ok){
			console.log("Bad response: " + response.status);
			return;
		}

        router.navigate('Sent');
    }catch (e){
        alert("Error: " + e);
    }
}

export function Sent(){
    return (
        <div>
            Suggestion sent successfully.
            <button onClick={() => router.navigate("/")}>Go Home</button>
            <button onClick={() => router.navigate("/Suggestions")}>Add Another Suggestion</button>
        </div>
    );
}