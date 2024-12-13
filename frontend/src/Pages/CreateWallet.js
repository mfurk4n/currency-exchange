import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import NavBar from '../components/NavBar';
import Footer from '../components/Footer';
import './CreateWallet.css';
import { useTranslation } from 'react-i18next';

const CreateWallet = () => {
  const { t } = useTranslation();
  const [newWallet, setNewWallet] = useState('');
  const [availableWallets, setAvailableWallets] = useState([]);
  const [showSuccessMessage, setShowSuccessMessage] = useState(false);
  const [errorMessage, setErrorMessage] = useState('');
  const navigate = useNavigate();

  const selectedCustomer = JSON.parse(localStorage.getItem('selectedCustomer'));
  const customerId = selectedCustomer ? selectedCustomer.id : null;

  useEffect(() => {

    const fetchAvailableCurrencies = async () => {
      const jwtToken = localStorage.getItem('jwtToken');
      if (!jwtToken) {
        setErrorMessage('JWT token bulunamadı.');
        navigate('/giris');
        return;
      }

      try {
        const response = await fetch(`http://localhost:8080/api/v1/wallet/available-currencies/${customerId}`, {
          method: 'GET',
          headers: {
            'Authorization': `Bearer ${jwtToken}`,
            'Content-Type': 'application/json',
          },
        });

        if (!response.ok) {
          throw new Error('Mevcut para birimleri alınamadı.');
        }

        const data = await response.json();
        setAvailableWallets(data);
      } catch (error) {
        console.error('Error fetching available currencies:', error);
        setErrorMessage('Mevcut para birimleri alınırken bir hata oluştu.');
      }
    };

    fetchAvailableCurrencies();
  }, [customerId]);

  const handleCreateWallet = async () => {
    if (!newWallet) {
      setErrorMessage(t('selectCurrency'));
      setTimeout(() => setErrorMessage(''), 2000);
      return;
    }

    const jwtToken = localStorage.getItem('jwtToken');
    if (!jwtToken) {
      setErrorMessage('JWT token bulunamadı.');
      setTimeout(() => setErrorMessage(''), 2000);
      return;
    }

    try {
      const response = await fetch('http://localhost:8080/api/v1/wallet/create', {
        method: 'POST',
        headers: {
          'Authorization': `Bearer ${jwtToken}`,
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          customerId: customerId,
          currencyId: newWallet,
        }),
      });

      if (!response.ok) {
        const errorData = await response.json();
        if (errorData.message.includes('Bu kura ait cüzdan zaten mevcut')) {
          setErrorMessage('Bu para birimine ait bir cüzdan zaten mevcut.');
        } else {
          setErrorMessage('Cüzdan oluşturulamadı: ' + errorData.message);
        }
        setTimeout(() => setErrorMessage(''), 2000);
        return;
      }

      setShowSuccessMessage(true);

      setTimeout(() => {
        navigate('/Assets');
      }, 1000);

    } catch (error) {
      console.error('Error creating wallet:', error);
      setErrorMessage('Cüzdan oluşturulamadı. Lütfen tekrar deneyin.');
      setTimeout(() => setErrorMessage(''), 2000);
    }
  };

  const handleGoBack = () => {
    navigate('/Assets');
  };

  return (
    <div className='create-wallet'>
      <NavBar />
      <div className='create-wallet-blok'>
        <div className='main-content'>
          { }
          <div className='create-wallet'>
            <div className="button-container-back-wallet">
              <button className="back-button-wallet" onClick={handleGoBack}>{t("Back")}</button>
            </div>
            <h2>{t("createWallet")}</h2>
            <div className='create-wallet-form'>
              <select
                value={newWallet}
                onChange={(e) => setNewWallet(e.target.value)}
              >
                <option value='' disabled>{t("Select")}...</option>
                {availableWallets.map((wallet, index) => (
                  <option key={index} value={wallet.currencyId}>{wallet.currencyName}</option>
                ))}
              </select>
              <button onClick={handleCreateWallet}>{t("Create")}</button>
            </div>
          </div>

          { }
          {showSuccessMessage && (
            <div className='success-message'>
              <h3>{t("walletSuccessfullyCreated")}</h3>
              <p>{t("redirectedToTheAssetsPage")}...</p>
            </div>
          )}

          { }
          {errorMessage && (
            <div className='error-message'>
              <h3>Hata:</h3>
              <p>{errorMessage}</p>
            </div>
          )}
        </div>
      </div>

      <Footer />
    </div>
  );
};

export default CreateWallet;
