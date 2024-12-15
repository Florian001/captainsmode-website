import React, {useEffect, useState} from 'react';
import {baseUrl} from "../GlobalContext";
import ReloadButton from "./ReloadButton";

const InsertAnswer = () => {


    const [question, setQuestion] = useState(null);
    const [participants, setParticipants] = useState([]);
    const [answers, setAnswers] = useState([]);
    const [trigger, setTrigger] = useState(true);
    const [points, setPoints] = useState([]);
    
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
            fetchParticipantAnswers(questionData.number);
            
        } catch (error) {
            setQuestion(null);
            console.error('Error fetching question:', error);
        } 
    };

    const fetchParticipants = async () => {
        try {

            const participantsResponse = await fetch(baseUrl + 'api/v1/quiz/participants', {
                method: "get",
                headers: new Headers({
                    "Authorization": "Basic " + btoa(localStorage.getItem("username") + ":" + localStorage.getItem("password"))
                }),
            });
            const participantsData = await participantsResponse.json();
            setParticipants(participantsData);

        } catch (error) {
            console.error('Error fetching participants:', error);
        }
    };

    const fetchParticipantAnswers = async (number) => {
        try {

            const response = await fetch(baseUrl + `api/v1/quiz/question/${number}/answers`, {
                method: "get",
                headers: new Headers({
                    "Authorization": "Basic " + btoa(localStorage.getItem("username") + ":" + localStorage.getItem("password"))
                }),
            });
            const responseData = await response.json();
            setAnswers(responseData);
            responseData.map((item, index) => (
                setPoints((prevPoints) => ({
                        ...prevPoints,
                        [item.name]: item.points}
            ))));
        } catch (error) {
            console.error('Error fetching participants:', error);
        }
    };
    

    useEffect(() => {
        fetchCurrentQuestion();
        fetchParticipants();
    }, [trigger]);

    
    const nextQuestion = async (number) => {
        const url = baseUrl + `api/v1/quiz/question/${number}`;
        console.log("Trying PUT on " + url);
        try {
            const response = await fetch(url, {
                method: 'PUT',
                headers: {
                    "Authorization": "Basic " + btoa(localStorage.getItem("username") + ":" + localStorage.getItem("password"))
                },
            });

            if (!response.ok) {
                throw new Error('Network response was not ok');
            }
        } catch (error) {
            console.error('Error setting next question', error);
        }
    };

    const submitPoints = async (number) => {
        const url = baseUrl + `api/v1/quiz/question/${number}/points`;

        const request = Object.entries(points).map(([name, point]) => ({
            participant: name,
            points: point,
        }));
        
        try {
            const response = await fetch(url, {
                method: 'POST',
                headers: {
                    "Authorization": "Basic " + btoa(localStorage.getItem("username") + ":" + localStorage.getItem("password")),
                    "Content-Type": "application/json"
                },
                body: JSON.stringify(request)
            });

            if (!response.ok) {
                throw new Error('Network response was not ok');
            }
        } catch (error) {
            console.error('Error posting points', error);
        }
    };
    
    const toggleTrigger = () => {
        setTrigger(!trigger);
    }
    
    const handleNextQuestionClick = async () => {
        await nextQuestion(question.number + 1);
        toggleTrigger();
        // window.location.reload();
    };

    const handlePreviousQuestionClick = async () => {
        console.log(question.number - 1);
        await nextQuestion(question.number - 1);
        toggleTrigger();
        // window.location.reload();
    };
    
    const handleSubmitPointsClick = async () => {
        await submitPoints(question.number);
        toggleTrigger();
        // window.location.reload();
    };
    
    
    
    const handleCloseQuestionClick = async () => {
        window.location.reload();
    };

    // Increment points for a specific name
    const incrementPoints = (name) => {
        setPoints((prevPoints) => ({
            ...prevPoints,
            [name]: prevPoints[name] + 1,
        }));
    };

    // Decrement points for a specific name
    const decrementPoints = (name) => {
        setPoints((prevPoints) => ({
            ...prevPoints,
            [name]: Math.max(prevPoints[name] - 1, 0), // Prevent negative points
        }));
    };
    
    
        if (question === null) {
            return <div>
                Lädt
            </div>
        } else if (participants === null) {
            return <div>
                Lädt
            </div>
        } else if (answers === null) {
            return <div>
                Lädt
            </div>
        } else {
            return <div>
                <h1>Frage Nummer {question.number}</h1>
                <h2> {question.question}</h2>

                {answers.length > 0 && <h3 style={{marginBottom: '16px'}}>Antworten</h3>}
                <div style={{display: 'flex', flexWrap: 'wrap', gap: '10px'}}>
                    {answers.map((item, index) => (
                        <div
                            key={index}
                            style={{
                                border: '1px solid #ccc',
                                padding: '10px',
                                borderRadius: '8px',
                                width: '150px',
                                textAlign: 'center',
                            }}
                        >
                            <h3>{item.name}</h3>
                            <p>{item.answer}</p>
                            <p> +{points[item.name]} Punkte</p>
                            <button onClick={() => decrementPoints(item.name)}>-</button>
                            <button onClick={() => incrementPoints(item.name)}>+</button>

                        </div>
                    ))}
                </div>

                {participants.length > 0 && <h3 style={{marginBottom: '16px'}}>Punkte</h3>}
                <div style={{display: 'flex', flexWrap: 'wrap', gap: '10px'}}>
                    {/* Render a box for each data item */}
                    {participants.map((item, index) => (
                        <div
                            key={index}
                            style={{
                                border: '1px solid #ccc',
                                padding: '10px',
                                borderRadius: '8px',
                                width: '150px',
                                textAlign: 'center',
                            }}
                        >
                            <h3>{item.name}</h3>
                            <p>Punkte: {item.points}</p>
                        </div>
                    ))}
                </div>

                <div>
                    <button onClick={handleCloseQuestionClick}>Antworten anzeigen</button>
                </div>
                <div>
                    <button onClick={handleSubmitPointsClick}>Punkte eintragen</button>
                </div>
                <button onClick={handlePreviousQuestionClick}>Vorherige Frage</button>
                <button onClick={handleNextQuestionClick}>Nächste Frage</button>
            </div>
        }
}

export default InsertAnswer;
