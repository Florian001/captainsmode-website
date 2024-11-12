import React from 'react';
import WinRow from "./WinRow";

const GameResultOverviewTable = ({ game }) => {
    
    function computeKdaAndGold(game) {
        let killsObleute = 0;
        let deathsObleute = 0;
        let assistsObleute = 0;
        let goldObleute = 0;
        let killsEnemy = 0;
        let deathsEnemy = 0;
        let assistsEnemy = 0;
        let goldEnemy = 0;

        game.participations.forEach(gp => {
            if (gp.player.name === "Gegner") {
                killsEnemy += gp.kills;
                deathsEnemy += gp.deaths;
                assistsEnemy += gp.assists;
                goldEnemy += gp.goldEarned;
            } else {
                killsObleute += gp.kills;
                deathsObleute += gp.deaths;
                assistsObleute += gp.assists;
                goldObleute += gp.goldEarned;
            }
        })
        
        return {
            killsObleute: killsObleute,
            deathsObleute: deathsObleute,
            assistsObleute: assistsObleute,
            goldObleute: goldObleute,
            killsEnemy: killsEnemy,
            deathsEnemy: deathsEnemy,
            assistsEnemy: assistsEnemy,
            goldEnemy: goldEnemy,
        }
    }
    
    const gameData = computeKdaAndGold(game);
    
    return (
        <table className="game-overview-general-table">
            <tbody>
            <tr>
                <WinRow game={game}/>
            </tr>
            <tr>
                <td className="td-game-overview">{"Obleute: "}</td>
                <td className="td-game-overview">{gameData["killsObleute"]}</td>
                <td className="td-game-overview">{gameData["deathsObleute"]}</td>
                <td className="td-game-overview">{gameData["assistsObleute"]}</td>
                <td className="td-game-overview">Gold: {gameData["goldObleute"]}</td>
            </tr>
            <tr>
                <td className="td-game-overview">{"Gegner: "}</td>
                <td className="td-game-overview">{gameData["killsEnemy"]}</td>
                <td className="td-game-overview">{gameData["deathsEnemy"]}</td>
                <td className="td-game-overview">{gameData["assistsEnemy"]}</td>
                <td className="td-game-overview">Gold: {gameData["goldEnemy"]}</td>
            </tr>
            <tr>
                <td colSpan="5">Wir bedanken uns bei unserem Captain {game.captain?.name}!</td>
            </tr>
            </tbody>
        </table>
    );
};

export default GameResultOverviewTable;
