// src/App.js
import React from 'react';
import './App.css';
import { BrowserRouter as Router, Route, Routes } from 'react-router-dom';
import Sidenav from "./Sidenav";
import MainContent from "./MainContent";
import Congratulations from "./quiz/Congratulations";

function App() {
    return (
        <Router>
            <div className="app">
                <Sidenav />
                <MainContent />
            </div>
        </Router>
        
    );
}

export default App;
