import React from 'react';
import './CurrencyCard.css';

const CurrencyCard = ({ label, value, gradientStart, gradientEnd }) => {
  const [baseCurrency, quoteCurrency] = label.split('/');

  const gradientId = `gradient-${label.replace('/', '-')}`;

  return (
    <div className="currency-card">
      <svg
        width="40"
        height="40"
        viewBox="4 6 17 17"
        fill="none"
        xmlns="http://www.w3.org/2000/svg"
      >
        <g filter="url(#filter0_i_420_185)">
          <rect width="32" height="32" rx="4" fill="#292D39"></rect>
          <path
            fillRule="evenodd"
            clipRule="evenodd"
            d="M18 9H21.5V12.5H20.5V10.7071L15.5 15.7071L14 14.2071L9.85355 18.3536L9.14645 17.6464L14 12.7929L15.5 14.2929L19.7929 10H18V9ZM18.5 13.5H21.5V22H23.5V23H8.5V22H10.5V19.5H13.5V22H14.5V16.5H17.5V22H18.5V13.5ZM19.5 22H20.5V14.5H19.5V22ZM16.5 22V17.5H15.5V22H16.5ZM12.5 22V20.5H11.5V22H12.5Z"
            fill="#0095FF"
          ></path>
        </g>
        <defs>
          <filter
            id="filter0_i_420_185"
            x="0"
            y="-2"
            width="38"
            height="38"
            filterUnits="userSpaceOnUse"
            colorInterpolationFilters="sRGB"
          >
            <feFlood floodOpacity="0" result="BackgroundImageFix"></feFlood>
            <feBlend
              mode="normal"
              in="SourceGraphic"
              in2="BackgroundImageFix"
              result="shape"
            ></feBlend>
            <feColorMatrix
              in="SourceAlpha"
              type="matrix"
              values="0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 127 0"
              result="hardAlpha"
            ></feColorMatrix>
            <feOffset dx="2" dy="-2"></feOffset>
            <feGaussianBlur stdDeviation="6"></feGaussianBlur>
            <feComposite
              in2="hardAlpha"
              operator="arithmetic"
              k2="-1"
              k3="1"
            ></feComposite>
            <feColorMatrix
              type="matrix"
              values="0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0.25 0"
            ></feColorMatrix>
            <feBlend
              mode="normal"
              in2="shape"
              result="effect1_innerShadow_420_185"
            ></feBlend>
          </filter>
        </defs>
      </svg>
      <div className="currency-card-details">
        <svg
          width="80"
          height="60"
          viewBox="0 0 48 48"
          fill="none"
          xmlns="http://www.w3.org/2000/svg"
        >
          <path
            d="M22 1.1547C23.2376 0.440169 24.7624 0.440169 26 1.1547L42.7846 10.8453C44.0222 11.5598 44.7846 12.8803 44.7846 14.3094V33.6906C44.7846 35.1197 44.0222 36.4402 42.7846 37.1547L26 46.8453C24.7624 47.5598 23.2376 47.5598 22 46.8453L5.21539 37.1547C3.97779 36.4402 3.21539 35.1197 3.21539 33.6906V14.3094C3.21539 12.8803 3.97779 11.5598 5.21539 10.8453L22 1.1547Z"
            fill={`url(#${gradientId})`}
          ></path>
          <defs>
            <linearGradient
              id={gradientId}
              x1="24"
              y1="0"
              x2="24"
              y2="48"
              gradientUnits="userSpaceOnUse"
            >
              <stop stopColor={gradientStart}></stop>
              <stop offset="1" stopColor={gradientEnd}></stop>
            </linearGradient>
          </defs>
        </svg>
        <div className="currency-card-info">
          <div className="currency-label">{label}</div>
          <div className="currency-value">
            1 {baseCurrency} = {value} {quoteCurrency}
          </div>
        </div>
      </div>
    </div>
  );
};

export default CurrencyCard;
