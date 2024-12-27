import React, { useState } from 'react';
import {baseUrl} from "../GlobalContext";

const SettingsMainPage = () => {

    // State to store the selected radio button value
    const [selectedOption, setSelectedOption] = useState('');
    const [textFieldValue, setTextFieldValue] = useState('');
    const [deleteGameNumber, setDeleteGameNumber] = useState('');

    const triggerSynchronisation = async () => {
        try {
            const response = await fetch(baseUrl + 'api/v1/syncWithApi/' + selectedOption, {
                method: "post",
                headers: new Headers({
                    "ngrok-skip-browser-warning": true,
                    "Authorization": "Basic " + btoa(localStorage.getItem("username") + ":" + localStorage.getItem("password"))
                }),
            });
        } catch (error) {
            console.error('Error fetching row data:', error);
        }
    };

    const addApiKey = async () => {
        try {
            const data = {
                apiKey: textFieldValue,
            };
            
            const response = await fetch(baseUrl + 'api/v1/add-api-key', {
                method: "put",
                headers: new Headers({
                    "ngrok-skip-browser-warning": true,
                    "Content-Type" : "application/json",
                    "Authorization": "Basic " + btoa(localStorage.getItem("username") + ":" + localStorage.getItem("password"))
                }),
                body: JSON.stringify(data),
            });
        } catch (error) {
            console.error('Error fetching row data:', error);
        }
    };

    const deleteGame = async (gameNumber) => {
        try {
            await fetch(baseUrl + `api/v1/games/${gameNumber}`, {
                method: "delete",
                headers: new Headers({
                    "Content-Type" : "application/json",
                    "Authorization": "Basic " + btoa(localStorage.getItem("username") + ":" + localStorage.getItem("password"))
                }),
            });
        } catch (error) {
            console.error('Error fetching row data:', error);
        }
    };
    
    // Function to handle the radio button change
    const handleRadioChange = (event) => {
        setSelectedOption(event.target.value);
    };

    const handleInputChange = (event) => {
        setTextFieldValue(event.target.value);
    };

    const handleDeleteGameInputChange = (event) => {
        setDeleteGameNumber(event.target.value);
    };

    // Function to handle the button click
    const handleSyncButtonClick = () => {
        // Trigger the function with the selected radio button value
        triggerSynchronisation();
    };

    const handleApiKeyButtonClick = () => {
        addApiKey();
    };

    const handleDeleteGameButtonClick = () => {
        deleteGame(deleteGameNumber);
    };
    
    return (
        <div>
            <h3>Sync für Spieler</h3>
            <div>
                <input
                    type="radio"
                    id="option1"
                    name="options"
                    value="Florian"
                    checked={selectedOption === 'Florian'}
                    onChange={handleRadioChange}
                />
                <label htmlFor="option1">Florian</label>
            </div>
            <div>
                <input
                    type="radio"
                    id="option2"
                    name="options"
                    value="Erik"
                    checked={selectedOption === 'Erik'}
                    onChange={handleRadioChange}
                />
                <label htmlFor="option2">Erik</label>
            </div>
            <div>
                <input
                    type="radio"
                    id="option3"
                    name="options"
                    value="Daniel"
                    checked={selectedOption === 'Daniel'}
                    onChange={handleRadioChange}
                />
                <label htmlFor="option3">Daniel</label>
            </div>

            <button onClick={handleSyncButtonClick}>Sync mit Lol-Api</button>
            <div>
                <h3>Enter api key</h3>
                <div><a href="https://developer.riotgames.com/" target="_blank" rel="noopener noreferrer">
                    Api-Website
                </a>
                </div>
                <input
                    type="text"
                    value={textFieldValue}
                    onChange={handleInputChange}
                    placeholder="Api key"
                />
                <button onClick={handleApiKeyButtonClick}>Insert Api key</button>
            </div>

            <div>
                <h3>Delete Game</h3>
                <input
                    type="text"
                    value={deleteGameNumber}
                    onChange={handleDeleteGameInputChange}
                    placeholder="Delete Game"
                />
                <button onClick={handleDeleteGameButtonClick}>Delete Game</button>
            </div>
        </div>


    );
};


export default SettingsMainPage;
