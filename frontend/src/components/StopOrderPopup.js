import React, { useState, useEffect } from 'react';
import './StopOrderPopup.css';
import 'flag-icon-css/css/flag-icons.min.css';
import flagCode from '../flag-code.json';


const StopOrderPopup = ({ bid, baseCurrency, targetCurrency, onClose }) => {
  const [stopPrice, setStopPrice] = useState('');
  const [amount, setAmount] = useState('1');
  const [totalCost, setTotalCost] = useState(0);
  const [errorMessage, setErrorMessage] = useState('');
  const [amountWarning, setAmountWarning] = useState('');
  const [showSubmitButton, setShowSubmitButton] = useState(false);

  useEffect(() => {
    const calculatedCost = parseFloat(amount);
    setTotalCost(calculatedCost.toFixed(2));

    if (stopPrice && parseFloat(stopPrice) > 0 && parseFloat(stopPrice) <= parseFloat(bid)) {
      setShowSubmitButton(true);
    } else {
      setShowSubmitButton(false);
    }
  }, [stopPrice, amount]);

  const getFlagIconClass = (currencyCode) => {
    return `flag-icon flag-icon-${flagCode[currencyCode]}`;
  };

  const handleStopPriceChange = (e) => {
    const value = e.target.value;

    if (value === '' || /^\d*\.?\d*$/.test(value)) {
      setStopPrice(value);

      const calculatedCost = parseFloat(amount);
      setTotalCost(calculatedCost.toFixed(2));

      if (parseFloat(value) > parseFloat(bid)) {
        setErrorMessage(`Stop fiyatı piyasa fiyatından yüksek olamaz. Piyasa Fiyatı: ${bid} ${targetCurrency.code}`);
        setShowSubmitButton(false);
      } else {
        setErrorMessage('');
        if (parseFloat(value) > 0) {
          setShowSubmitButton(true);
        } else {
          setShowSubmitButton(false);
        }
      }
    }
  };

  const handleAmountChange = (e) => {
    const value = e.target.value.replace(/\D/, '');
    setAmount(value);

    if (parseFloat(value) < 1) {
      setAmountWarning('Miktar 1\'den düşük olamaz.');
      setShowSubmitButton(false);
    } else {
      setAmountWarning('');
      if (parseFloat(stopPrice) > 0) {
        setShowSubmitButton(true);
      }
    }

    const calculatedCost = parseFloat(value);
    setTotalCost(calculatedCost.toFixed(2));
  };

  const handleSubmit = async () => {
    if (showSubmitButton) {
      const jwtToken = localStorage.getItem('jwtToken');
      const selectedCustomer = JSON.parse(localStorage.getItem('selectedCustomer'));
      const customerId = selectedCustomer ? selectedCustomer.id : null;
      const requestBody = {
        customerId: customerId,
        baseCurrency: baseCurrency.code,
        targetCurrency: targetCurrency.code,
        amount: parseFloat(amount),
        expectedPrice: parseFloat(stopPrice),
        transactionType: 2,
        orderType: true
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
          window.location.href = '/islemlerim/emirlerim';
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
    }
  };

  return (
    <div className="popup-overlay">
      <div className="popup-container">
        <div className="popup-header">
          <div className="popup-header-item selected">
            {baseCurrency.code} Stop Emir
          </div>
        </div>
        <div className="popup-content">
          <div className="exchange-rate-popup">
            <span>Piyasa Fiyatı</span>
            <span>1 {baseCurrency.code} = {bid} {targetCurrency.code}</span>
          </div>
          <div className="currency-input">
            <i className={getFlagIconClass(targetCurrency.code)}></i>
            <span>Stop Fiyat ({targetCurrency.code})</span>
            <input
              type="text"
              value={stopPrice}
              onChange={handleStopPriceChange}
              className="amount-input"
            />
          </div>
          <div className="currency-input">
            <i className={getFlagIconClass(baseCurrency.code)}></i>
            <span>Miktar ({baseCurrency.code})</span>
            <input
              type="text"
              value={amount}
              onChange={handleAmountChange}
              className="amount-input"
            />
          </div>
          {amountWarning && <div className="error-message">{amountWarning}</div>}
          <div className="currency-input">
            <i className={getFlagIconClass(baseCurrency.code)}></i>
            <span>Rezerve Tutar</span>
            <span className="reserve-amount">
              {totalCost} {baseCurrency.code}
            </span>
          </div>
          {errorMessage && <div className="error-message">{errorMessage}</div>}
        </div>
        {showSubmitButton && (
          <button className="close-button" onClick={handleSubmit}>Emri Gir</button>
        )}
        <button className="close-button" onClick={onClose}>Kapat</button>
      </div>
    </div>
  );
};

export default StopOrderPopup;
