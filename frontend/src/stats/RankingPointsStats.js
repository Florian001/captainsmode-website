import React, {useEffect, useState} from 'react';
import {baseUrl} from "../GlobalContext";
import { useMemo } from "react";
import { LineChart, Line, XAxis, YAxis, Tooltip, Legend, CartesianGrid } from "recharts";



const RankingPoints = () => {

    const [players, setPlayers] = useState([]);
    const [stats, setStats] = useState([]);
    const [stats10, setStats10] = useState([]);
  
    
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
            console.log("STARTING FETCHING RANKING POINTS");
            
            try {
                const response = await fetch(baseUrl + `api/v1/games/stats/ranking-points`, {
                    method: "get",
                    headers: new Headers({
                        "ngrok-skip-browser-warning": true,
                        "Authorization": "Basic " + btoa(localStorage.getItem("username") + ":" + localStorage.getItem("password"))
                    }),
                });
                if (response.status === 400) {
                    console.log("Fetching data results in Bad Request");
                    alert("Bad Request sent");
                } else {
                    const data = await response.json();
                    setStats(data);
                }
            } catch (error) {
                console.error('Error fetching row data:', error);
            }
        };

        const fetchRowData10 = async () => {
            console.log("STARTING FETCHING RANKING POINTS");

            try {
                const response = await fetch(baseUrl + `api/v1/games/stats/ranking-points-last-10`, {
                    method: "get",
                    headers: new Headers({
                        "ngrok-skip-browser-warning": true,
                        "Authorization": "Basic " + btoa(localStorage.getItem("username") + ":" + localStorage.getItem("password"))
                    }),
                });
                if (response.status === 400) {
                    console.log("Fetching data last 10 results in Bad Request");
                    alert("Bad Request sent");
                } else {
                    const data = await response.json();
                    setStats10(data);
                }
            } catch (error) {
                console.error('Error fetching row data:', error);
            }
        };
        
        fetchRowData();
        fetchRowData10();
        fetchPlayers();

    }, []);
    
    function useChartData(players, stats) {
        return useMemo(() => {
            const groupedByGame = {};

            stats.forEach(({ player, rankingpoints, number }) => {
                if (!groupedByGame[number]) {
                    groupedByGame[number] = { game: number };
                }
                const playerName = players.find(p => p.id === player)?.name || `P${player}`;
                groupedByGame[number][playerName] = rankingpoints;
            });

            // turn object into sorted array
            return Object.values(groupedByGame).sort((a, b) => a.game - b.game);
        }, [players, stats]);
    }

    const chartData = useChartData(players, stats);
    const chartData10 = useChartData(players, stats10);
    
    return (<div><h3>RankingPoints Gesamt</h3><LineChart width={900} height={500} data={chartData}>
            <CartesianGrid strokeDasharray="3 3"/>
            <XAxis dataKey="game"/>
            <YAxis/>
            <Tooltip/>
            <Legend/>
            {players.map((p, idx) => (
                <Line
                    key={p.id}
                    type="monotone"
                    dataKey={p.name}
                    stroke={["#8884d8", "#82ca9d", "#ff7300", "#00C49F", "#FFBB28", "#FF8042"][idx % 6]}
                    dot={false}
                />
            ))}
        </LineChart>
            <h3>RankingPoints letzte 10 Spiele</h3>
            <LineChart width={900} height={500} data={chartData10}>
                <CartesianGrid strokeDasharray="3 3"/>
                <XAxis dataKey="game"/>
                <YAxis/>
                <Tooltip/>
                <Legend/>
                {players.map((p, idx) => (
                    <Line
                        key={p.id}
                        type="monotone"
                        dataKey={p.name}
                        stroke={["#8884d8", "#82ca9d", "#ff7300", "#00C49F", "#FFBB28", "#FF8042"][idx % 6]}
                        dot={false}
                    />
                ))}
            </LineChart>

        </div>
    );
};

export default RankingPoints;
