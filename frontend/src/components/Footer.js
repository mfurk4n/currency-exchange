import React, { useState, useEffect } from 'react';
import { useTranslation } from 'react-i18next';
import './Footer.css';

const Footer = () => {
  const { t, i18n } = useTranslation();

  const [isDarkMode, setIsDarkMode] = useState(() => {
    return JSON.parse(localStorage.getItem('darkMode')) || false;
  });


  useEffect(() => {
    localStorage.setItem('darkMode', JSON.stringify(isDarkMode));
    document.body.classList.toggle('dark-mode', isDarkMode);
  }, [isDarkMode]);

  const toggleDarkMode = () => {
    setIsDarkMode(!isDarkMode);
    document.body.classList.toggle('dark-mode', !isDarkMode);
  };

  return (
    <footer className="footer">
      <p className="text-center-footer text-muted-footer pb-2 mt-5">
        {t('dev')} <span className="text-primary-footer">Furkan</span> | {t('rights')}
      </p>
      <div className="toggle-label">
        <div className="LightModeText">
          {isDarkMode ? " Dark" : "Light"}
        </div>
        <label className="switch">
          <input type="checkbox" checked={isDarkMode} onChange={toggleDarkMode} />
          <span className="slider"></span>
        </label>
      </div>
    </footer>
  );
};

export default Footer;
