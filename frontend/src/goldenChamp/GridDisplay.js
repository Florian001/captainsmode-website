import {dataDragonVersion} from "../GlobalContext";

const GridDisplay = ({ data, all, open }) => {
    // Split the data into rows, each containing 10 entries
    const itemsPerRow = 15;
    const rows = [];
    
    for (let i = 0; i < data.length; i += itemsPerRow) {
        rows.push(<tr>
            {data.slice(i, i+itemsPerRow).map((champion) => (
                <td key={champion.id}><img
                    src={`https://ddragon.leagueoflegends.com/cdn/${dataDragonVersion}/img/champion/${champion.id}.png`}
                    alt={champion.name}
                    title={champion.name}
                    className={champion.isAvailable ? "iconGoldenChampAvailable" : "iconGoldenChampNotAvailable"}/>
                </td>
            ))}
            </tr>
        );
    }
    
    return (<div>
            <h2>Liste der möglichen goldenen Champions</h2>
            <p>Noch potentiell goldene Champions: {open} / {all} ({(open*100/all).toFixed(2)} %) </p>
        <table className={"goldenChampOverviewTable"}>
            {rows}
        </table>
        </div>
       
)
    ;
};

export default GridDisplay;