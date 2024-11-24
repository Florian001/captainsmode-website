import React from 'react';
import {dataDragonVersion} from "../GlobalContext";

const GameResultTable = ({ gameResultTableData }) => {

    function writeGoldDifference(gameResultTableData, gold1, gold2) {
        if (gold1 > gold2) {
            return <td className="goldAdvantage td-right"> +{gold1 - gold2}</td>;
        } else {
            return <td className="goldDisadvantage td-right"> {gold1 - gold2}</td>;
        }
    }

    return (
        <table className="game-overview-result-table">
            <tr>
                <th></th>
                <th>Spieler</th>
                <th>K</th>
                <th>D</th>
                <th>A</th>
                <th>Gold</th>
                <th>Diff</th>
                <th>Gold</th>
                <th>K</th>
                <th>D</th>
                <th>A</th>
                <th>Spieler</th>
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
                    <td className="td-right td-padding-left">
                        {gameResultTableData[role]["obleute"].gold}
                    </td>
                    {writeGoldDifference(gameResultTableData, gameResultTableData[role]["obleute"]["gold"], gameResultTableData[role]["enemy"]["gold"])}

                    <td className="td-right td-padding-right">
                        {gameResultTableData[role]["enemy"].gold}
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
