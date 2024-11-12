import React from 'react';
import GameStat from "./GameStat";
import TableHeader from "./TableHeader";

const GameStatGeneral = ({ participations, players }) => {
    
    
    return (
        <table className="game-stat-table">
            <TableHeader players={players} participations={participations} />
    <tbody>
        <GameStat statName={"championName"} displayName="Champion" participations={participations} regular={null} exclude={[]}/>
        <GameStat statName={"ccv"} displayName="ccv-Punkte" participations={participations} regular={true} exclude={[]}/>
        <GameStat statName={"kills"} displayName="Kills" participations={participations} regular={true} exclude={[]}/>
        <GameStat statName={"deaths"} displayName="Deaths" participations={participations} regular={false} exclude={[]}/>
        <GameStat statName={"assists"} displayName="Assists" participations={participations} regular={true} exclude={[]}/>
        <GameStat statName={"goldEarned"} displayName="Gold" participations={participations} regular={true} exclude={[]}/>
        <GameStat statName={"champExperience"} displayName="XP" participations={participations} regular={true} exclude={[]}/>
        <GameStat statName={"champLevel"} displayName="Level" participations={participations} regular={true} exclude={[]}/>
        </tbody>
        </table>
    );
};

export default GameStatGeneral;
