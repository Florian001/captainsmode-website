import React, { useState } from 'react';
import { Tabs, Tab, Box } from '@mui/material';
import GameStatGeneral from "./GameStatGeneral";
import GameStatCS from "./GameStatCS";
import GameStatDamage from "./GameStatDamage";
import GameStatObjectives from "./GameStatObjectives";
import GameStatVision from "./GameStatVision";
import GameStatKills from "./GameStatKills";
import GameStatFacts from "./GameStatFacts";

const TabSegment = ({ participations, players }) => {
    const [selectedTab, setSelectedTab] = useState(0);

    const handleChange = (event, newValue) => {
        setSelectedTab(newValue);
    };

    return (
        <Box className="box">
            <Tabs value={selectedTab} onChange={handleChange}>
                <Tab label="Allgemein" />
                <Tab label="CS" />
                <Tab label="Damage" />
                <Tab label="Objectives" />
                <Tab label="Vision" />
                <Tab label="Kills" />
                <Tab label="Facts" />
            </Tabs>
            {selectedTab === 0 && <GameStatGeneral participations ={participations} players={players}/>}
            {selectedTab === 1 && <GameStatCS participations ={participations} players={players}/>}
            {selectedTab === 2 && <GameStatDamage participations ={participations} players={players}/>}
            {selectedTab === 3 && <GameStatObjectives participations ={participations} players={players}/>}
            {selectedTab === 4 && <GameStatVision participations ={participations} players={players}/>}
            {selectedTab === 5 && <GameStatKills participations ={participations} players={players}/>}
            {selectedTab === 6 && <GameStatFacts participations ={participations} players={players}/>}
        </Box>
    );
};


export default TabSegment;
