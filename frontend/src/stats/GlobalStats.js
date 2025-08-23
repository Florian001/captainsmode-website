import React, {useEffect, useState} from 'react';
import {baseUrl} from "../GlobalContext";
import MyPieChart from "../util/MyPieChart";
import MyBarChart from "../util/MyBarChart";




const GlobalStats = ({statType, dateFrom, dateTo}) => {

    const [players, setPlayers] = useState([]);
    const [stats, setStats] = useState([]);
    const [selectedRowIndex, setSelectedRowIndex] = useState(null);
    const [selectedRowData, setSelectedRowData] = useState(null);
    
    const handleRowClick = (index) => {
        setSelectedRowIndex(index);
    };

    useEffect(() => {
        if (selectedRowIndex !== null) {
            setSelectedRowData( [
                { name: stats[selectedRowIndex].statsOfPlayers[0]?.playerName, value : stats[selectedRowIndex].statsOfPlayers[0]?.value},
                { name: stats[selectedRowIndex].statsOfPlayers[1]?.playerName, value : stats[selectedRowIndex].statsOfPlayers[1]?.value},
                { name: stats[selectedRowIndex].statsOfPlayers[2]?.playerName, value : stats[selectedRowIndex].statsOfPlayers[2]?.value},
                { name: stats[selectedRowIndex].statsOfPlayers[3]?.playerName, value : stats[selectedRowIndex].statsOfPlayers[3]?.value},
                { name: stats[selectedRowIndex].statsOfPlayers[4]?.playerName, value : stats[selectedRowIndex].statsOfPlayers[4]?.value},
                { name: stats[selectedRowIndex].statsOfPlayers[5]?.playerName, value : stats[selectedRowIndex].statsOfPlayers[5]?.value},
                // { name: stats[selectedRowIndex].statsOfPlayers[6]?.playerName, value : stats[selectedRowIndex].statsOfPlayers[6]?.value},
            ])
        }
    }, [selectedRowIndex, stats]);
    
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
            }
        };

        // Fetch row data
        const fetchRowData = async () => {
            console.log("dateFrom: " + dateFrom + " dateTo: " + dateTo + " statType: " + statType);
            if (!dateFrom || !dateTo) return;
            try {
                const response = await fetch(baseUrl + `api/v1/games/stats/${statType}?dateFrom=${dateFrom.toISOString().split("T")[0]}&dateTo=${dateTo.toISOString().split("T")[0]}`, {
                    method: "get",
                    headers: new Headers({
                        "ngrok-skip-browser-warning": true,
                        "Authorization": "Basic " + btoa(localStorage.getItem("username") + ":" + localStorage.getItem("password"))
                    }),
                });
                if (response.status === 400) {
                    console.log("Fetching data results in Bad Request");
                    alert("Datumsanzeige ist quatsch");
                } else {
                    const data = await response.json();
                    setStats(data);
                }
            } catch (error) {
                console.error('Error fetching row data:', error);
            }
        };
        
        fetchRowData(dateFrom, dateTo);
        fetchPlayers();

    }, [dateFrom, dateTo]);



    return (<div>
    <table className="stat-table-number-of-games">
        <thead>
        <tr>
            <th></th>
            {players.map(player => (
                <th key={player.id} id="td-header">{player.name}</th>
            ))}
        </tr>
        </thead>
        <tbody>
        {stats.map((stat, statIndex) => (
            <tr key={statIndex} 
                className={selectedRowIndex === statIndex ? `selected` : ``} 
                onClick={() => handleRowClick(statIndex)}>
                <td className={`bla`}>
                    {stat.displayName}
                </td>
                {stat.statsOfPlayers.map((statOfPlayer, contentIndex) => {
                    const playerNameIndex = players.findIndex(player => player.name === statOfPlayer.playerName);
                    let i = 0;
                    console.log("playerNameIndex");
                console.log(playerNameIndex);
                    // Initialize an array to hold <td> elements
                    const cells = [];

                    // Add empty <td> elements for missing players
                    while (contentIndex + i < playerNameIndex) {
                        cells.push(<td key={`empty-${contentIndex}`}></td>);
                        console.log("empty");
                        i++; // Increment to move to the correct index
                    }

                    // Add the actual <td> element for the statOfPlayer
                    cells.push(
                        <td key={contentIndex}
                            className={statOfPlayer.best ? "td-global-stat-best" : statOfPlayer.worst ? "td-global-stat-worst" : ""}>
                            {statOfPlayer.value} {stat.percentValue ? " %" : ""}
                        </td>
                    );

                    return cells;
                })}

            </tr>
        ))}
        </tbody>
    </table>
        {selectedRowData !== null && <MyPieChart data={selectedRowData} />}
            { selectedRowData !== null && <MyBarChart data={selectedRowData} />}
        </div>
    );
};

export default GlobalStats;
