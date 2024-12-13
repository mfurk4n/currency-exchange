import React from 'react';
import PropTypes from 'prop-types';

const CurrencySelector = ({ selectedCurrency, onCurrencyChange, currencies = [] }) => {

  if (!currencies.length) {
    return <div>No currencies available</div>;
  }

  return (
    <select value={selectedCurrency} onChange={(e) => onCurrencyChange(e.target.value)}>
      {currencies.map((currency) => (
        <option key={currency} value={currency}>
          {currency}
        </option>
      ))}
    </select>
  );
};

CurrencySelector.propTypes = {
  selectedCurrency: PropTypes.string.isRequired,
  onCurrencyChange: PropTypes.func.isRequired,
  currencies: PropTypes.arrayOf(PropTypes.string)
};

export default CurrencySelector;
