import React, { useState, useEffect } from 'react';
import NavBar from '../components/NavBar';
import Footer from '../components/Footer';
import Sidebar from '../components/Sidebar';
import { useNavigate } from 'react-router-dom';
import './Profile.css';
import { useTranslation } from 'react-i18next';

const Profile = () => {
  const { t } = useTranslation();
  const [profileData, setProfileData] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [successMessage, setSuccessMessage] = useState('');
  const [errorMessage, setErrorMessage] = useState('');
  const [isLoading, setIsLoading] = useState(true);

  const navigate = useNavigate();

  useEffect(() => {
    const jwtToken = localStorage.getItem('jwtToken');

    if (!jwtToken) {
      console.log('JWT token not found or is empty');
      navigate('/giris');
      return;
    }

    const fetchData = async () => {
      try {
        const response = await fetch('http://localhost:8080/api/v1/user/get-user', {
          method: 'GET',
          headers: {
            'Authorization': `Bearer ${jwtToken}`,
            'Content-Type': 'application/json'
          },
          credentials: 'include',
        });

        if (!response.ok) {
          throw new Error(`HTTP error! status: ${response.status}`);
        }

        const data = await response.json();
        console.log('Gelen Veri:', data);
        setProfileData(data);
        setIsLoading(false);
      } catch (error) {
        console.error('Veri çekilirken hata oluştu:', error);
        setErrorMessage('Veri çekilirken bir hata oluştu.');
        setError(error.message);
      } finally {
        setLoading(false);
      }
    };

    fetchData();
  }, [navigate]);

  const dismissNotification = () => {
    setSuccessMessage('');
    setErrorMessage('');
  };

  return (
    <div className='profile'>
      <NavBar />

      {isLoading ? (
        <div className="loading-container">
          <div className="loading-spinner"></div>
        </div>
      ) : (
        <div className='main'>
          <Sidebar />
          <div className='content-wrapper-profile'>
            <div className='profile-contents'>
              <div className='profile-info'>
                <h2>{t("nameSurname")}</h2>
                <p>{error ? `Hata: ${error}` : profileData?.name}</p>

                <h2>{t("accountID")}</h2>
                <p>{error ? `Hata: ${error}` : profileData?.id}</p>

                <h2>{t("email")}</h2>
                <p>{error ? `Hata: ${error}` : profileData?.email}</p>
              </div>
            </div>
          </div>
        </div>
      )}

      <Footer />

      {successMessage && (
        <div className="notification success">
          {successMessage}
          <button className="dismiss-button-profile" onClick={dismissNotification}>X</button>
        </div>
      )}
      {errorMessage && (
        <div className="notification error">
          {errorMessage}
          <button className="dismiss-button-profile" onClick={dismissNotification}>X</button>
        </div>
      )}
    </div>
  );

};

export default Profile;
