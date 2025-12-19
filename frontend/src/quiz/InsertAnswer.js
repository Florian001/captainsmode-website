import React, {useEffect, useState} from 'react';
import {baseUrl} from "../GlobalContext";
import ReloadButton from "./ReloadButton";

const InsertAnswer = () => {

    const [inputName, setInputName] = useState(localStorage.getItem('quiz_name'));
    const [name, setName] = useState(localStorage.getItem('quiz_name'));
    const [question, setQuestion] = useState(null);
    const [answer, setAnswer] = useState("");
    const [hasAnswered, setHasAnswered] = useState(false);
    const [yourAnswer, setYourAnswer] = useState("");
    
    const fetchCurrentQuestion = async () => {
        try {
            
            const questionResponse = await fetch(baseUrl + 'api/v1/quiz/question', {
                method: "get",
                headers: new Headers({
                    "Authorization": "Basic " + btoa(localStorage.getItem("username") + ":" + localStorage.getItem("password"))
                }),
            });
            const questionData = await questionResponse.json();
            setQuestion(questionData);
            
        } catch (error) {
            setQuestion(null);
            console.error('Error fetching question:', error);
        } 
    };
    
    
    
    const handleSubmit = (e) => {
        e.preventDefault();
        if (inputName === null) {
            alert('Hey, gibt doch bitte einen Namen ein.');
            return;
        }
        if (inputName.trim()) {
            localStorage.setItem('quiz_name', inputName);
            setName(inputName)
        } else {
            alert('Hey, gibt doch bitte einen Namen ein.');
        }
    };

    useEffect(() => {
        fetchCurrentQuestion();
    }, []);


    const insertAnswer = async (text) => {
        const url = baseUrl + `api/v1/quiz/answer`;

        const request = {
            question: question.number,
            person: name,
            answer: text
        };

        try {
            const response = await fetch(url, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    "Authorization": "Basic " + btoa(localStorage.getItem("username") + ":" + localStorage.getItem("password"))
                },
                body: JSON.stringify(request), // Convert the data to a JSON string
            });

            if (!response.ok) {
                throw new Error('Network response was not ok');
            }
        } catch (error) {
            console.error('Error inserting answer:', error);
        }
    };
    
    const handleAnswerSubmit = (e) => {
        insertAnswer(answer);
        setHasAnswered(true);
        setYourAnswer(answer);
    };
    
    if (name) {
        if (question === null) {
            return <div>
                <h1> Hallo {name} </h1>
                <h2> Es ist keine Frage zur Zeit ausgewählt</h2>
                <ReloadButton/>
            </div>
        }
        return <div>
            <h1> Hallo {name} </h1>
            <h2>Frage Nummer {question.number}</h2>
            <h3> {question.question}</h3>
            <label>
                <input
                    type="text"
                    value={answer}
                    onChange={(e) => setAnswer(e.target.value)}
                    placeholder="Antwort"
                />
                <button onClick={handleAnswerSubmit}>Abschicken!</button>
            </label>
            {hasAnswered && <div><p>{yourAnswer} wurde abgeschickt!</p> <ReloadButton/></div>}
        </div>
    } else {
        return (
            <div>
                <h1>Herzlich Willkommen zum Weihnachtsquiz</h1>
                <form onSubmit={handleSubmit}>
                    <label>
                        Bitte trage hier deinen Teamnamen ein: <br/>
                        <input
                            type="text"
                            value={inputName}
                        onChange={(e) => setInputName(e.target.value)}
                        placeholder="Dein Name"
                    />
                </label>
                <button type="submit">Loslegen!</button>
            </form>
                <ReloadButton/>
            </div>
        );
    }
}

export default InsertAnswer;
