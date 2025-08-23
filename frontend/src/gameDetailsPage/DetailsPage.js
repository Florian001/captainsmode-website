import React, {useEffect, useState} from 'react';
import {useNavigate, useParams} from 'react-router-dom';
import TabSegment from './TabSegment';
import GameResultTable from "./GameResultTable";
import EditableText from "./EditableText";
import GameResultOverviewTable from "./GameResultOverviewTable";
import {baseUrl} from "../GlobalContext";

const DetailsPage = () => {
    const { id } = useParams();
    const navigate = useNavigate();
    const [game, setGame] = useState([]);
    const [players, setPlayers] = useState([]);
    const [loading, setLoading] = useState(true);
    const [gameNotExistent, setGameNotExistent] = useState(false);

    const formatter = new Intl.DateTimeFormat('de-DE', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit',
        hour: 'numeric',
        minute: 'numeric',
        second: 'numeric'
    });

    const incrementGame = () => {
        navigate(`/details/${parseInt(id) + 1}`);
        window.location.reload();
    };

    const decrementGame = () => {
        navigate(`/details/${Math.max(1, parseInt(id) - 1)}`);
        window.location.reload();
    };
    
    function getGameResultTableData(participations) {
        return {
            top: getParticipation(participations, "TOP"),
            jgl: getParticipation(participations, "JGL"),
            mid: getParticipation(participations, "MID"),
            adc: getParticipation(participations, "ADC"),
            sup: getParticipation(participations, "SUP"),
        };
    }

    function getParticipation(participations, role) {
        const participationObleute =  participations
            .filter(gp => gp.role === role)
            .filter(gp => (gp.player.name !== "Gegner"))[0];
        const participationEnemy =  participations
            .filter(gp => gp.role === role)
            .filter(gp => (gp.player.name === "Gegner"))[0];

        return {
            obleute: {
                summonerName: participationObleute["summonerName"],
                championName: participationObleute["championName"],
                gold: participationObleute["goldEarned"],
                kills: participationObleute["kills"],
                deaths: participationObleute["deaths"],
                assists: participationObleute["assists"],
                champExperience: participationObleute["champExperience"],
                champLevel: participationObleute["champLevel"]
            },
            enemy: {
                summonerName: participationEnemy["summonerName"],
                championName: participationEnemy["championName"],
                gold: participationEnemy["goldEarned"],
                kills: participationEnemy["kills"],
                deaths: participationEnemy["deaths"],
                assists: participationEnemy["assists"],
                champExperience: participationEnemy["champExperience"],
                champLevel: participationEnemy["champLevel"]
            }
        }
    }
    
    useEffect(() => {
        const fetchData = async () => {
            try {
                setLoading(true);
                const playersResponse = await fetch(baseUrl + 'api/v1/players', {
                    method: "get",
                    headers: new Headers({
                        "ngrok-skip-browser-warning": true,
                        "Authorization": "Basic " + btoa(localStorage.getItem("username") + ":" + localStorage.getItem("password"))
                    }),
                });
                const playersData = await playersResponse.json();
                setPlayers(playersData);

                const gameResponse = await fetch(baseUrl + `api/v1/games/${id}`, {
                    method: "get",
                    headers: new Headers({
                        "ngrok-skip-browser-warning": true,
                        "Authorization": "Basic " + btoa(localStorage.getItem("username") + ":" + localStorage.getItem("password"))
                    }),
                });
                if (gameResponse.status === 400) {
                    setGameNotExistent(true);
                    return;
                }
                const gameData = await gameResponse.json();
                setGame(gameData);
                
            } catch (error) {
                setGameNotExistent(true);
            } finally {
                setLoading(false);
            }
        };
        
        fetchData();
    }, [id]);

    if (loading) {
        return <div>Loading...</div>;
    }
    
    if (gameNotExistent) {
        return <div className="header-container">
            {/* Left Arrow */}
            <span className="header-arrow" onClick={decrementGame} title="Previous Game">
                ⬅️
            </span>

            <h1 className="header-title">Dieses Spiel gibts noch nicht, trommel erstmal alle Obleute zusammen und spiele 'ne Runde!</h1>

            {/* Right Arrow */}
            <span className="header-arrow" onClick={incrementGame} title="Next Game">
                ➡️
            </span>
        </div>
    }

    const updateDescription = async (matchNumber, newText) => {
        const url = baseUrl + `api/v1/games/${matchNumber}/description`;

        const request = {
            description: newText
        };

        try {
            const response = await fetch(url, {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json',
                    "ngrok-skip-browser-warning": true,
                    "Authorization": "Basic " + btoa(localStorage.getItem("username") + ":" + localStorage.getItem("password"))
                },
                body: JSON.stringify(request), // Convert the data to a JSON string
            });

            if (!response.ok) {
                throw new Error('Network response was not ok');
            }
            console.log("UPDATED DESCRIPTION TO " + newText);
        } catch (error) {
            console.error('Error updating data:', error);
        }
    };
    
    const handleSave = (newText) => {
        console.log(newText);
        if (newText === "") {
            newText = "Spielbeschreibung";
        }
        updateDescription(game.number, newText);
    };
    
    return (
        <div>

            <div className="header-container">
                {/* Left Arrow */}
                <span className="header-arrow" onClick={decrementGame} title="Previous Game">
                ⬅️
            </span>
                
                <h1 className="header-title">{`Spiel ${game.number} am 
                ${formatter.format(new Date(game.date))}`}</h1>

                {/* Right Arrow */}
                <span className="header-arrow" onClick={incrementGame} title="Next Game">
                ➡️
            </span>
            </div>
            <GameResultOverviewTable game={game}/>

            <EditableText initialText={game.description === null ? "Spielbeschreibung" : game.description}
                          initalGame={game} onSave={handleSave}/>
            <GameResultTable gameResultTableData={getGameResultTableData(game.participations)}/>
            <TabSegment participations={game.participations} players={players}/>
        </div>
    );
}

export default DetailsPage;
