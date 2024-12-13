import React, { useState, useEffect } from 'react';
import NavBar from '../components/NavBar';
import Footer from '../components/Footer';
import MarketSellPopup from '../components/MarketSellPopup';
import LimitOrderPopup from '../components/LimitOrderPopup';
import StopOrderPopup from '../components/StopOrderPopup';
import { useTranslation } from 'react-i18next';
import 'flag-icon-css/css/flag-icons.min.css';
import flagCode from '../flag-code.json';
import './Market.css';
import { useNavigate } from 'react-router-dom';

const Market = () => {
  const { t } = useTranslation();
  const navigate = useNavigate();
  const [isLoading, setIsLoading] = useState(true);
  const [showPiyasaPopup, setShowPiyasaPopup] = useState(false);
  const [showLimitPopup, setShowLimitPopup] = useState(false);
  const [showStopPopup, setShowStopPopup] = useState(false);
  const [selectedCurrency, setSelectedCurrency] = useState(null);
  const [crossCurrencies, setCrossCurrencies] = useState([]);
  const [nonCrossCurrencies, setNonCrossCurrencies] = useState([]);
  const [selectedFilterCurrencies, setSelectedFilterCurrencies] = useState('Tümü');
  const [selectedFilterCrossCurrencies, setSelectedFilterCrossCurrencies] = useState('Tümü');
  const [selectedMarketTypes, setSelectedMarketTypes] = useState({});
  const [wallets, setWallets] = useState([]);
  const jwtToken = localStorage.getItem('jwtToken');
  const selectedCustomer = JSON.parse(localStorage.getItem('selectedCustomer'));
  const customerId = selectedCustomer ? selectedCustomer.id : null;

  useEffect(() => {

    if (!jwtToken) {
      console.log('JWT token not found or is empty');
      navigate('/giris');
      return;
    }

    console.log("Customer Idd" + customerId);

    const fetchCurrencies = async () => {
      try {
        let url;
        if (!customerId) {
          url = new URL(`http://localhost:8080/api/v1/exchange/all-part`);
        } else {
          url = new URL(`http://localhost:8080/api/v1/exchange/all-part-wallets/${customerId}`);
        }

        const response = await fetch(url, {
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
        if (!customerId) {
          setCrossCurrencies(data.cross);
          setNonCrossCurrencies(data.noncross);
        } else {
          setCrossCurrencies(data.exchangeRates.cross);
          setNonCrossCurrencies(data.exchangeRates.noncross);
          setWallets(data.wallets);
        }
        setIsLoading(false);
      } catch (error) {
        console.error("Veri çekilirken hata oluştu:", error);
      }
    };

    fetchCurrencies();
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

  const getFlagIconClass = (currencyCode) => {
    return `flag-icon flag-icon-${flagCode[currencyCode]}`;
  };

  const getFilteredCurrencies = (list, filter) => {
    if (filter === "Favoriler" || filter === "Favorites") {
      return list.filter(currency => currency.isFavorite);
    } else if (filter === "Kazananlar" || filter === "Winners") {
      return list.filter(currency => currency.changeRate > 0);
    } else if (filter === "Kaybedenler" || filter === "Losers") {
      return list.filter(currency => currency.changeRate < 0);
    }
    return list;
  };


  const handleMarketSelection = (currencyId, e) => {
    setSelectedMarketTypes((prev) => ({
      ...prev,
      [currencyId]: e.target.value
    }));
  };

  const handleIslemButtonClick = (currency) => {
    setSelectedCurrency(currency);
    const marketType = selectedMarketTypes[currency.id] || 'MARKET';
    if (marketType === 'MARKET') {
      setShowPiyasaPopup(true);
    } else if (marketType === 'LIMIT') {
      setShowLimitPopup(true);
    } else if (marketType === 'STOP') {
      setShowStopPopup(true);
    }
  };

  return (
    <div>
      <NavBar />
      {isLoading ? (
        <div className="loading-container">
          <div className="loading-spinner"></div>
        </div>
      ) : (
        <>
          <div className="wallet-balances">
            {wallets.map(wallet => (
              <div className="wallet-balance" key={wallet.id}>
                <i className={getFlagIconClass(wallet.currency.code)}></i>
                <span>{wallet.currency.code}: {wallet.balance.amount.toFixed(2)} {wallet.currency.symbol}</span>
              </div>
            ))}
          </div>
          <div className="buy-sell-container">
            <div className="content-wrapper">
              <div className="table-container">
                <div className="currency-section">
                  <div className="table-header">
                    <div className="favorites-selection">
                      <select onChange={(e) => setSelectedFilterCrossCurrencies(e.target.value)}>
                        <option value="Tümü">{t('ALL')}</option>
                        <option value="Kazananlar">{t('WINNERS')}</option>
                        <option value="Kaybedenler">{t('LOSERS')}</option>
                      </select>
                    </div>
                  </div>
                  <div className="currency-list">
                    {getFilteredCurrencies(nonCrossCurrencies, selectedFilterCrossCurrencies).map(currency => {
                      const { baseCurrency, targetCurrency } = currency;

                      return (
                        <div className="currency-item" key={currency.id}>
                          <div className="currency-info">
                            <i className={getFlagIconClass(baseCurrency.code)}></i>
                            <span className="currency-pair">{currency.exchangeCode}</span>
                            <i className={getFlagIconClass(targetCurrency.code)}></i>
                            <span className={`currency-percentage ${currency.changeRate < 0 ? 'negative' : 'positive'}`}> {Math.abs(currency.changeRate)}</span>

                            <div className='buySellSection'>
                              <div className="exchange-info">
                                <div className="exchange-rate buy">{currency.ask}</div>
                                <div className="exchange-divider">/</div>
                                <div className="exchange-rate sell">{currency.bid}</div>
                              </div>
                            </div>
                            <div className="market-selection">
                              <select onChange={(e) => handleMarketSelection(currency.id, e)}>
                                <option value="MARKET">{t('MARKET')}</option>
                                <option value="LIMIT">{t('LIMIT')}</option>
                                <option value="STOP">{t('STOP')}</option>
                              </select>
                            </div>
                            <button
                              onClick={() => handleIslemButtonClick(currency)}
                              className="custom-button-a"
                            >
                              {t('islem')}
                            </button>
                          </div>
                        </div>
                      );
                    })}
                  </div>
                </div>

                <div className="currency-section-sag">
                  <div className="table-header">
                    <div className="favorites-selection">
                      <select onChange={(e) => setSelectedFilterCurrencies(e.target.value)}>
                        <option value="Tümü">{t('ALL')}</option>
                        <option value="Kazananlar">{t('WINNERS')}</option>
                        <option value="Kaybedenler">{t('LOSERS')}</option>
                      </select>
                    </div>
                  </div>
                  <div className="currency-list">
                    {getFilteredCurrencies(crossCurrencies, selectedFilterCurrencies).map(currency => {
                      const { baseCurrency, targetCurrency } = currency;

                      return (
                        <div className="currency-item" key={currency.id}>
                          <div className="currency-info">
                            <i className={getFlagIconClass(baseCurrency.code)}></i>
                            <span className="currency-pair">{currency.exchangeCode}</span>
                            <i className={getFlagIconClass(targetCurrency.code)}></i>
                            <span className={`currency-percentage ${currency.changeRate < 0 ? 'negative' : 'positive'}`}> {Math.abs(currency.changeRate)}</span>

                            <div className='buySellSection-sag'>
                              <div className="exchange-info">
                                <div className="exchange-rate buy">{currency.ask}</div>
                              </div>
                            </div>
                          </div>
                        </div>
                      );
                    })}
                  </div>
                </div>
              </div>
            </div>
          </div>
          {showPiyasaPopup && selectedCurrency && (
            <MarketSellPopup
              ask={selectedCurrency.ask}
              bid={selectedCurrency.bid}
              baseCurrency={selectedCurrency.baseCurrency}
              targetCurrency={selectedCurrency.targetCurrency}
              onClose={() => setShowPiyasaPopup(false)}
            />
          )}
          {showLimitPopup && selectedCurrency && (
            <LimitOrderPopup
              ask={selectedCurrency.ask}
              baseCurrency={selectedCurrency.baseCurrency}
              targetCurrency={selectedCurrency.targetCurrency}
              onClose={() => setShowLimitPopup(false)}
            />
          )}
          {showStopPopup && selectedCurrency && (
            <StopOrderPopup
              bid={selectedCurrency.bid}
              baseCurrency={selectedCurrency.baseCurrency}
              targetCurrency={selectedCurrency.targetCurrency}
              onClose={() => setShowStopPopup(false)}
            />
          )}
        </>
      )}
      <Footer />
    </div>
  );

};

export default Market;
