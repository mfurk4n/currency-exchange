import React, { useEffect, useRef, useState } from 'react';
import './CurrencyTicker.css';
import { useNavigate } from 'react-router-dom';

const CurrencyTicker = () => {
  const tickerRef = useRef(null);
  const [position, setPosition] = useState(0);
  const [exchangeRates, setExchangeRates] = useState([]);
  const navigate = useNavigate();
  const jwtToken = localStorage.getItem('jwtToken');

  useEffect(() => {
    if (!jwtToken) {
      console.log('JWT token not found or is empty');
      navigate('/giris');
      return;
    }

    const fetchExchangeRates = async () => {
      try {
        const response = await fetch('http://localhost:8080/api/v1/exchange/all', {
          method: 'GET',
          headers: {
            'Authorization': `Bearer ${jwtToken}`,
            'Content-Type': 'application/json',
          },
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
        setExchangeRates(data);
      } catch (error) {
        console.error('Veri çekilirken hata oluştu:', error);
      }
    };

    fetchExchangeRates();
  }, [jwtToken, navigate]);

  useEffect(() => {
    const tickerElement = tickerRef.current;
    const tickerWidth = tickerElement.scrollWidth;
    const windowWidth = window.innerWidth;
    let animationFrameId;
    const speed = 0.5;
    const pauseDuration = 0;
    const initialPauseDuration = 1000;

    const animate = () => {
      setPosition((prevPosition) => {
        let newPosition = prevPosition - speed;
        if (newPosition <= -tickerWidth) {
          newPosition = windowWidth;
        }
        tickerElement.style.transform = `translateX(${newPosition}px)`;
        return newPosition;
      });
      animationFrameId = requestAnimationFrame(animate);
    };

    setTimeout(() => {
      animate();
    }, initialPauseDuration);

    return () => cancelAnimationFrame(animationFrameId);
  }, []);

  const handleMouseDown = (e) => {
    e.preventDefault();
    let startX = e.pageX;

    const handleMouseMove = (moveEvent) => {
      const dx = moveEvent.pageX - startX;
      setPosition((prevPosition) => {
        tickerRef.current.style.transform = `translateX(${prevPosition + dx}px)`;
        return prevPosition + dx;
      });
      startX = moveEvent.pageX;
    };

    const handleMouseUp = () => {
      window.removeEventListener('mousemove', handleMouseMove);
      window.removeEventListener('mouseup', handleMouseUp);
    };

    window.addEventListener('mousemove', handleMouseMove);
    window.addEventListener('mouseup', handleMouseUp);
  };

  return (
    <div className="ticker-wrapper">
      <div className="ticker" ref={tickerRef} onMouseDown={handleMouseDown}>
        {exchangeRates.map((rate) => (
          <div key={rate.id} className="ticker-item">
            <span style={{ fontSize: '18px' }}>{rate.exchangeCode}: </span>
            {rate.targetCurrency.code === "TRY" ? (
              <span style={{ fontSize: '19px' }}>
                {rate.ask.toFixed(2)} / {rate.bid.toFixed(2)}
              </span>
            ) : (
              <span style={{ fontSize: '19px' }}>
                {rate.ask.toFixed(2)} {rate.targetCurrency.symbol}
              </span>
            )}
            <span className={rate.changeRate > 0 ? "rate-up" : "rate-down"}>
              {rate.changeRate > 0 ? "▲" : "▼"}
              {Math.abs(rate.changeRate)}%
            </span>
          </div>
        ))}
      </div>
    </div>
  );
};

export default CurrencyTicker;
