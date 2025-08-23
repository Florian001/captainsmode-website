import React, {useEffect, useState} from 'react';
import {baseUrl, dataDragonVersion} from "../GlobalContext";
import GridDisplayAnswer from "./GridDisplayAnswer";
import Scoreboard from "./Scoreboard";


const ChampQuizMainPage = () => {
    const [pickedChamp, setPickedChamp] = useState("");
    const [points, setPoints] = useState(0);
    const [round, setRound] = useState(1);
    const [allChampsData, setAllChampsData] = useState([]);
    const [passiveUrlEnding, setPassiveUrlEnding] = useState("");
    const [tipps, setTipps] = useState(new Map([
        ["title", {
            "desc": "Titel",
            "cost": 0,
            "costMin": 3,
            "costMax": 6,
            "tipp": "",
        }],
        ["tags", {
            "desc": "Positionen",
            "cost": 0,
            "costMin": 1,
            "costMax": 3,
            "tipp": "",
        }],
        ["blurb", {
            "desc": "Story",
            "cost": 0,
            "costMin": 4,
            "costMax": 8,
            "tipp": "",
        }],
        ["info", {
            "desc": "Info",
            "cost": 0,
            "costMin": 1,
            "costMax": 1,
            "tipp": "",
        }],
        ["stats", {
            "desc": "Stats",
            "cost": 0,
            "costMin": 1,
            "costMax": 1,
            "tipp": "",
        }],
        ["passive", {
            "desc": "Passive",
            "cost": 0,
            "costMin": 4,
            "costMax": 8,
            "tipp": "",
        }],
        ["startLetter", {
            "desc": "1. Buchst.",
            "cost": 0,
            "costMin": 4,
            "costMax": 8,
            "tipp": "",
        }]
    ]));
    const [searchQuery, setSearchQuery] = useState('');
    const [currentlySelectedChampNames, setCurrentlySelectedChampNames] = useState([]);
    const [correctChampId, setCorrectChampId] = useState(200);
    const [loading, setLoading] = useState(true);
    
    const addOrUpdateTipp = (key, newTipp) => {
        setTipps((prevTipps) => {
            const updatedTipps = new Map(prevTipps); // Create a copy of the current Map
            const entry = updatedTipps.get(key); // Get the current entry for the key

            if (entry) {
                updatedTipps.set(key, { ...entry, tipp: newTipp }); // Update the tipp field
            }

            return updatedTipps; // Return the updated Map
        });
    };
    
    const resetTipps = () => {
        setTipps((prevTipps) => {
            const updatedTipps = new Map(prevTipps); // Create a copy of the current Map

            for (const [key, value] of updatedTipps.entries()) {
                const maxCost = prevTipps.get(key).costMax;
                const minCost = prevTipps.get(key).costMin;
                updatedTipps.set(key, {
                    ...value,
                    tipp: "",
                    cost: Math.floor(Math.random() * (maxCost - minCost + 1)) + minCost
                });
            }
            return updatedTipps; 
        });
    };
    
    const addPoints = (value) => {
        setPoints((prevPoints) => prevPoints + value);
    };

    const subtractPoints = (value) => {
        setPoints((prevPoints) => prevPoints - value);
    };

    const getRandomChampId = () => {
        return Math.floor(Math.random() * 169) + 1;
    }
    
    const handleNextQuestionClick = async () => {
        const newSelectedChampId = getRandomChampId();
        setCorrectChampId(newSelectedChampId);
        resetTipps();
        setPickedChamp("");
        await fetchChampData(allChampsData[newSelectedChampId].id);
        if (round+1 === 6) {
            await postResult(points);
        }
        setRound(round+1);
    };

    const handleNextGameClick = async () => {
        window.location.reload();
    };
    
    const postResult = async (points) => {
        const url = baseUrl + `api/v1/quiz/champion-quiz/result`;
        const currentDateTime = new Date().toISOString().slice(0, 19); // Removes milliseconds and 'Z'

        const request = {
            points: points,
            dateTime: currentDateTime,
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
    
    const handleTippClick = (key) => {
        if (tipps.get(key).tipp !== "") {
            return;
        }
        if (key === "blurb") {
            const champion = allChampsData[correctChampId].id;
            const championWithWhiteSpace = champion.replace(/([a-z])([A-Z])/g, '$1 $2');
            
            const blurb = allChampsData[correctChampId][key];
            const blurbWithoutApostrophes = blurb.replace(/'/g, '');
            let blurbWithoutApostrophesAndWhiteSpace = blurbWithoutApostrophes.replace(/([a-z])([A-Z])/g, '$1 $2');
            const blurbWithCancelledChampionName = blurbWithoutApostrophesAndWhiteSpace
                .replace(new RegExp(championWithWhiteSpace, 'g'), "XXX")
                .replace(new RegExp("Cho Gath", 'g'), "XXX")
                .replace(new RegExp("Prince Jarvan", 'g'), "XXX")
                .replace(new RegExp("Kai Sa", 'g'), "XXX")
                .replace(new RegExp("Dr. Mundo", 'g'), "XXX");
            addOrUpdateTipp(key, blurbWithCancelledChampionName)
        } else if (key ==="info") {
            addOrUpdateTipp(key, mapInfo(allChampsData[correctChampId][key]));
        } else if (key ==="stats") {
            addOrUpdateTipp(key, mapStats(allChampsData[correctChampId][key]));
        } else if (key === "passive"){
            addOrUpdateTipp(key, mapPassive());
        } else if (key === "startLetter") {
            addOrUpdateTipp(key, allChampsData[correctChampId].id[0]);
        } else {
            addOrUpdateTipp(key, allChampsData[correctChampId][key].toString());
        }
         subtractPoints(tipps.get(key).cost);
    };

    const mapInfo = (info) => {
        return `<table class="stats-table">
  <tr>
    <td class="stat-label">Attack:</td>
    <td class="stat-value">${info.attack}</td>
  </tr>
  <tr>
    <td class="stat-label">Defense:</td>
    <td class="stat-value">${info.defense}</td>
  </tr>
  <tr>
    <td class="stat-label">Magic:</td>
    <td class="stat-value">${info.magic}</td>
  </tr>
  <tr>
    <td class="stat-label">Difficulty:</td>
    <td class="stat-value">${info.difficulty}</td>
  </tr>
</table>
`;}

    const mapStats = (stats) => {
        return `<table class="stats-table">
  <tr>
    <td class="stat-label">HP:</td>
    <td class="stat-value">${stats.hp}</td>
  </tr>
  <tr>
    <td class="stat-label">Mana:</td>
    <td class="stat-value">${stats.mp}</td>
  </tr>
  <tr>
    <td class="stat-label">Attack:</td>
    <td class="stat-value">${stats.attackdamage}</td>
  </tr>
  <tr>
    <td class="stat-label">Movespeed:</td>
    <td class="stat-value">${stats.movespeed}</td>
  </tr>
</table>
`;}
    const mapPassive = () => {
        return `<img src=https://ddragon.leagueoflegends.com/cdn/${dataDragonVersion}/img/passive/${passiveUrlEnding}
            alt="${passiveUrlEnding}"
        title="${passiveUrlEnding}"
        />
`;}
    
    const fetchData = async () => {
        try {
            setLoading(true);
            const allChampResponse = await fetch(`https://ddragon.leagueoflegends.com/cdn/${dataDragonVersion}/data/en_US/champion.json`, {
                method: "get",
            });

            const wantedChampId = getRandomChampId();
            setCorrectChampId(wantedChampId);
            const champData = await allChampResponse.json();
            const allChampsData = Object.values(champData.data);
            setAllChampsData(allChampsData);
            const allChampNames = allChampsData.map(d => d.id);
            setCurrentlySelectedChampNames(allChampNames);
            fetchChampData(allChampsData[wantedChampId].id);
        } catch (error) {
            console.error('Error fetching players:', error);
        } finally {
            setLoading(false);
        }
    };
    useEffect(() => {
        fetchData();
        resetTipps();
    }, []);

    const fetchChampData = async (champId) => {
        try {
            const champResponse = await fetch(`https://ddragon.leagueoflegends.com/cdn/${dataDragonVersion}/data/en_US/champion/${champId}.json`, {
                method: "get",
            });
            const champData = await champResponse.json();
            const currentChampData = champData["data"][champId]["passive"]["image"]["full"];
            console.log(currentChampData);
            setPassiveUrlEnding(currentChampData);
      
        } catch (error) {
            console.error('Error fetching current champ data:', error);
        } 
    };
    
    if (loading || allChampsData[correctChampId] === undefined) {
        return "Lade..."
    } else if (round < 6) {
    return <div>
        <a href="#target-heading">Spielregeln stehen unten</a>
        <h1 style={{textAlign: 'center'}}>Champion Quiz</h1>

        <div className="info-container">
            <div className="name-box">
                <p>{localStorage.getItem("username")}</p>
            </div>
            <div className="points-box">
                <p>Punkte: {points}</p>
            </div>
            <div className="points-box">
                <p>Runde: {round}</p>
            </div>
        </div>

        <div className="lower-section">
            {[...tipps.keys()].map((key, index) => (
                <div key={key} className="main-box">
                    <div className="header-box" onClick={() => handleTippClick(key)}>
                        {tipps.get(key).desc} - {tipps.get(key).cost}P
                    </div>
                    <div
                        className={`content-box ${index === 2 ? "content-box-small" : ""}`}
                        dangerouslySetInnerHTML={{__html: tipps.get(key).tipp}}
                        onClick={() => handleTippClick(key)}
                    ></div>
                </div>
            ))}
        </div>
        
        {pickedChamp !== "" && pickedChamp === allChampsData[correctChampId].id &&
            <div className="success-container">
                <div className="success-box">
                    🎉 Glückwunsch! {allChampsData[correctChampId].id} ist richtig! 🎉
                </div>
                <div className="proceed-box" onClick={handleNextQuestionClick}>
                    Weiter
                </div>
            </div>}

        {pickedChamp !== "" && pickedChamp !== allChampsData[correctChampId].id &&
            <div className="success-container">
                <div className="wrong-box">
                     {pickedChamp} ist leider nicht richtig, ein Punkt wurde abgezogen
                </div>
                
            </div>}
        
        <br/>
        <h2>Deine Antwort</h2>
        <input
            type="text"
            placeholder="Suche..."
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)} // Update the search query state
        />
        <GridDisplayAnswer
            data={currentlySelectedChampNames.filter(name => name.toLowerCase().includes(searchQuery.toLowerCase()))}
            correctChampionName={allChampsData[correctChampId].id}
            addPoints={addPoints} subtractPoints={subtractPoints} updatePickedChamp={setPickedChamp}/>
        <h3 id="target-heading">Spielregeln</h3>
        <ul>
            <li>Finde den gesuchten Champion und wähle ihn mit Doppelclick aus</li>
            <li>Richtig gewählt: + 10P</li>
            <li>Falsch gewählt: - 1P</li>
            <li>Man kann Hinweise mit Punkten Kaufen (Klick auf Überschrift)</li>
        </ul>
    </div>

    } else {
        return <div>
            <h1 style={{textAlign: 'center'}}>Champion Quiz</h1>
            <div className="info-container">
                <div className="name-box">
                    <p>{localStorage.getItem("username")}</p>
                </div>
                <div className="points-box">
                    <p>Punkte: {points}</p>
                </div>
            </div>
            <div className="success-container">
                <div className="success-box">
                    🎉 Glückwunsch! Du hast {points} Punkte erreicht!
                </div>
                <div className="proceed-box" onClick={handleNextGameClick}>
                    Nochmal Spielen
                </div>
            </div>
            <div>
                <Scoreboard />
            </div>
        </div>
    }
};

export default ChampQuizMainPage;
