import React, { useState, useEffect } from 'react';
import NavBar from '../components/NavBar';
import Footer from '../components/Footer';
import Sidebar from '../components/Sidebar';
import './PasswordChange.css';
import '@fortawesome/fontawesome-free/css/all.min.css';
import { useNavigate } from 'react-router-dom';
import { useTranslation } from 'react-i18next';

const PasswordChange = () => {
  const { t } = useTranslation();
  const [currentPassword, setCurrentPassword] = useState('');
  const [newPassword, setNewPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const [errorMessage, setErrorMessage] = useState('');
  const [successMessage, setSuccessMessage] = useState('');
  const [userId, setUserId] = useState('');

  const [showCurrentPassword, setShowCurrentPassword] = useState(false);
  const [showNewPassword, setShowNewPassword] = useState(false);
  const [showConfirmPassword, setShowConfirmPassword] = useState(false);

  const navigate = useNavigate();

  const jwtToken = localStorage.getItem('jwtToken');

  useEffect(() => {
    if (!jwtToken) {
      console.log('JWT token not found or is empty');
      navigate('/giris');
      return;
    }

    const fetchUserData = async () => {
      try {
        const response = await fetch('http://localhost:8080/api/v1/user/get-user', {
          method: 'GET',
          headers: {
            'Authorization': `Bearer ${jwtToken}`,
            'Content-Type': 'application/json'
          },
        });

        if (!response.ok) {
          throw new Error('Kullanıcı bilgileri alınamadı');
        }

        const data = await response.json();
        setUserId(data.id);
      } catch (error) {
        console.error('Kullanıcı bilgisi alınırken hata:', error);
        setErrorMessage('Kullanıcı bilgisi alınırken bir hata oluştu.');
        setTimeout(() => setErrorMessage(''), 2000);
      }
    };

    fetchUserData();
  }, [jwtToken, navigate]);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setErrorMessage('');
    setSuccessMessage('');

    if (newPassword !== confirmPassword) {
      setErrorMessage(t('newPasswordsDontMatch'));
      setTimeout(() => setErrorMessage(''), 2000);
      return;
    }

    try {
      const response = await fetch(`http://localhost:8080/api/v1/user/${userId}`, {
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${jwtToken}`,
        },
        body: JSON.stringify({
          currentPassword: currentPassword,
          newPassword: newPassword,
        }),
      });

      if (response.ok) {
        setSuccessMessage(t('passwordChangedSuccessfully'));
        setCurrentPassword('');
        setNewPassword('');
        setConfirmPassword('');

        setTimeout(() => setSuccessMessage(''), 2000);
      } else {
        const responseData = await response.json();
        if (response.status === 400) {
          setErrorMessage(responseData.message || 'Geçersiz istek. Lütfen bilgilerinizi kontrol edin.');
        } else {
          setErrorMessage(t('passwordChangeFailed'));
        }
        setTimeout(() => setErrorMessage(''), 2000);
      }
    } catch (error) {
      console.error('Şifre değiştirme hatası:', error);
      setErrorMessage('Bir hata oluştu. Lütfen tekrar deneyin.');
      setTimeout(() => setErrorMessage(''), 2000);
    }
  };

  return (
    <div className='password-changee'>
      <NavBar />
      <div className='main-cont'>
        <Sidebar />
        <div className='content-wrapper-sifre'>

          <div className='password-change'>
            <h2>{t("changePassword")}</h2>
            {successMessage && <p className='success-message'>{successMessage}</p>}
            {errorMessage && <p className='error-message'>{errorMessage}</p>}
            <form onSubmit={handleSubmit}>
              <div className='form-group'>
                <label htmlFor='currentPassword'>{t("oldPassword")}</label>
                <input
                  type={showCurrentPassword ? 'text' : 'password'}
                  id='currentPassword'
                  value={currentPassword}
                  onChange={(e) => setCurrentPassword(e.target.value)}
                  required
                />
                <i
                  className={`toggle-password fas ${showCurrentPassword ? 'fa-eye-slash' : 'fa-eye'}`}
                  onClick={() => setShowCurrentPassword(!showCurrentPassword)}
                ></i>
              </div>
              <div className='form-group'>
                <label htmlFor='newPassword'>{t("newPassword")}</label>
                <input
                  type={showNewPassword ? 'text' : 'password'}
                  id='newPassword'
                  value={newPassword}
                  onChange={(e) => setNewPassword(e.target.value)}
                  required
                />
                <i
                  className={`toggle-password fas ${showNewPassword ? 'fa-eye-slash' : 'fa-eye'}`}
                  onClick={() => setShowNewPassword(!showNewPassword)}
                ></i>
              </div>
              <div className='form-group'>
                <label htmlFor='confirmPassword'>{t("newPasswordAgain")}</label>
                <input
                  type={showConfirmPassword ? 'text' : 'password'}
                  id='confirmPassword'
                  value={confirmPassword}
                  onChange={(e) => setConfirmPassword(e.target.value)}
                  required
                />
                <i
                  className={`toggle-password fas ${showConfirmPassword ? 'fa-eye-slash' : 'fa-eye'}`}
                  onClick={() => setShowConfirmPassword(!showConfirmPassword)}
                ></i>
              </div>
              <button type='submit'>{t("changePassword")}</button>
            </form>
          </div>
        </div>
      </div>
      <Footer />
    </div>
  );
};

export default PasswordChange;
