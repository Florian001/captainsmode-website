import React, {useEffect, useState} from 'react';
import {baseUrl, dataDragonVersion} from "../GlobalContext";
import WinRow from "../gameDetailsPage/WinRow";
import GridDisplay from "./GridDisplay";


const GoldenChampMainPage = () => {
    
    const [goldenChampData, setGoldenChampData] = useState([]);
    const [goldenChampOpenNumber, setGoldenChampOpenNumber] = useState(0);
    const [goldenChampAllNumber, setGoldenChampAllNumber] = useState(0);
    const [goldenChampOverview, setGoldenChampOverview] = useState([]);
    const [loading, setLoading] = useState(true);
    
    
    useEffect(() => {
        // Fetch players data
        const fetchData = async () => {
            try {
                setLoading(true);
                const allChampResponse = await fetch(`https://ddragon.leagueoflegends.com/cdn/${dataDragonVersion}/data/en_US/champion.json`, {
                    method: "get",
                });
           
                const champData = await allChampResponse.json();
                const allChampsData = Object.values(champData.data);
                
                const wrongChampsResponse = await fetch(baseUrl + `api/v1/golden-champ/wrong-champs`, {
                        method: "get",
                        headers: new Headers({
                            "Content-Type" : "application/json",
                            "Authorization": "Basic " + btoa(localStorage.getItem("username") + ":" + localStorage.getItem("password"))
                        }),
                    });
                const wrongChamps = await wrongChampsResponse.json();
                const allChampNames = allChampsData.map(d => d.id);
                
                setGoldenChampAllNumber(allChampNames.length);
                setGoldenChampOpenNumber(allChampNames.length-wrongChamps.length);
                
                const goldenChampData = allChampNames.map(id => {
                    return {
                        id: id,
                        isAvailable: !wrongChamps.includes(id),
                    }
                });
                setGoldenChampData(goldenChampData);

                const overviewResponse = await fetch(baseUrl + `api/v1/golden-champ/overview`, {
                    method: "get",
                    headers: new Headers({
                        "Content-Type" : "application/json",
                        "Authorization": "Basic " + btoa(localStorage.getItem("username") + ":" + localStorage.getItem("password"))
                    }),
                });
                const overview = await overviewResponse.json();
                console.log("overview");
                console.log(overview);
                console.log(overview.overviews);
                setGoldenChampOverview(overview.overviews);
                
            } catch (error) {
                console.error('Error fetching players:', error);
            } finally {
                setLoading(false);
            }
        };

        fetchData();
    }, []);

    
    return <div>
        <h1>Golden Champ</h1>
        {loading && "Lade..."}
        {goldenChampOverview.length > 0 && <h2>Historie der gefundenen goldenen Champions</h2>}
        <ul>
            {goldenChampOverview.map((go) => {
                return <li>Gratulation an {go.player}! Es ist ihm als ehrenwerter Captain in Spiel {go.matchFound} gelungen, den goldenen Champion {go.champ} zu vergeben. Wahnsinn!</li>
            })}
        </ul>
        <GridDisplay data={goldenChampData} all={goldenChampAllNumber} open={goldenChampOpenNumber}/>
    </div>
};

export default GoldenChampMainPage;
