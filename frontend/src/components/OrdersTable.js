import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import './OrdersTable.css';
import { useTranslation } from 'react-i18next';

const OrdersTable = () => {
    const { t } = useTranslation();
    const [waitingFilter, setWaitingFilter] = useState('all-times');
    const [cancelledFilter, setCancelledFilter] = useState('all-times');
    const [waitingTransactions, setWaitingTransactions] = useState([]);
    const [cancelledTransactions, setCancelledTransactions] = useState([]);
    const navigate = useNavigate();
    const [isLoading, setIsLoading] = useState(true);

    useEffect(() => {
        const jwtToken = localStorage.getItem('jwtToken');
        const selectedCustomer = JSON.parse(localStorage.getItem('selectedCustomer'));
        const customerId = selectedCustomer ? selectedCustomer.id : null;

        if (!jwtToken) {
            console.log('JWT token not found or is empty');
            navigate('/giris');
            return;
        }

        const fetchOrders = async () => {
            try {
                const response = await fetch(`http://localhost:8080/api/v1/order/${customerId}`, {
                    method: 'GET',
                    headers: {
                        'Authorization': `Bearer ${jwtToken}`,
                        'Content-Type': 'application/json'
                    }
                });

                if (response.status === 401) {
                    console.log('Unauthorized. Redirecting to login...');
                    navigate('/giris');
                    return;
                }

                if (!response.ok) {
                    throw new Error('API isteği başarısız oldu.');
                }

                const data = await response.json();
                setWaitingTransactions(data.waiting);
                setCancelledTransactions(data.cancelled);
                setIsLoading(false);
            } catch (error) {
                console.error("Veri çekilirken hata oluştu:", error);
            }
        };

        fetchOrders();
    }, [navigate]);

    const handleButtonClick = async (orderId) => {
        try {
            const response = await fetch(`http://localhost:8080/api/v1/order/cancel/${orderId}`, {
                method: 'GET',
                headers: {
                    'Authorization': `Bearer ${localStorage.getItem('jwtToken')}`,
                    'Content-Type': 'application/json'
                }
            });

            if (!response.ok) {
                throw new Error('API request failed.');
            }

            if (response.status === 200) {
                console.log("Order cancelled successfully.");
                window.location.reload();
            }

            const data = await response.json();
            console.log("Data fetched:", data);
        } catch (error) {
            console.error("Error during API request:", error);
        }
    };

    const filterTransactions = (transactions, filter) => {
        const now = new Date();
        return transactions.filter((transaction) => {
            const transactionDate = new Date(transaction.createdAt.split(' ')[0]);

            switch (filter) {
                case 'last-day':
                    return now - transactionDate < 24 * 60 * 60 * 1000;
                case 'last-week':
                    return now - transactionDate < 7 * 24 * 60 * 60 * 1000;
                case 'last-month':
                    return now - transactionDate < 30 * 24 * 60 * 60 * 1000;
                case 'last-6-months':
                    return now - transactionDate < 6 * 30 * 24 * 60 * 60 * 1000;
                case 'last-year':
                    return now - transactionDate < 365 * 24 * 60 * 60 * 1000;
                default:
                    return true;
            }
        });
    };

    return (
        <>
            {isLoading ? (
                <div className="loading-container">
                    <div className="loading-spinner"></div>
                </div>
            ) : (
                <>
                    <div className="transactions-container-out">
                        <div className="transactions-header">
                            <h1>{t('bekleyenemir')}</h1>
                            <div className="transactions-container">
                                <div className="filter-bar">
                                    <select value={waitingFilter} onChange={(e) => setWaitingFilter(e.target.value)}>
                                        <option value="all-times">{t('allTimes')}</option>
                                        <option value="last-day">{t('lastDay')}</option>
                                        <option value="last-week">{t('lastWeek')}</option>
                                        <option value="last-month">{t('lastMonth')}</option>
                                        <option value="last-6-months">{t('lastSixMonth')}</option>
                                        <option value="last-year">{t('lastYear')}</option>
                                    </select>
                                </div>
                            </div>
                            {filterTransactions(waitingTransactions, waitingFilter).length > 0 ? (
                                <table className="transactions-table">
                                    <thead className='transactions-text-color'>
                                        <tr>
                                            <th>{t('customerInfo')}</th>
                                            <th>{t('transactionType')}</th>
                                            <th>{t('currencyType')}</th>
                                            <th>{t('amount2')}</th>
                                            <th>{t('price')}</th>
                                            <th>{t('reserve')}</th>
                                            <th>{t('date')}</th>
                                            <th>{ }</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        {filterTransactions(waitingTransactions, waitingFilter).map((transaction, index) => (
                                            <tr key={transaction.id}>
                                                {index === 0 && (
                                                    <td rowSpan={filterTransactions(waitingTransactions, waitingFilter).length}>
                                                        {`${transaction.customer.name} (${transaction.customer.user.email})`}
                                                    </td>
                                                )}
                                                <td className={transaction.orderType ? 'type-satis' : 'type-alis'}>
                                                    {transaction.orderType ? 'Limit' : 'Stop'}
                                                </td>
                                                <td>{`${transaction.baseCurrency.code}/${transaction.targetCurrency.code}`}</td>
                                                <td>{transaction.amount}</td>
                                                <td>{transaction.expectedPrice}</td>
                                                <td>{transaction.blockedBalance}</td>
                                                <td>{transaction.createdAt.split(' ')[0]}</td>
                                                <td>
                                                    <button
                                                        onClick={() => handleButtonClick(transaction.id)}
                                                        className="custom-button-cancel">
                                                        {t('Cancell')}
                                                    </button>
                                                </td>
                                            </tr>
                                        ))}
                                    </tbody>
                                </table>
                            ) : (
                                <div className="no-transactions">{t('NoPendingOrders')}</div>
                            )}
                        </div>
                    </div>

                    <div className="transactions-container-out">
                        <div className="transactions-header">
                            <h1>{t('iptalemir')}</h1>
                            <div className="transactions-container">
                                <div className="filter-bar">
                                    <select value={cancelledFilter} onChange={(e) => setCancelledFilter(e.target.value)}>
                                        <option value="all-times">{t('allTimes')}</option>
                                        <option value="last-day">{t('lastDay')}</option>
                                        <option value="last-week">{t('lastWeek')}</option>
                                        <option value="last-month">{t('lastMonth')}</option>
                                        <option value="last-6-months">{t('lastSixMonth')}</option>
                                        <option value="last-year">{t('lastYear')}</option>
                                    </select>
                                </div>
                            </div>
                            {filterTransactions(cancelledTransactions, cancelledFilter).length > 0 ? (
                                <table className="transactions-table">
                                    <thead className='transactions-text-color'>
                                        <tr>
                                            <th>{t('customerInfo')}</th>
                                            <th>{t('transactionType')}</th>
                                            <th>{t('currencyType')}</th>
                                            <th>{t('amount2')}</th>
                                            <th>{t('price')}</th>
                                            <th>{t('reserve')}</th>
                                            <th>{t('date')}</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        {filterTransactions(cancelledTransactions, cancelledFilter).map((transaction, index) => (
                                            <tr key={transaction.id}>
                                                {index === 0 && (
                                                    <td rowSpan={filterTransactions(cancelledTransactions, cancelledFilter).length}>
                                                        {`${transaction.customer.name} (${transaction.customer.user.email})`}
                                                    </td>
                                                )}
                                                <td className={transaction.orderType ? 'type-satis' : 'type-alis'}>
                                                    {transaction.orderType ? 'Limit' : 'Stop'}
                                                </td>
                                                <td>{`${transaction.baseCurrency.code}/${transaction.targetCurrency.code}`}</td>
                                                <td>{transaction.amount}</td>
                                                <td>{transaction.expectedPrice}</td>
                                                <td>{transaction.blockedBalance}</td>
                                                <td>{transaction.createdAt.split(' ')[0]}</td>
                                            </tr>
                                        ))}
                                    </tbody>
                                </table>
                            ) : (
                                <div className="no-transactions">{t('NoCancelledOrders')}</div>
                            )}
                        </div>
                    </div>
                </>
            )}
        </>
    );
};

export default OrdersTable;
