import React from 'react';
import "./ActionButtons.css"

const ActionButtons = () => {
    return (
        <div className="action-buttons">
            <button className="button" style={{ background: 'green', color: 'white', margin: '10px' }}>Yatır</button>
            <button className="button" style={{ background: 'red', color: 'white', margin: '10px' }}>Çek</button>
        </div>
    );
};

export default ActionButtons;
