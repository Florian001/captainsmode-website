import React from 'react';
import GameStat from "./GameStat";
import TableHeader from "./TableHeader";

const GameStatGeneral = ({ participations, players }) => {
    
    return (
        <table className="game-stat-table">
            <TableHeader players={players} participations={participations} />
        <tbody>
            <GameStat statName={"visionScore"} displayName="Visionscore" participations={participations} regular={true} exclude={[]}/>
            <GameStat statName={"detectorWardsPlaced"} displayName="Detector wards placed" participations={participations} regular={true} exclude={[]}/>
            <GameStat statName={"wardsPlaced"} displayName="Wards placed" participations={participations} regular={true} exclude={[]}/>
            <GameStat statName={"wardsKilled"} displayName="Wards killed" participations={participations} regular={true} exclude={[]}/>
            
        </tbody>
        </table>
    );
};

export default GameStatGeneral;
