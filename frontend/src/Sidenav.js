import React, { useState } from "react";
import { Link } from "react-router-dom";
import "./Sidenav.css";
import { appVersion } from "./GlobalContext"; // Add your CSS styling here

const Sidenav = () => {
    const [isChampionQuizOpen, setChampionQuizOpen] = useState(false);

    const handleLogoutClick = () => {
        // Clear localStorage and redirect
        console.log("Test");
        localStorage.removeItem("username");
        localStorage.removeItem("password");
        window.location.href = "/overview";
    };

    const openChampionQuizMenu = () => {
        setChampionQuizOpen(true);
    };

    const closeChampionQuizMenu = () => {
        setChampionQuizOpen(false);
    };

    return (
        <div className="sidenav">
            <div className="small-news-banner">
                <div className="small-news-text">
                    🚨Bei dem Weihnachtsquiz 2025 hat Obleute Gaming Fan Katja mit sagenhaften 57 Punkten den dritten
                    Platz erreichen können!🚨
                    Save the date: Die Obleute Gaming Weihnachtsfeier 2026 findet am 19.12.2026 statt!
                </div>
            </div>
            <div>
                <h4>{localStorage.getItem("username")}</h4>
            </div>
            <div className="menu">
                <Link to="/">Übersicht</Link>
                <Link to="/stats">Statistiken</Link>
                <Link to="/golden-champ">Goldener Champion</Link>
                <div className="menu">
                    <div className="menu" onMouseEnter={openChampionQuizMenu} onClick={closeChampionQuizMenu}>
                        Champion Quiz {isChampionQuizOpen ? "▲" : "▼"}
                    </div>
                    {isChampionQuizOpen && (
                        <div className="submenu-items">
                            <Link to="/champ-quiz">Quiz</Link>
                            <Link to="/champ-quiz/scoreboard">Bestenliste</Link>
                        </div>
                    )}
                </div>
                {/*<Link to="/quiz">Weihnachtsquiz 2024</Link>*/}
                <Link to="/settings">Einstellungen</Link>
                <Link to="/patch-notes">Patch Notes</Link>
            </div>
            <div className="app-version">
                v.{appVersion}
                <button onClick={handleLogoutClick}>Logout</button>
            </div>
        </div>
    );
};

export default Sidenav;
