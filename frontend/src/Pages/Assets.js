import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import NavBar from '../components/NavBar';
import Footer from '../components/Footer';
import TotalBalanceIcon from '../Images/total-balance.png';
import WalletDistributionChart from '../components/WalletDistributionChart';
import './Assets.css';
import { useTranslation } from 'react-i18next';

const Assets = () => {
  const { t } = useTranslation();
  const [isAdmin, setIsAdmin] = useState(false);
  const [wallets, setWallets] = useState([]);
  const [message, setMessage] = useState('');
  const [showChart, setShowChart] = useState(false);
  const navigate = useNavigate();
  const [isLoading, setIsLoading] = useState(true);

  const selectedCustomer = JSON.parse(localStorage.getItem('selectedCustomer'));
  const customerId = selectedCustomer ? selectedCustomer.id : null;

  const fetchWallets = async () => {
    const jwtToken = localStorage.getItem('jwtToken');
    if (!jwtToken) {
      console.log('JWT token not found.');
      navigate('/giris');
      return;
    }
    if (!customerId) {
      console.log('Customer ID not found');
      setMessage('Müşteri bilgisi bulunamadı.');
      return;
    }

    try {
      const response = await fetch(`http://localhost:8080/api/v1/wallet/all/${customerId}`, {
        method: 'GET',
        headers: {
          'Authorization': `Bearer ${jwtToken}`,
          'Content-Type': 'application/json',
        },
      });

      if (!response.ok) {
        throw new Error('Cüzdan verileri alınamadı.');
      }

      const data = await response.json();
      setWallets(data);
      setIsLoading(false);
    } catch (error) {
      console.error('Error fetching wallets:', error);
    }
  };

  useEffect(() => {
    const fetchData = async () => {
      const jwtToken = localStorage.getItem('jwtToken');
      if (!jwtToken) {
        console.log('JWT token not found.');
        return;
      }

      try {
        const response = await fetch('http://localhost:8080/api/v1/user/get-user', {
          method: 'GET',
          headers: {
            'Authorization': `Bearer ${jwtToken}`,
            'Content-Type': 'application/json'
          },
          credentials: 'include',
        });

        if (!response.ok) {
          throw new Error('Kullanıcı verileri alınamadı.');
        }

        const data = await response.json();
        setIsAdmin(data.admin);

      } catch (error) {
        console.error('Error fetching user data:', error);
      }
    };

    fetchData();
    fetchWallets();
  }, []);

  if (!customerId) {
    return (
      <div>
        <NavBar />
        <div className="assets-container">
          <p>{t("selectedCustomerNotFound")}</p>
        </div>
        <Footer />
      </div>
    );
  }

  const totalBalance = wallets.reduce((total, wallet) => total + (wallet.balance.tryAmount || 0), 0);
  const formattedTotalBalance = new Intl.NumberFormat('tr-TR', { style: 'currency', currency: 'TRY' }).format(totalBalance);

  return (
    <div>
      <NavBar />
      {isLoading ? (
        <div className="loading-container">
          <div className="loading-spinner"></div>
        </div>
      ) : (
        <>
          <div className="assets-container">
            {isAdmin && (
              <div className="button-container-entity">
                <button
                  className="btn-list-customers"
                  onClick={() => {
                    console.log('Butona tıklandı!');
                    navigate('/musteri-cuzdan-listele');
                  }}
                >
                  {t("listAllCustomers")}
                </button>
              </div>
            )}

            <div className="balance-summary">
              <div className="balancee-container">
                <div className="total-balancee">
                  <div className="total-balancee-content">
                    <h2>{t("totalBalance")}</h2>
                    <p>{formattedTotalBalance}</p>
                  </div>
                  <img src={TotalBalanceIcon} alt="Total Balance Icon" className="total-balance-icon" />
                  <button className="btn-bakiye-yukle" onClick={() => navigate('/cuzdan-olusturma')}
                    style={{ width: '220px', height: '45px', borderRadius: '18px' }}>
                    {t("openNewWallet")}
                  </button>

                  <button
                    className="btn-bakiye-yukle"
                    onClick={() => {
                      console.log('Butona tıklandı!');
                      navigate('/bakiye-yukle');
                    }}
                    style={{ width: '220px', height: '45px', borderRadius: '18px' }}>
                    {t("defineBalance")}
                  </button>

                  <button
                    className="btn-show-chart"
                    onClick={() => setShowChart(true)} // Butona basınca grafiği tam ekran yap
                    style={{ width: '220px', height: '45px', borderRadius: '18px' }}>
                    {t("openBalanceChart")}
                  </button>
                </div>
              </div>
            </div>
            <div className="walllets">
              {wallets.length > 0 && (
                <h3 className="customer-name" style={{ textAlign: 'center', marginBottom: '20px' }}>
                  {wallets[0].customer.name} {t("Wallets")}
                </h3>
              )}

              {wallets.length > 0 ? (
                wallets.map((wallet, index) => (
                  <div key={index} className="wallet-card">
                    <table className="wallet-table">
                      <thead>
                        <tr>
                          <th>{t("currencyDenomination")}</th>
                          <th>{t("currencyCode")}</th>
                          <th>{t("Quantity")}</th>
                          <th>{t("Amount")}</th>
                          <th>{t("dailyChange")}</th>
                        </tr>
                      </thead>
                      <tbody>
                        <tr>
                          <td>{wallet.currency.name}</td>
                          <td>{wallet.currency.code}</td>
                          <td>{wallet.balance.amount} {wallet.currency.symbol}</td>
                          <td>{wallet.balance.tryAmount} ₺</td>
                          <td style={{ color: wallet.balance.dailyAmountChange < 0 ? 'red' : 'green' }}>
                            {wallet.balance.dailyAmountChange} {wallet.currency.symbol}
                          </td>
                        </tr>
                      </tbody>
                    </table>
                  </div>
                ))
              ) : (
                <p>{t("youDontHaveYourWalletYet")}</p>
              )}
            </div>
          </div>
          <Footer />

          {showChart && (
            <div className="modal-overlay-grafik" onClick={() => setShowChart(false)}>
              <div className="modal-content-grafik" onClick={(e) => e.stopPropagation()}>
                <button className="close-button-grafik" onClick={() => setShowChart(false)}>X</button>
                <div className="full-screen-chart-grafik">
                  <WalletDistributionChart />
                </div>
              </div>
            </div>
          )}
        </>
      )}
    </div>
  );

};

export default Assets;
