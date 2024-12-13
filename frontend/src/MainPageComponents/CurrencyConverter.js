import React, { useState, useEffect } from 'react';
import './CurrencyConverter.css';
import { useTranslation } from 'react-i18next';

const CurrencyConverter = ({ rates }) => {
  const { t } = useTranslation();
  const [amount, setAmount] = useState(0);
  const [fromCurrency, setFromCurrency] = useState('USD');
  const [toCurrency] = useState('TRY');
  const [result, setResult] = useState(null);

  useEffect(() => {
    const handleConvert = () => {
      const askRate = parseFloat(rates[fromCurrency]) || 1;
      console.log("Ask Rate:", askRate);
      console.log("Amount:", amount);
      const convertedAmount = (amount * askRate);
      console.log("Converted Amount (before rounding):", convertedAmount);
      setResult(`${convertedAmount.toFixed(2)} ${toCurrency}`);
    };


    handleConvert();
  }, [amount, fromCurrency, rates]);

  return (
    <div className="currency-converter">
      <input
        type="number"
        value={amount}
        onChange={(e) => setAmount(e.target.value)}
        className="converter-input"
        placeholder={t("Quantity")}
      />
      <select
        value={fromCurrency}
        onChange={(e) => setFromCurrency(e.target.value)}
        className="converter-select"
      >
        {Object.keys(rates).map((key) => (
          <option key={key} value={key}>
            {key}
          </option>
        ))}
      </select>
      <span>→&nbsp;&nbsp;</span>

      <span className="converter-fixed-currency">TRY</span> { }
      <div className="result">{result}</div>
    </div>
  );
};

export default CurrencyConverter;
