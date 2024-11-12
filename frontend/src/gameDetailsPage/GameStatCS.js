import React from 'react';
import GameStat from "./GameStat";
import TableHeader from "./TableHeader";

const GameStatGeneral = ({ participations, players }) => {
    
    return (
        <table className="game-stat-table">
            <TableHeader players={players} participations={participations} />
        <tbody>
            <GameStat statName={"totalMinionsKilled"} displayName="Minions killed" participations={participations} regular={true} exclude={['SUP', 'JGL']}/>
            <GameStat statName={"laneMinionsFirst10Minutes"} displayName="Minions killed first 10 minutes" participations={participations} regular={true} exclude={['SUP', 'JGL']}/>
            <GameStat statName={"maxCsAdvantageOnLaneOpponent"} displayName="max CS advantage on lane opponent" participations={participations} regular={true} exclude={['SUP', 'JGL']}/>
            {/*neutralMinionsKilled = mNeutralMinionsKilled, which is incremented on kills of kPet and kJungleMonster*/}
            <GameStat statName={"neutralMinionsKilled"} displayName="Neutral minions killed" participations={participations} regular={null} exclude={[]}/>
            <GameStat statName={"totalAllyJungleMinionsKilled"} displayName="Ally jungleminions killed" participations={participations} regular={null} exclude={[]}/>
            <GameStat statName={"totalEnemyJungleMinionsKilled"} displayName="Enemy jungleminions killed" participations={participations} regular={null} exclude={[]}/>
        </tbody>
        </table>
    );
};

export default GameStatGeneral;
