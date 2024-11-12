import React, { useEffect, useState } from 'react';
import Tooltip from "./Tooltip";
import {baseUrl} from "../GlobalContext";
import {Link} from "react-router-dom";
import Login from "../authentication/AuthenticationPage";

const OverviewTable = () => {
    const [players, setPlayers] = useState([]);
    const [games, setGames] = useState([]);
    const [isAuth, setIsAuth] = useState(true);

    const updateIsAuth = (newValue) => {
        setIsAuth(newValue);
    }
    
    const formatter = new Intl.DateTimeFormat('de-DE', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit',
        hour: 'numeric',
        minute: 'numeric',
        second: 'numeric'
    });
    
    useEffect(() => {
        
        
        // Fetch players data
        const fetchPlayers = async () => {
            try {
                const response = await fetch(baseUrl + 'api/v1/players', {
                    method: "get",
                    headers: new Headers({
                        "ngrok-skip-browser-warning": true,
                        "Authorization": "Basic " + btoa(localStorage.getItem("username") + ":" + localStorage.getItem("password"))
                    }),
                });  
                const data = await response.json();
                setPlayers(data);
            } catch (error) {
                console.error('Error fetching players:', error);
                setIsAuth(false)
            }
        };

        // Fetch row data
        const fetchRowData = async () => {
            try {
                const response = await fetch(baseUrl + 'api/v1/games', {
                    method: "get",
                    headers: new Headers({
                        "ngrok-skip-browser-warning": true,
                        "Authorization": "Basic " + btoa(localStorage.getItem("username") + ":" + localStorage.getItem("password"))
                    }),
                }); 
                const data = await response.json();
                setGames(data);
            } catch (error) {
                console.error('Error fetching row data:', error);
                setIsAuth(false)
            }
        };
        
        fetchPlayers();
        fetchRowData();
    }, [isAuth]);

    const handleDoubleClick = async (gameIndex, playerName) => {
        const url = baseUrl + `api/v1/games/${gameIndex}/captain/${playerName}`;
        console.log("Hier bin ich");
        try {
            const response = await fetch(url, {
                method: "put",
                headers: new Headers({
                    "ngrok-skip-browser-warning": true,
                }),
            });

            if (!response.ok) {
                throw new Error('Network response was not ok');
            }

            const result = await response.json();
            console.log('Success:', result);
        } catch (error) {
            console.error('Error:', error);
        }

        try {
            const response = await fetch(baseUrl + 'api/v1/games', {
                method: "get",
                headers: new Headers({
                    "ngrok-skip-browser-warning": true,
                }),
            });
            const data = await response.json();
            setGames(data);
        } catch (error) {
            console.error('Error fetching row data:', error);
        }
    };
    
    if (isAuth) {
        return (
            <table className="overview-table">
                <thead>
                <tr>
                    <th></th>
                    {players.map(player => (
                        <th key={player.id} id="td-header">{player.name}</th>
                    ))}
                    <th>Dauer</th>
                </tr>
                </thead>
                <tbody>
                {games.map((game, gameIndex) => (
                    <tr key={gameIndex} id={`tr-win-${game.win}`}>
                        <td className={`captain-missing-${game.captain === null} td-game-number`}>
                            <Tooltip text={
                                <span id="span-date">{formatter.format(new Date(game.date))}</span>}
                            >
                                <span id="span-game-number">{game.number}</span>
                            </Tooltip>

                        </td>
                        {game.participations.map((participation, contentIndex) => (
                            participation?.player.name !== "Gegner" &&
                            <td key={contentIndex}
                                className={`td-captain-${game.captain !== null && participation?.player.name === game.captain?.name} td-best-player-${game.bestCcv === participation?.ccv} overview-td`}
                                onDoubleClick={() => handleDoubleClick(game.number, participation?.player.name)}>
                                {participation !== null &&
                                    <Tooltip text={
                                        <div className="td-content">
                                            <img src={`${participation.role}.png`} alt={participation.role}
                                                 className="icon"/>
                                            <span id="td-left">
                                        {`${participation?.kills} ${participation?.deaths} ${participation?.assists}`}
                                    </span>
                                            <span id="td-right">
                                        {` ${participation?.championName}`}
                                    </span>
                                        </div>
                                    }>
                                        <div className="td-content">

                                            <img src={`${participation.role}.png`} alt={participation.role}
                                                 className="icon"/>
                                            <span
                                                id="td-left">
                                        {participation?.kills - participation?.deaths + participation?.assists / 2}</span>
                                            <span id="td-right"> {participation?.championName}</span>
                                        </div>
                                    </Tooltip>}
                            </td>
                        ))}
                        {/* Add an empty cell if there are less than 6 cells in the row */}
                        <td id="td-duration">{Math.floor(game.durationInSeconds / 60)}:{game.durationInSeconds % 60 < 10 ? `0${game.durationInSeconds % 60}` : game.durationInSeconds % 60} min</td>
                        <td id="td-description" title={game.description}>{game.description}</td>
                        <td id="td-detail-link">
                            <Link to={`/details/${game.number}`}>
                                <img src={`arrow.png`} alt={"Arrow"}
                                     className="icon"/>
                            </Link>
                        </td>
                    </tr>
                ))}
                </tbody>
            </table>
        );
    } else {
        return <Login updateIsAuth={updateIsAuth}/>
    }
};

export default OverviewTable;
