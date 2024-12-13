import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import NavBar from '../components/NavBar';
import Footer from '../components/Footer';
import './AddBalance.css';
import { useTranslation } from 'react-i18next';

const AddBalance = () => {
  const { t } = useTranslation();
  const [amount, setAmount] = useState('');
  const [selectedWallet, setSelectedWallet] = useState('');
  const [wallets, setWallets] = useState([]);
  const [showSuccessMessage, setShowSuccessMessage] = useState(false);
  const [errorMessage, setErrorMessage] = useState('');
  const navigate = useNavigate();

  const fetchWallets = async () => {
    const jwtToken = localStorage.getItem('jwtToken');
    const selectedCustomer = JSON.parse(localStorage.getItem('selectedCustomer'));
    const customerId = selectedCustomer ? selectedCustomer.id : null;

    if (!jwtToken) {
      console.log('JWT token not found or is empty');
      navigate('/giris');
      return;
    }

    if (!customerId) {
      console.log('Customer ID not found');
      setErrorMessage('Müşteri bilgisi bulunamadı.');
      return;
    }

    try {
      const response = await fetch(`http://localhost:8080/api/v1/wallet/all/${customerId}`, {
        method: 'GET',
        headers: {
          'Authorization': `Bearer ${jwtToken}`,
          'Content-Type': 'application/json',
        },
      });

      if (!response.ok) {
        throw new Error('Cüzdan verileri alınamadı.');
      }

      const data = await response.json();
      setWallets(data);
    } catch (error) {
      console.error('Error fetching wallets:', error);
      setErrorMessage('Cüzdan verileri alınırken bir hata oluştu.');
    }
  };

  useEffect(() => {
    fetchWallets();
  }, []);

  const loadBalance = async () => {
    if (!selectedWallet) {
      setErrorMessage(t('selectWallet'));
      return;
    }

    if (!amount || parseFloat(amount) <= 0) {
      setErrorMessage(t('negativeValuesAreNotAccepted'));
      return;
    }

    const jwtToken = localStorage.getItem('jwtToken');
    if (!jwtToken) {
      setErrorMessage('JWT token bulunamadı.');
      navigate('/giris');
      return;
    }

    const selectedCustomer = JSON.parse(localStorage.getItem('selectedCustomer'));
    const customerId = selectedCustomer ? selectedCustomer.id : null;

    if (!customerId) {
      setErrorMessage('Müşteri bilgisi bulunamadı.');
      return;
    }

    try {
      // URL'i oluşturuyoruz ve query parametrelerini ekliyoruz
      const url = new URL(`http://localhost:8080/api/v1/wallet/balance/${selectedWallet}`);
      url.searchParams.append('newAmount', amount);
      url.searchParams.append('customerId', customerId);

      const response = await fetch(url, {
        method: 'PUT',
        headers: {
          'Authorization': `Bearer ${jwtToken}`,
          'Content-Type': 'application/json',
        }
      });

      if (!response.ok) {
        throw new Error('Bakiye yükleme başarısız.');
      }

      setShowSuccessMessage(true);
      setTimeout(() => {
        navigate('/Assets');
      }, 1000); // 1 saniye sonra yönlendirme

    } catch (error) {
      console.error('Error loading balance:', error);
      setErrorMessage('Bakiye yüklenirken bir hata oluştu.');
    }
  };

  const handleGoBack = () => {
    navigate('/Assets');
  };

  return (
    <div className="add-balance-page-container">
      <NavBar />
      <div className="add-balance-content-wrapper">
        <div className="add-balance-form-container">
          <div className="button-container-balance">
            <button className="back-button-container-balance" onClick={handleGoBack}>{t("Back")}</button>
          </div>
          <h3>{t("loadBalance")}</h3>
          <div className="add-balance-form-group">
            <label htmlFor="wallet">{t("selectWalet")}:</label>
            <select
              id="wallet"
              value={selectedWallet}
              onChange={(e) => setSelectedWallet(e.target.value)}
            >
              <option value="">{t("selectWalet")}</option>
              {wallets.map((wallet) => (
                <option key={wallet.id} value={wallet.id}>
                  {wallet.currency.name} ({wallet.currency.code})
                </option>
              ))}
            </select>
          </div>
          <div className="add-balance-form-group">
            <label htmlFor="amount">{t("Quantity")}:</label>
            <input
              type="number"
              id="amount"
              value={amount}
              onChange={(e) => setAmount(e.target.value)}
              min="0"
            />
          </div>
          <button className="add-balance-button" onClick={loadBalance}>{t("Load")}</button>

          {/* Başarı messageı */}
          {showSuccessMessage && (
            <div className='success-message'>
              <h3>{t("balanceSuccessfullyIdentified")}</h3>
              <p>{t("redirectedToTheAssetsPage")}...</p>
            </div>
          )}

          {/* Hata messageı */}
          {errorMessage && (
            <div className='error-message'>
              <h3>{t("mistake")}:</h3>
              <p>{errorMessage}</p>
            </div>
          )}

          { }

        </div>
      </div>
      <Footer />
    </div>
  );
};

export default AddBalance;
