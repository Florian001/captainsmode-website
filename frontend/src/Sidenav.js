import React from 'react';
import { Link } from 'react-router-dom';
import './Sidenav.css'; // Add your CSS styling here

const Sidenav = () => {
    return (
        <div className="sidenav">
            <Link to="/">Übersicht</Link>
            <Link to="/stats">Statistiken</Link>
            <Link to="/golden-champ">Goldener Champion</Link>
            <Link to="/settings">Einstellungen</Link>
        </div>
    );
};

export default Sidenav;
