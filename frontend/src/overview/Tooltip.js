// src/Tooltip.js
import React, { useState } from 'react';

const Tooltip = ({ children, text }) => {
    const [isVisible, setIsVisible] = useState(false);

    return (
        <div
            className="tooltip-container"
            onMouseEnter={() => setIsVisible(true)}
            onMouseLeave={() => setIsVisible(false)}
        >
            {!isVisible && children}
            {isVisible && (
                <div className="tooltip-box">
                    {text}
                </div>
            )}
        </div>
    );
};

export default Tooltip;
