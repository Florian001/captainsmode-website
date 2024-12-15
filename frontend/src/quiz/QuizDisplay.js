import React, {useEffect, useState} from 'react';
import {baseUrl} from "../GlobalContext";
import ReloadButton from "./ReloadButton";

const QuizDisplay = () => {
    
    const [question, setQuestion] = useState(null);

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

    useEffect(() => {
        fetchCurrentQuestion();
    }, []);
 
    // const handleSubmit = (e) => {
    //     e.preventDefault();
    //     if (inputName === null) {
    //         alert('Hey, gibt doch bitte einen Namen ein.');
    //         return;
    //     }
    //     if (inputName.trim()) {
    //         localStorage.setItem('quiz_name', inputName);
    //         setName(inputName)
    //     } else {
    //         alert('Hey, gibt doch bitte einen Namen ein.');
    //     }
    // };
    //
    //
    //
    //
    // const nextQuestion = async (number) => {
    //     const url = baseUrl + `api/v1/quiz/question/${number}`;
    //    
    //     try {
    //         const response = await fetch(url, {
    //             method: 'PUT',
    //             headers: {
    //                 "Authorization": "Basic " + btoa(localStorage.getItem("username") + ":" + localStorage.getItem("password"))
    //             },
    //         });
    //
    //         if (!response.ok) {
    //             throw new Error('Network response was not ok');
    //         }
    //     } catch (error) {
    //         console.error('Error setting next question', error);
    //     }
    // };
    //
    // const handleNextQuestionButton = (e) => {
    //     nextQuestion(question.number + 1);
    // };
    
    return <div>
            <h1>Frage Nummer 1231241 {question.number}</h1>
            <h2> {question.question}</h2>
            
            {/*<button onClick={handleNextQuestionButton}>Nächste Frage</button>*/}
    </div>
}

export default QuizDisplay;
