import React, { useState } from 'react';
import { Tabs, Tab, Box } from '@mui/material';
import GlobalStats from "./GlobalStats";

const TabSegment = () => {
    const [selectedTab, setSelectedTab] = useState(0);

    const handleChange = (event, newValue) => {
        setSelectedTab(newValue);
    };

    return (
        <Box className="box">
            <Tabs value={selectedTab} onChange={handleChange}>
                <Tab label="Allgemein" />
                <Tab label="Captain" />
                <Tab label="KDA" />
                <Tab label="Damage" />
            </Tabs>
            {selectedTab === 0 && <GlobalStats statType={"GENERAL"}/>}
            {selectedTab === 1 && <GlobalStats statType={"CAPTAIN"}/>}
            {selectedTab === 2 && <GlobalStats statType={"KDA"}/>}
        </Box>
    );
};


export default TabSegment;
