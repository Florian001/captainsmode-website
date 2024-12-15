import React, {useState} from 'react';
import {baseUrl} from "../GlobalContext";

const InsertQuestion = () => {

    const [inputNumber, setInputNumber] = useState("");
    const [inputQuestion, setInputQuestion] = useState("");
    const [inputCorrectAnswer, setInputCorrectAnswer] = useState("");

    const insertQuestion = async (number, question, correctAnswer) => {
        const url = baseUrl + `api/v1/quiz/question`;

        const request = {
            number: number,
            question: question,
            correctAnswer: correctAnswer
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
            console.error('Error inserting question:', error);
        }
    };
    
    
    const handleSubmit = (e) => {
        insertQuestion(inputNumber, inputQuestion, inputCorrectAnswer);
    };
    
    
        return (
            <div>
                <h1>Frage hinzufügen</h1>
                <form onSubmit={handleSubmit}>
                    <label>
                        Bitte trage hier die Frage ein: <br/>
                        <input
                            type="text"
                            value={inputQuestion}
                            onChange={(e) => setInputQuestion(e.target.value)}
                            placeholder="Frage"
                        />
                    </label>

                    <br/>

                    <label>
                        Bitte trage hier die Antwort ein: <br/>
                        <input
                            type="text"
                            value={inputCorrectAnswer}
                            onChange={(e) => setInputCorrectAnswer(e.target.value)}
                            placeholder="Antwort"
                        />
                    </label>
                    <br/>
                    <label>
                        Nummer: <br/>
                        <input
                            type="string"
                            value={inputNumber}
                            onChange={(e) => setInputNumber(e.target.value)}
                            placeholder="Nummer"
                        />
                    </label>
                    <button type="submit">Eintragen!</button>
                </form>
            </div>
        );

}

export default InsertQuestion;
