import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import './Sidebar.css';
import '../Pages/Profile.css';
import accountIcon from '../Images/account-icon.png';
import passwordIcon from '../Images/password-icon.png';
import logoutIcon from '../Images/logout-icon.png';
import ConfirmationModal from '../Modal/ConfirmationModal';
import { useTranslation } from 'react-i18next';

const Sidebar = () => {
  const { t } = useTranslation();
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [profileData, setProfileData] = useState({});
  const navigate = useNavigate();

  useEffect(() => {
    const fetchData = async () => {
      const jwtToken = localStorage.getItem('jwtToken');
      if (!jwtToken) {
        console.log('JWT token not found or is empty');
        navigate('/giris');
        return;
      }

      try {
        const response = await fetch('http://localhost:8080/api/v1/user/get-user', {
          method: 'GET',
          headers: {
            'Authorization': `Bearer ${jwtToken}`,
            'Content-Type': 'application/json',
          },
        });

        if (!response.ok) {
          throw new Error('Kullanıcı bilgileri alınamadı');
        }

        const data = await response.json();
        setProfileData(data);
      } catch (error) {
        console.error('Error fetching data:', error);
      }
    };

    fetchData();
  }, [navigate]);

  const handleLogoutClick = (e) => {
    e.preventDefault();
    setIsModalOpen(true);
    document.body.classList.add('modal-open');
  };

  const handleConfirmLogout = () => {
    localStorage.clear();
    setIsModalOpen(false);
    document.body.classList.remove('modal-open');
    console.log("Oturum kapatıldı.");
    navigate('/giris');
  };

  const handleCancelLogout = () => {
    setIsModalOpen(false);
    document.body.classList.remove('modal-open');
  };

  const handleProfileClick = (e) => {
    e.preventDefault();
    navigate('/profilim');
  };

  const handlePasswordChangeClick = (e) => {
    e.preventDefault();
    navigate('/profilim/sifre-degistirme');
  };

  return (
    <div className="sidebar">
      <div className="sidebar-header">
        <h3>{profileData.admin ? 'Admin' : 'User'}</h3>
        <span>{profileData.name}</span>
      </div>
      <ul className="sidebar-menu">
        <li>
          <a href="#" onClick={handleProfileClick}>
            <img src={accountIcon} alt={t("accountInformation")} className="menu-icon" />{t("accountInformation")}
          </a>
        </li>
        <li>
          <a href="#" onClick={handlePasswordChangeClick}>
            <img src={passwordIcon} alt={t("changePassword")} className="menu-icon" /> {t("changePassword")}
          </a>
        </li>
        <li>
          <a href="#" onClick={handleLogoutClick}>
            <img src={logoutIcon} alt={t("safeExit")} className="menu-icon" /> {t("safeExit")}
          </a>
        </li>
      </ul>
      <ConfirmationModal
        isOpen={isModalOpen}
        onConfirm={handleConfirmLogout}
        onCancel={handleCancelLogout}
      />
    </div>
  );
};

export default Sidebar;
