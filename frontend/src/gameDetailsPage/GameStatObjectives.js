import React from 'react';
import GameStat from "./GameStat";
import TableHeader from "./TableHeader";

const GameStatGeneral = ({ participations, players }) => {
    
    return (
        <table className="game-stat-table">
            <TableHeader players={players} participations={participations} />
        <tbody>
            <GameStat statName={"damageDealtToObjectives"} displayName="Damage to objectives" participations={participations} regular={true} exclude={[]}/>
            <GameStat statName={"damageDealtToBuildings"} displayName="Damage to buildings" participations={participations} regular={true} exclude={[]}/>
            <GameStat statName={"damageDealtToTurrets"} displayName="Damage to turrets" participations={participations} regular={true} exclude={[]}/>
            <GameStat statName={"turretKills"} displayName="Turrets killed" participations={participations} regular={true} exclude={[]}/>
            <GameStat statName={"dragonKills"} displayName="Dragon kills" participations={participations} regular={true} exclude={[]}/>
            <GameStat statName={"baronKills"} displayName="Baron kills" participations={participations} regular={true} exclude={[]}/>
            
        </tbody>
        </table>
    );
};

export default GameStatGeneral;
