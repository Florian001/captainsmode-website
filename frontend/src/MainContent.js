import React from 'react';
import { Routes, Route } from 'react-router-dom';
import OverviewPage from "./OverviewPage";
import DetailsPage from "./gameDetailsPage/DetailsPage";
import StatsMainPage from "./stats/StatsMainPage";
import GoldenChampMainPage from "./goldenChamp/GoldenChampMainPage";
import SettingsMainPage from "./settings/SettingsMainPage";
import StartPage from "./StartPage";
import InsertAnswer from "./quiz/InsertAnswer";
import InsertQuestion from "./quiz/InsertQuestion";
import QuizDisplay from "./quiz/QuizDisplay";
import Quiz from "./quiz/Quiz";


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
                <Route path="/quiz/answers" element={<InsertAnswer />}/>
                <Route path="/quiz/insert-question" element={<InsertQuestion />}/>
                <Route path="/quiz" element={<Quiz />}/>
            </Routes>
        </div>
    );
};

export default MainContent;
