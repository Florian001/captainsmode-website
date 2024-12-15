import React, { useState } from 'react';
import { Tabs, Tab, Box } from '@mui/material';
import GlobalStats from "./GlobalStats";
import DateRangePicker from "./DateRangePicker";

const TabSegment = () => {
    const today = new Date();
    const past = new Date("2023-04-01");
    const [selectedTab, setSelectedTab] = useState(0);
    const [dateRange, setDateRange] = useState({ dateFrom: past, dateTo: today });
    
    const handleChange = (event, newValue) => {
        setSelectedTab(newValue);
    };

    return (
        <div>
            <DateRangePicker onDateChange={setDateRange}/>
        <Box className="box">
            <Tabs value={selectedTab} onChange={handleChange}>
                <Tab label="Allgemein" />
                <Tab label="Captain" />
                <Tab label="KDA" />
                <Tab label="Damage" />
            </Tabs>
            {selectedTab === 0 && <GlobalStats statType={"GENERAL"} dateTo={dateRange.dateTo} dateFrom={dateRange.dateFrom}/>}
            {selectedTab === 1 && <GlobalStats statType={"CAPTAIN"} dateTo={dateRange.dateTo} dateFrom={dateRange.dateFrom}/>}
            {selectedTab === 2 && <GlobalStats statType={"KDA"} dateTo={dateRange.dateTo} dateFrom={dateRange.dateFrom}/>}
        </Box>
        </div>
    );
};


export default TabSegment;
