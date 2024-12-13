import React from 'react';
import './CurrencyTable.css';

const CurrencyTable = ({ currencies }) => {
  return (
    <table className="currency-table">
      <thead>
        <tr>
          <th>Döviz Cinsi</th>
          <th>Sembol</th>
          <th>1 Haftalık Değişim</th>
          <th>Güncel Fiyat</th>
        </tr>
      </thead>
      <tbody>
        {currencies.map((currency) => (
          <tr key={currency.symbol}>
            <td>{currency.name}</td>
            <td>{currency.symbol}</td>
            <td>{currency.change}</td>
            <td>{currency.price}</td>
          </tr>
        ))}
      </tbody>
    </table>
  );
};

export default CurrencyTable;
