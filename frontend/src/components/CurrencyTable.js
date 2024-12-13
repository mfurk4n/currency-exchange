import React from 'react';

const CurrencyTable = ({ currencies }) => {
  return (
    <table>
      <thead>
        <tr>
          <th>Name</th>
          <th>Symbol</th>
          <th>Change</th>
          <th>Price</th>
        </tr>
      </thead>
      <tbody>
        {currencies.map((currency, index) => (
          <tr key={index}>
            <td>{currency.name}</td>
            <td>{currency.code}</td>
            <td>{currency.change}</td>
            <td>{currency.price}</td>
          </tr>
        ))}
      </tbody>
    </table>
  );
};

export default CurrencyTable;
