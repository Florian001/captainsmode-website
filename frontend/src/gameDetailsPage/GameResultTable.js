import React, {useState} from 'react';
import {dataDragonVersion} from "../GlobalContext";

const GameResultTable = ({ gameResultTableData }) => {
    
    const [metric, setMetric] = useState("gold");
    
    function writeDifference(gameResultTableData, valueObleute, valueEnemy) {
        if (valueObleute >= valueEnemy) {
            return <td className="goldAdvantage td-center"> +{valueObleute - valueEnemy}</td>;
        } else {
            return <td className="goldDisadvantage td-center"> {valueObleute - valueEnemy}</td>;
        }
    }

    const getDisplayName = (metric) => {
        switch (metric) {
            case "gold": return "Gold";
            case "xp": return "XP";
            case "level": return "Level";
            default: return "Fehler";
        }
    }
    
    const getValue = (metric, role, team) => {
        switch (metric) {
            case "gold": return gameResultTableData[role][team].gold;
            case "xp": return gameResultTableData[role][team].champExperience;
            case "level": return gameResultTableData[role][team].champLevel;
            default: return "Fehler";
        }
    }

    const handleCycle = () => {
        switch (metric) {
            case "gold": setMetric("xp"); break;
            case "xp": setMetric("level"); break;
            case "level": setMetric("gold"); break;
            default: throw new Error("unexpected case");
        }
    };
    
    return (
        <table className="game-overview-result-table">
            <tr>
                <th></th>
                <th>Spieler</th>
                <th>K</th>
                <th>D</th>
                <th>A</th>
                <th style={{
                    minWidth: "7ch", // Minimum width for 5 digits
                    textAlign: "center",
                }}>{getDisplayName(metric)}</th>
                <th style={{
                    minWidth: "7ch", // Minimum width for 5 digits
                    textAlign: "center",
                }}>Diff</th>
                <th style={{
                    minWidth: "7ch", // Minimum width for 5 digits
                    textAlign: "center",
                }}>{getDisplayName(metric)}</th>
                <th>K</th>
                <th>D</th>
                <th>A</th>
                <th>Spieler</th>
                <th>
                    <span
                        style={{
                            cursor: "pointer",
                            userSelect: "none",
                            fontSize: "1.2rem",
                        }}
                        onClick={handleCycle}
                    >
                            ⇿ 
                        </span>
                </th>
            </tr>
            {["top", "jgl", "mid", "adc", "sup"].map((role => {
                return <tr>
                    <td>
                        <img
                            src={`https://ddragon.leagueoflegends.com/cdn/${dataDragonVersion}/img/champion/${gameResultTableData[role]["obleute"].championName}.png`}
                            alt={gameResultTableData[role]["obleute"].championName}
                            className="icon"/>
                    </td>
                    <td className="td-padding-right">
                        {gameResultTableData[role]["obleute"].summonerName}
                    </td>
                    <td className="td-right">
                        {gameResultTableData[role]["obleute"].kills}
                    </td>
                    <td className="td-right">
                        {gameResultTableData[role]["obleute"].deaths}
                    </td>
                    <td className="td-right">
                        {gameResultTableData[role]["obleute"].assists}
                    </td>
                    <td className="td-center td-padding-left">
                        {getValue(metric, role, "obleute")}
                    </td>
                    {writeDifference(gameResultTableData, getValue(metric, role, "obleute"), getValue(metric, role, "enemy"))}

                    <td className="td-center td-padding-right">
                        {getValue(metric, role, "enemy")}
                    </td>
                    <td className="td-right">
                        {gameResultTableData[role]["enemy"].kills}
                    </td>
                    <td className="td-right">
                        {gameResultTableData[role]["enemy"].deaths}
                    </td>
                    <td className="td-right">
                        {gameResultTableData[role]["enemy"].assists}
                    </td>
                    <td className="td-left td-padding-left">
                        {gameResultTableData[role]["enemy"].summonerName}
                    </td>
                    <td>
                        <img
                            src={`https://ddragon.leagueoflegends.com/cdn/${dataDragonVersion}/img/champion/${gameResultTableData[role]["enemy"].championName}.png`}
                            alt={gameResultTableData[role]["enemy"].championName}
                            className="icon"/>
                    </td>
                </tr>
            }))}
        </table>
    );
};

export default GameResultTable;
