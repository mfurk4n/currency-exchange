import React from 'react';
import './MessageBox.css';

const MessageBox = ({ message, onClose }) => {
  return (
    <div className="message-box">
      <div className="message-box-content">
        <span className="message">{message}</span>
        <button className="close-button" onClick={onClose}>X</button>
      </div>
    </div>
  );
};

export default MessageBox;
