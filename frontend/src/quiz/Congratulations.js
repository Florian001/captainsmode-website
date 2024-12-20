import React, {useEffect, useState} from 'react';
import '../App.css';
import {baseUrl} from "../GlobalContext"; // Assuming you have a separate CSS file for styles

const Congratulations = ({ name, points }) => {
    const [bestPlayer, setBestPlayer] = useState([]);

    const fetchParticipants = async () => {
        try {
            const participantsResponse = await fetch(baseUrl + 'api/v1/quiz/participant/best', {
                method: "get",
                headers: new Headers({
                    "Authorization": "Basic " + btoa(localStorage.getItem("username") + ":" + localStorage.getItem("password"))
                }),
            });
            const participantsData = await participantsResponse.json();
            setBestPlayer(participantsData);

        } catch (error) {
            console.error('Error fetching participants:', error);
        }
    };

    useEffect(() => {
        fetchParticipants();
    }, []);
    
    return (
        <div className="congratulations-banner">
            <div className="congratulations-content">
                Weihnachtsquiz 2024 Gewinner: <br/>
                {bestPlayer.name} - {bestPlayer.points} points
                <br/>
                GG WP!
            </div>
        </div>
    );
};

export default Congratulations;
