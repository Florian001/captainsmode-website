import React from 'react';

function ReloadButton() {
    // Function to reload the page
    const reloadPage = () => {
        window.location.reload();
    };

    return (
        <button onClick={reloadPage} style={{ padding: '5px', fontSize: '14px' }}>
            🔄 Reload
        </button>
    );
}

export default ReloadButton;
