import React from 'react';
import GameStat from "./GameStat";
import TableHeader from "./TableHeader";

const GameStatGeneral = ({ participations, players }) => {
    
    return (
        <table className="game-stat-table">
            <TableHeader players={players} participations={participations} />
        <tbody>
            <GameStat statName={"damageSelfMitigated"} displayName="Self-damage" participations={participations} regular={null} exclude={[]}/>
            <GameStat statName={"longestTimeSpentLiving"} displayName="Longest time spent living" participations={participations} regular={true} exclude={[]}/>
            <GameStat statName={"totalTimeSpentDead"} displayName="Time spent dead" participations={participations} regular={false} exclude={[]}/>
            <GameStat statName={"totalTimeCCDealt"} displayName="Time CC dealt" participations={participations} regular={true} exclude={[]}/>
            <GameStat statName={"objectivesStolen"} displayName="Objectives stolen" participations={participations} regular={true} exclude={[]}/>
            <GameStat statName={"saveAllyFromDeath"} displayName="saveAllyFromDeath" participations={participations} regular={true} exclude={[]}/>
            <GameStat statName={"survivedSingleDigitHpCount"} displayName="HP<10 (und überlebt)" participations={participations} regular={true} exclude={[]}/>
            <GameStat statName={"nexusKills"} displayName="Nexus kills" participations={participations} regular={true} exclude={[]} />
            <GameStat statName={"abilityUses"} displayName="Ability uses" participations={participations} regular={true} exclude={[]}/>

            <GameStat statName={"acesBefore15Minutes"} displayName="aces before 15 minutes" participations={participations} regular={null} exclude={[]}/>
            <GameStat statName={"hadOpenNexus"} displayName="hadOpenNexus" participations={participations} regular={null} exclude={[]}/>
            
        </tbody>
        </table>
    );
};

export default GameStatGeneral;
