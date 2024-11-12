import React from 'react';
import GameStat from "./GameStat";
import TableHeader from "./TableHeader";

const GameStatGeneral = ({ participations, players }) => {
    
    return (
        <table className="game-stat-table">
            <TableHeader players={players} participations={participations} />
        <tbody>
            <GameStat statName={"doubleKills"} displayName="Doublekills" participations={participations} regular={true} exclude={[]}/>
            <GameStat statName={"tripleKills"} displayName="Triplekills" participations={participations} regular={true} exclude={[]}/>
            <GameStat statName={"quadraKills"} displayName="Quadrakills" participations={participations} regular={true} exclude={[]}/>
            <GameStat statName={"pentaKills"} displayName="Pentakills" participations={participations} regular={true} exclude={[]}/>
            <GameStat statName={"firstBloodKill"} displayName="First blood kill" participations={participations} regular={true} exclude={[]}/>
            <GameStat statName={"firstBloodAssist"} displayName="First blood assist" participations={participations} regular={null} exclude={[]}/>
            <GameStat statName={"firstTowerKill"} displayName="First tower kill" participations={participations} regular={true} exclude={[]}/>
            <GameStat statName={"firstTowerAssist"} displayName="First tower assist" participations={participations} regular={null} exclude={[]}/>
            <GameStat statName={"killParticipation"} displayName="killParticipation" participations={participations} regular={true} exclude={[]}/>
            <GameStat statName={"soloKills"} displayName="Solokills" participations={participations} regular={true} exclude={[]}/>
            <GameStat statName={"killsUnderOwnTurret"} displayName="killsUnderOwnTurret" participations={participations} regular={true} exclude={[]}/>
        </tbody>
        </table>
    );
};

export default GameStatGeneral;
