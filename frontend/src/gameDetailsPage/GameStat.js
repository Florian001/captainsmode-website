import React from 'react';

const GameStat = ({ statName, displayName, participations, regular, exclude}) => {
    
    let max = -20000;
    let min = 20000;
    
    function computeMinAndMax() {
        if (regular === null) return;
        
        participations.forEach((participation) => {
            if ((participation?.player.name !== "Gegner") && (!exclude.includes(participation["role"]))){
                if (participation[statName] > max) {
                    max = participation[statName]
                }
                if (participation[statName] < min) {
                    min = participation[statName]
                }
            }
        })
    }

    computeMinAndMax();
    return (
        <tr key={statName} id={`tr-stat`}>
            <td className="td-game-stat-display-name">
                {displayName}
            </td>
            {participations.map((participation, contentIndex) => (
                participation?.player.name !== "Gegner" &&
                <td key={contentIndex} className={participation[statName] === max ? (regular ? 'td-stat-best' : 'td-stat-worst') : participation[statName] === min ? (regular ? 'td-stat-worst' : 'td-stat-best') : 'td-stat'}>
                    {participation !== null &&
                        participation[statName]}
                </td>
            ))}
        </tr>
    );
};

export default GameStat;
