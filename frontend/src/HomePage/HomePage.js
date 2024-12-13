import React, { useState, useEffect } from 'react';
import NavBar from '../components/NavBar';
import CurrencyChart from '../MainPageComponents/CurrencyChart';
import Footer from '../components/Footer';
import CurrencySelector from '../components/CurrencySelector';
import CurrencyTicker from '../components/CurrencyTicker';
import CurrencyConverter from '../MainPageComponents/CurrencyConverter';
import './HomePage.css';
import { useNavigate } from 'react-router-dom';
import { useTranslation } from 'react-i18next';

const HomePage = () => {
  const [chartData1, setChartData1] = useState([]);
  const [chartData2, setChartData2] = useState([]);
  const [selectedCurrency1, setSelectedCurrency1] = useState("USD");
  const [selectedCurrency2, setSelectedCurrency2] = useState("CAD");
  const [currencies, setCurrencies] = useState([]);
  const [currencies2, setCurrencies2] = useState([]);
  const [noncrossRates, setNoncrossRates] = useState({});
  const [isLoading, setIsLoading] = useState(true);
  const { t } = useTranslation();
  const navigate = useNavigate();


  useEffect(() => {
    const fetchData = async () => {
      try {
        const jwtToken = localStorage.getItem('jwtToken');

        if (!jwtToken) {
          console.log('JWT token not found or is empty');
          navigate('/giris');
          return;
        }

        const response = await fetch('http://localhost:8080/api/v1/exchange/chart', {
          method: 'GET',
          headers: {
            'Authorization': `Bearer ${jwtToken}`,
            'Content-Type': 'application/json'
          }
        });

        if (response.status === 200) {
          const data = await response.json();

          setCurrencies(data.noncross.baseCurrencies.map(currency => currency.baseCurrency));
          setCurrencies2(data.cross.baseCurrencies.map(currency => currency.baseCurrency));

          const rates = {};
          data.noncross.baseCurrencies.forEach(currency => {
            const lastDataEntry = currency.datas[currency.datas.length - 1];
            rates[currency.baseCurrency] = lastDataEntry?.ask || 1;
          });
          setNoncrossRates(rates);

          const currencyData1 = data.noncross.baseCurrencies.find(currency => currency.baseCurrency === selectedCurrency1);
          if (currencyData1) {
            const formattedData1 = currencyData1.datas.map(dataPoint => ({
              price: dataPoint.bid,
              date: new Date(dataPoint.dataDate),
            }));
            setChartData1(formattedData1);
          }

          const currencyData2 = data.cross.baseCurrencies.find(currency => currency.baseCurrency === selectedCurrency2);
          if (currencyData2) {
            const formattedData2 = currencyData2.datas.map(dataPoint => ({
              price: dataPoint.bid,
              date: new Date(dataPoint.dataDate),
            }));
            setChartData2(formattedData2);
          }

          setIsLoading(false); // Veriler yüklendiğinde loading durumu false yapılır
        } else {
          console.error('Failed to fetch data:', response.statusText);
        }
      } catch (error) {
        console.error('Error fetching data:', error);
      }
    };

    fetchData();
  }, [selectedCurrency1, selectedCurrency2]);

  return (
    <div>
      <NavBar />
      {isLoading ? (
        <div className="loading-container">
          <div className="loading-spinner"></div>
        </div>
      ) : (
        <>
          <CurrencyTicker />
          <div className="converter-container">
            <h2>{t("currencyCalculator")}</h2>
            <CurrencyConverter rates={noncrossRates} />
          </div>
          <div className='currency-container-items'>
            <div className='currency-container'>
              <h3 className='chart-title'>{t('weeklyExchangeRateChangeBasedOnTRY')}</h3>
              <CurrencySelector
                selectedCurrency={selectedCurrency1}
                onCurrencyChange={setSelectedCurrency1}
                currencies={currencies}
              />
              <CurrencyChart data={chartData1} />
            </div>
            <div className='currency-container'>
              <h3 className='chart-title'>{t('usdBasedWeeklyCrossExchangeRate')}</h3>
              <CurrencySelector
                selectedCurrency={selectedCurrency2}
                onCurrencyChange={setSelectedCurrency2}
                currencies={currencies2}
              />
              <CurrencyChart data={chartData2} />
            </div>
          </div>
          <Footer />
        </>
      )}
    </div>
  );
};

export default HomePage;
