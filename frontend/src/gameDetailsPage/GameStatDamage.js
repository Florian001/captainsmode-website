import React from 'react';
import GameStat from "./GameStat";
import TableHeader from "./TableHeader";

const GameStatGeneral = ({ participations, players }) => {
    
    return (
        <table className="game-stat-table">
            <TableHeader players={players} participations={participations} />
        <tbody>
            <GameStat statName={"totalDamageDealtToChampions"} displayName="Damage dealt to champs" participations={participations} regular={true} exclude={[]}/>
            <GameStat statName={"physicalDamageDealtToChampions"} displayName="    -> physical" participations={participations} regular={true} exclude={[]}/>
            <GameStat statName={"magicDamageDealtToChampions"} displayName="    -> magic" participations={participations} regular={true} exclude={[]}/>
            <GameStat statName={"trueDamageDealtToChampions"} displayName="    -> true" participations={participations} regular={true} exclude={[]}/>
            <GameStat statName={"teamDamagePercentage"} displayName="    -> Team %" participations={participations} regular={true} exclude={[]}/>
            <GameStat statName={"totalDamageTaken"} displayName="Damage taken" participations={participations} regular={true} exclude={[]}/>
            <GameStat statName={"physicalDamageTaken"} displayName="    -> physical" participations={participations} regular={true} exclude={[]}/>
            <GameStat statName={"magicDamageTaken"} displayName="    -> magic" participations={participations} regular={true} exclude={[]}/>
            <GameStat statName={"trueDamageTaken"} displayName="    -> true" participations={participations} regular={true} exclude={[]}/>
            <GameStat statName={"damageTakenOnTeamPercentage"} displayName="    -> Team %" participations={participations} regular={true} exclude={[]}/>
            <GameStat statName={"totalDamageShieldedOnTeammates"} displayName="Damage shielded on teammates" participations={participations} regular={true} exclude={[]}/>
            <GameStat statName={"totalHeal"} displayName="Total heal" participations={participations} regular={true} exclude={[]}/>
            <GameStat statName={"totalHealsOnTeammates"} displayName="Heals on teammates" participations={participations} regular={true} exclude={[]}/>
            
        </tbody>
        </table>
    );
};

export default GameStatGeneral;
