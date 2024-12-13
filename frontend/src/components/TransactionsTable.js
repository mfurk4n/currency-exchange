import React, { useState } from 'react';
import './TransactionsTable.css';
import { useTranslation } from 'react-i18next';

const TransactionsTable = ({ transactions }) => {
    const { t } = useTranslation();
    const [filter, setFilter] = useState('all-times');

    const parseDate = (dateStr) => {
        return new Date(dateStr);
    };

    const filteredTransactions = transactions.filter((transaction) => {
        const transactionDate = parseDate(transaction.createdAt);
        const now = new Date();

        switch (filter) {
            case 'last-day':
                return now - transactionDate < 24 * 60 * 60 * 1000; // Last 24 hours
            case 'last-week':
                return now - transactionDate < 7 * 24 * 60 * 60 * 1000; // Last week
            case 'last-month':
                return now - transactionDate < 30 * 24 * 60 * 60 * 1000; // Last month
            default:
                return true; // All time
        }
    });

    return (
        <div className="transactions-container-out">
            <div className="transactions-header">
                <h1>{t('transactions')}</h1>
                <div className="transactions-container">
                    <div className="filter-bar">
                        <select value={filter} onChange={(e) => setFilter(e.target.value)}>
                            <option value="all-times">{t('allTimes')}</option>
                            <option value="last-day">{t('lastDay')}</option>
                            <option value="last-week">{t('lastWeek')}</option>
                            <option value="last-month">{t('lastMonth')}</option>
                        </select>
                    </div>
                </div>
                <table className="transactions-table">
                    <thead>
                        <tr>
                            <th>{t('customerName')}</th>
                            <th>{t('primaryCurrency')}</th>
                            <th>{t('secondaryCurrency')}</th>
                            <th>{t('primaryQuantity')}</th>
                            <th>{t('secondaryQuantity')}</th>
                            <th>{t('Price')}</th>
                            <th>{t('transactionType')}</th>
                            <th>{t('Status')}</th>
                            <th>{t('Date')}</th>
                        </tr>
                    </thead>
                    <tbody>
                        {filteredTransactions.map((transaction) => (
                            <tr key={transaction.id}>
                                <td>{transaction.customer.name}</td>
                                <td>{transaction.currencyFrom.name} ({transaction.currencyFrom.code})</td>
                                <td>{transaction.currencyTo.name} ({transaction.currencyTo.code})</td>
                                <td>{transaction.amountFrom}</td>
                                <td>{transaction.amountTo}</td>
                                <td>{transaction.currencyPrice}</td>
                                <td className={transaction.transactionType === 'BUY' ? 'type-buy' : 'type-sell'}>
                                    {t(transaction.transactionType)}
                                </td>
                                <td className={transaction.status === 'WAIT' ? 'status-wait' : 'status-complete'}>
                                    {t(transaction.status)}
                                </td>
                                <td>{new Date(transaction.createdAt).toLocaleString()}</td>
                            </tr>
                        ))}
                    </tbody>
                </table>
            </div>
        </div>
    );
};

export default TransactionsTable;
