import React, {useEffect, useState} from "react";
import {baseUrl} from "../GlobalContext";

const Scoreboard = () => {

    const [scoreboard, setScoreboard] = useState([]);

    const getScoreboard = async () => {
        try {
            const response = await fetch(baseUrl + 'api/v1/quiz/champion-quiz/result', {
                method: "get",
                headers: new Headers({
                    "ngrok-skip-browser-warning": true,
                    "Authorization": "Basic " + btoa(localStorage.getItem("username") + ":" + localStorage.getItem("password"))
                }),
            });
            const scoreboard = await response.json();
            setScoreboard(scoreboard);

        } catch (error) {
            console.error('Error fetching scoreboard:', error);
        }
    };

    useEffect(() => {
        getScoreboard();
    }, []);
    
    return (
        <div className="scoreboard-container">
            <h1 className="scoreboard-title">Bestenliste Champion Quiz</h1>
            <table className="scoreboard-table">
                <thead>
                <tr>
                    <th>Rank</th>
                    <th>Name</th>
                    <th>Points</th>
                </tr>
                </thead>
                <tbody>
                {scoreboard.map((entry, index) => (
                    <tr key={index}>
                        <td>{index + 1}</td>
                        <td className="name-cell">
                            {entry.person}
                {/*            <span className="hover-date">*/}
                {/*  {new Date(entry.dateTime).toLocaleString()}*/}
                {/*</span>*/}
                        </td>
                        <td>{entry.points}</td>
                    </tr>
                ))}
                </tbody>
            </table>
        </div>
    );
}

export default Scoreboard;
