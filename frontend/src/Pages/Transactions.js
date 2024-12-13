import React, { useState, useEffect } from 'react';
import NavBar from '../components/NavBar';
import Footer from '../components/Footer';
import TransactionsTable from '../components/TransactionsTable';
import { useNavigate } from 'react-router-dom';

const Transactions = () => {
    const [transactions, setTransactions] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const navigate = useNavigate();
    const [isLoading, setIsLoading] = useState(true);
    const token = localStorage.getItem('jwtToken');
    const selectedCustomer = JSON.parse(localStorage.getItem('selectedCustomer'));
    const customerId = selectedCustomer ? selectedCustomer.id : null;

    useEffect(() => {

        const fetchTransactions = async () => {

            if (!token) {
                console.log('Token bulunamadı');
                setError('Token bulunamadı.');
                setLoading(false);
                navigate('/giris');
                return;
            }

            try {
                let url;
                if (customerId) {
                    url = new URL(`http://localhost:8080/api/v1/transaction/all/customer/${customerId}`);
                    url.searchParams.append('status', 'SUCCESS');
                }
                const response = await fetch(url, {
                    method: "GET",
                    headers: {
                        'Authorization': `Bearer ${token}`,
                    },
                });

                if (!response.ok) {
                    const errorText = await response.text();
                    console.log(`Error ${response.status}: ${errorText}`);
                    setError(`Error ${response.status}: ${errorText}`);
                    return;
                }

                const data = await response.json();
                setTransactions(data);
                setIsLoading(false);
            } catch (error) {
                console.error("Error fetching transactions", error);
                setError("Seçili müşteri bulunamadı");
            } finally {
                setLoading(false);
            }
        };

        fetchTransactions();
    }, [navigate]);

    if (!customerId) {
        return (
            <div>
                <NavBar />
                <div className="assets-container">
                    <p>Seçili müşteri bulunamadı.</p>
                </div>
                <Footer />
            </div>
        );
    }

    return (
        <div>
            <NavBar />
            <div style={{ padding: '25px' }}>
                {loading ? (
                    <div className="loading-container">
                        <div className="loading-spinner"></div>
                    </div>
                ) : error ? (
                    <p>{error}</p>
                ) : (
                    <TransactionsTable transactions={transactions} />
                )}
            </div>
            <Footer />
        </div>
    );

};

export default Transactions;
