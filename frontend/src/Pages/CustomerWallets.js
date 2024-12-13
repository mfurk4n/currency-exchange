import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import NavBar from '../components/NavBar';
import Footer from '../components/Footer';
import './CustomerWallets.css';
import { useTranslation } from 'react-i18next'; 

const CustomerWallets = () => {
  const { t } = useTranslation();
  const [wallets, setWallets] = useState([]);
  const [isAdmin, setIsAdmin] = useState(false);
  const [searchTerm, setSearchTerm] = useState('');
  const navigate = useNavigate();

  useEffect(() => {
    const fetchData = async () => {
      const jwtToken = localStorage.getItem('jwtToken');
      if (!jwtToken) {
        console.log('JWT token not found.');
        navigate('/giris');
        return;
      }

      try {
        const userResponse = await fetch('http://localhost:8080/api/v1/user/get-user', {
          method: 'GET',
          headers: {
            'Authorization': `Bearer ${jwtToken}`,
            'Content-Type': 'application/json'
          },
          credentials: 'include',
        });

        if (!userResponse.ok) {
          throw new Error('Kullanıcı verileri alınamadı.');
        }

        const userData = await userResponse.json();
        setIsAdmin(userData.admin);

        if (userData.admin) {
          const walletResponse = await fetch('http://localhost:8080/api/v1/wallet/all-wallets', {
            method: 'GET',
            headers: {
              'Authorization': `Bearer ${jwtToken}`,
              'Content-Type': 'application/json',
            },
          });

          if (!walletResponse.ok) {
            throw new Error('Cüzdan verileri alınamadı.');
          }

          const walletData = await walletResponse.json();
          setWallets(walletData);
        } else {
          console.log('Admin yetkisi bulunmuyor.');
          navigate('/Assets');
        }
      } catch (error) {
        console.error('Error fetching data:', error);
      }
    };

    fetchData();
  }, [navigate]);

  const filteredWallets = wallets.filter(wallet =>
    wallet.customer.user.name.toLowerCase().includes(searchTerm.toLowerCase()) || // Kullanıcı adı filtresi
    wallet.customer.name.toLowerCase().includes(searchTerm.toLowerCase()) ||      // Müşteri adı filtresi
    wallet.currency.name.toLowerCase().includes(searchTerm.toLowerCase()) ||      // Döviz cinsi filtresi
    wallet.currency.code.toLowerCase().includes(searchTerm.toLowerCase())         // Döviz kodu filtresi
  );

  return (
    <div>
      <NavBar />
      <div className="customer-wallet-list-container">
        <button className="btn-back" onClick={() => navigate('/Assets')}>
          {t("Back")}
        </button>

         {}
         <input
          type="text"
          placeholder= {t("search")}
          value={searchTerm}
          onChange={(e) => setSearchTerm(e.target.value)}
          className="search-input"
        />

        <h2>{t("allCustomerWallets")}</h2>

       

        {filteredWallets.length > 0 ? (
          filteredWallets.map((wallet, index) => (
            <div key={index} className="wallet-card">
              <table className="wallets-table">
                <thead>
                  <tr>
                    <th>{t("userName")}</th>
                    <th>{t("customerName")}</th>
                    <th>{t("currencyDenomination")}</th>
                    <th>{t("currencyCode")}</th>
                    <th>{t("Quantity")}</th>
                    <th>{t("Amount")}</th>
                    <th>{t("dailyChange")}</th>
                  </tr>
                </thead>
                <tbody>
                  <tr>
                    <td>{wallet.customer.user.name}</td>
                    <td>{wallet.customer.name}</td>
                    <td>{wallet.currency.name}</td>
                    <td>{wallet.currency.code}</td>
                    <td>{wallet.balance.amount} {wallet.currency.symbol}</td>
                    <td>{wallet.balance.tryAmount} ₺</td>
                    <td style={{color: wallet.balance.dailyAmountChange < 0 ? 'red' : 'blue'}}>
                      {wallet.balance.dailyAmountChange} {wallet.currency.symbol}
                    </td>
                  </tr>
                </tbody>
              </table>
            </div>
          ))
        ) : (
          <p>{t("noWalletDataYet")}</p>
        )}
      </div>
      <Footer />
    </div>
  );
};

export default CustomerWallets;
