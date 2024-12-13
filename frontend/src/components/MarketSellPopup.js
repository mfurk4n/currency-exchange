import React, { useState } from 'react';
import './MarketSellPopup.css';
import 'flag-icon-css/css/flag-icons.min.css';
import flagCode from '../flag-code.json';

const MarketSellPopup = ({ ask, bid, baseCurrency, targetCurrency, onClose }) => {
  const [isBuying, setIsBuying] = useState(true);
  const [amountBase, setAmountBase] = useState('');
  const [amountTarget, setAmountTarget] = useState('');
  const [isBaseDisabled, setIsBaseDisabled] = useState(false);
  const [isTargetDisabled, setIsTargetDisabled] = useState(false);
  const [errorMessage, setErrorMessage] = useState(null);


  const displayedExchangeRate = isBuying
    ? `1 ${baseCurrency.code} = ${ask} ${targetCurrency.code}`
    : `1 ${baseCurrency.code} = ${bid} ${targetCurrency.code}`;

  const getFlagIconClass = (currencyCode) => {
    return `flag-icon flag-icon-${flagCode[currencyCode]}`;
  };

  const handleBaseAmountChange = (e) => {
    const value = e.target.value.replace(/\D/, '');
    setAmountBase(value);
    if (value !== '') {
      setIsTargetDisabled(true);
      const calculatedTargetAmount = isBuying ? value * ask : value * bid;
      setAmountTarget(calculatedTargetAmount.toFixed(2));
    } else {
      setIsTargetDisabled(false);
      setAmountTarget('');
    }
  };

  const handleTargetAmountChange = (e) => {
    const value = e.target.value.replace(/\D/, '');
    setAmountTarget(value);
    if (value !== '') {
      setIsBaseDisabled(true);
      const calculatedBaseAmount = isBuying ? value / ask : value / bid;
      setAmountBase(calculatedBaseAmount.toFixed(2));
    } else {
      setIsBaseDisabled(false);
      setAmountBase('');
    }
  };

  const handleIsleButtonClick = async () => {
    const jwtToken = localStorage.getItem('jwtToken');
    const selectedCustomer = JSON.parse(localStorage.getItem('selectedCustomer'));
    const customerId = selectedCustomer ? selectedCustomer.id : null;
    const requestBody = {
      customerId: customerId,
      baseCurrency: baseCurrency.code,
      targetCurrency: targetCurrency.code,
      amount: parseFloat(amountBase),
      expectedPrice: 1,
      transactionType: 0,
      orderType: !isBuying
    };

    try {
      const response = await fetch('http://localhost:8080/api/v1/exchange/order', {
        method: 'POST',
        headers: {
          'Authorization': `Bearer ${jwtToken}`,
          'Content-Type': 'application/json'
        },
        body: JSON.stringify(requestBody)
      });

      if (response.status === 200) {
        onClose();
        window.location.reload();
      } else if (response.status === 400 || response.status === 404) {
        const errorData = await response.json();
        setErrorMessage(errorData.message || 'İstek başarısız oldu.');
      } else {
        throw new Error('İstek başarısız oldu.');
      }

    } catch (error) {
      console.error("İstek gönderilirken hata oluştu:", error);
      setErrorMessage('İstek gönderilirken bir hata oluştu.');
    }
  };

  return (
    <div className="popup-overlay">
      <div className="popup-container">
        <div className="popup-header">
          <div
            className={`popup-header-item ${isBuying ? 'selected' : ''}`}
            onClick={() => {
              setIsBuying(true);
              setAmountBase('');
              setAmountTarget('');
              setIsBaseDisabled(false);
              setIsTargetDisabled(false);
              setErrorMessage(null);
            }}
          >
            {baseCurrency.code} Al
          </div>
          <div
            className={`popup-header-item ${!isBuying ? 'selected' : ''}`}
            onClick={() => {
              setIsBuying(false);
              setAmountBase('');
              setAmountTarget('');
              setIsBaseDisabled(false);
              setIsTargetDisabled(false);
              setErrorMessage(null);
            }}
          >
            {baseCurrency.code} Sat
          </div>
        </div>
        <div className="popup-content">
          <div className="exchange-rate-popup">
            <span>Döviz Kuru</span>
            <span>{displayedExchangeRate}</span>
          </div>
          {isBuying ? (
            <>
              <div className="currency-input">
                <i className={getFlagIconClass(baseCurrency.code)}></i>
                <span>Alınacak {baseCurrency.code}</span>
                <input
                  type="text"
                  value={amountBase}
                  onChange={handleBaseAmountChange}
                  className="amount-input"
                  disabled={isBaseDisabled}
                />
              </div>
              <div className="currency-input">
                <i className={getFlagIconClass(targetCurrency.code)}></i>
                <span>Satılacak {targetCurrency.code}</span>
                <input
                  type="text"
                  value={amountTarget}
                  onChange={handleTargetAmountChange}
                  className="amount-input"
                  disabled={isTargetDisabled}
                />
              </div>
            </>
          ) : (
            <>
              <div className="currency-input">
                <i className={getFlagIconClass(baseCurrency.code)}></i>
                <span>Satılacak {baseCurrency.code}</span>
                <input
                  type="text"
                  value={amountBase}
                  onChange={handleBaseAmountChange}
                  className="amount-input"
                  disabled={isBaseDisabled}
                />
              </div>
              <div className="currency-input">
                <i className={getFlagIconClass(targetCurrency.code)}></i>
                <span>Alınacak {targetCurrency.code}</span>
                <input
                  type="text"
                  value={amountTarget}
                  onChange={handleTargetAmountChange}
                  className="amount-input"
                  disabled={isTargetDisabled}
                />
              </div>
            </>
          )}
          {errorMessage && <div className="error-message">{errorMessage}</div>} { }
        </div>
        <button className="close-button" onClick={handleIsleButtonClick}>İşle</button>
        <button className="close-button" onClick={onClose}>Kapat</button>
      </div>
    </div>
  );
};

export default MarketSellPopup;
