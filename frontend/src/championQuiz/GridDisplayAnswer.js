import {dataDragonVersion} from "../GlobalContext";
import champQuizMainPage from "./ChampQuizMainPage";
import {useState} from "react";

const GridDisplayAnswer = ({data, correctChampionName, addPoints, subtractPoints, updatePickedChamp}) => {
    // Split the data into rows, each containing itemsPerRow many entries
    
    const itemsPerRow = 20;
    const rows = []
    
    function handleClick(title) {
        updatePickedChamp(title);
        if (title === correctChampionName) {
            addPoints(10);
        } else {
            subtractPoints(1);
        }
    }
    
    for (let i = 0; i < data.length; i += itemsPerRow) {
        rows.push(<tr>
            {data.slice(i, i+itemsPerRow).map((champion) => (
                <td className={"champ-answer-img"}
                    key={champion.id}><img
                    src={`https://ddragon.leagueoflegends.com/cdn/${dataDragonVersion}/img/champion/${champion}.png`}
                    alt={champion}
                    title={champion}
                    onDoubleClick={() => handleClick(champion)}
                />
                </td>
            ))}
            </tr>
        );
    }
    
    return (<div>
        <table>
            {rows}
        </table>
        </div>
       
)
    ;
};

export default GridDisplayAnswer;