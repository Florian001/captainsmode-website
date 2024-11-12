import React, {useEffect, useState} from 'react';

function EditableText({ initialText, initalGame, onSave }) {
    const [text, setText] = useState(initialText);
    const [isEditing, setIsEditing] = useState(false);
    
    const handleTextClick = () => {
        setIsEditing(true);
    };

    const handleInputChange = (event) => {
        setText(event.target.value);
    };

    const handleKeyDown = (event) => {
        if (event.key === 'Enter') {
            setIsEditing(false);
            onSave(text);
        }
    };

    const handleBlur = () => {
        setIsEditing(false);
        onSave(text);
    };

    return (
        <div className="game-description">
            {isEditing ? (
                <textarea
                    type="text"
                    value={text}
                    onChange={handleInputChange}
                    onKeyDown={handleKeyDown}
                    onBlur={handleBlur}
                    autoFocus
                    className="game-description-input"
                />
            ) : (
                <span onClick={handleTextClick}>{text}</span>
            )}
        </div>
    );
}

export default EditableText;
