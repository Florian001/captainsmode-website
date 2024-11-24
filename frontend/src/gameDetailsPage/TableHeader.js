import React from 'react';
import {dataDragonVersion} from "../GlobalContext";

const TableHeader = ({ players, participations }) => {
    const playersInParticipations = participations.filter(item => item !== null).map(item => item.player.name);
    
    return (
        <thead>
        <tr>
            <th className="game-stat-th-player"></th>
            {players.filter(p => playersInParticipations.includes(p.name)).map((player, index) => (
                <th key={player.id} className="th-game-stat">
                    <img src={`/${participations[index].role}.png`}
                         alt={participations[index].role}
                         className="th-game-stat-icon-pos"/>
                    {player.name}
                    <img
                        src={`https://ddragon.leagueoflegends.com/cdn/${dataDragonVersion}/img/champion/${participations[index].championName}.png`}
                        alt={participations[index].championName}
                        className="th-game-stat-icon-champ"/>
                </th>
            ))}
        </tr>
        </thead>
    );
};

export default TableHeader;
