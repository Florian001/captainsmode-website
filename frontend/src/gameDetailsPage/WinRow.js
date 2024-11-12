import React from 'react';

const WinRow = ({ game }) => {
    
    if (game.win) {
        return <tr>
            <td colSpan="5" className="td-game-overview-win-or-loss"><span
                className="span-game-overview-win">Sieg</span> in {game.duration} min</td>
            
        </tr>
    } else {
        return <tr>
            <td colSpan="5"><span className="span-game-overview-loss">Niederlage</span> in {game.duration}</td>
        </tr>
    }

};

export default WinRow;
