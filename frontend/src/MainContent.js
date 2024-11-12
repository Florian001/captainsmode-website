import React from 'react';
import { Routes, Route } from 'react-router-dom';
import OverviewPage from "./OverviewPage";
import DetailsPage from "./gameDetailsPage/DetailsPage";
import StatsMainPage from "./stats/StatsMainPage";
import GoldenChampMainPage from "./goldenChamp/GoldenChampMainPage";
import SettingsMainPage from "./settings/SettingsMainPage";
import StartPage from "./StartPage";


const MainContent = () => {
    return (
        <div className="main-content">
            <Routes>
                <Route path="/" element={<OverviewPage />} />
                <Route path="/stats" element={<StatsMainPage />} />
                <Route path="/golden-champ" element={<GoldenChampMainPage />} />
                <Route path="/settings" element={<SettingsMainPage />} />
                <Route exact path="/overview" element={<OverviewPage />}/>
                <Route path="/details/:id" element={<DetailsPage />}/>
            </Routes>
        </div>
    );
};

export default MainContent;
