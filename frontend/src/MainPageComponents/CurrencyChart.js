import React from 'react';
import { Line } from 'react-chartjs-2';
import { Chart as ChartJS, CategoryScale, LinearScale, PointElement, LineElement, TimeScale, Tooltip, Legend } from 'chart.js';
import 'chartjs-adapter-date-fns';
import './CurrencyChart.css'
import { useTranslation } from 'react-i18next';

ChartJS.register(
  CategoryScale,
  LinearScale,
  PointElement,
  LineElement,
  TimeScale,
  Tooltip,
  Legend
);

const CurrencyChart = ({ data }) => {
  const { t } = useTranslation();
  const chartData = {
    labels: data.map((point) => new Date(point.date)),
    datasets: [
      {
        label: 'Fiyat',
        data: data.map((point) => ({ x: new Date(point.date), y: point.price })),
        borderColor: '#4959ea',
        backgroundColor: '#fff',
        tension: 0.4,
      },
    ],
  };

  const options = {
    plugins: {
      legend: {
        display: false,

        onClick: () => { },
      },

      title: {
        display: true,
        text: '1 Haftalık Döviz Değişimi',
      },
    },
    scales: {
      x: {
        type: 'time',
        time: {
          unit: 'day'
        },
        title: {
          display: true,
          text: t('Date')
        }
      },
      y: {
        type: 'linear',
        position: 'bottom',
        title: {
          display: true,
          text: t('Price')
        }
      }
    }
  };

  return <Line data={chartData} options={options} />;
};

export default CurrencyChart;
