import React from 'react';
import OverviewTable from "./overview/OverviewTable";
import Congratulations from "./quiz/Congratulations";



const OverviewPage = () => {
    return (
        <div className="App">
            <h1>Captainsmode</h1>
            <Congratulations name="John Doe" points={150} />
            <OverviewTable />
        </div>
    );
}

export default OverviewPage;
