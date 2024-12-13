import React, { useEffect, useState } from 'react';
import { Doughnut } from 'react-chartjs-2';
import { Chart as ChartJS, ArcElement, Tooltip, Legend } from 'chart.js';
import { useNavigate } from 'react-router-dom';

ChartJS.register(ArcElement, Tooltip, Legend);

const WalletDistributionChart = () => {
    const [walletData, setWalletData] = useState({
        labels: [],
        balances: []
    });
    const navigate = useNavigate();
    useEffect(() => {
        const fetchData = async () => {

            const selectedCustomer = JSON.parse(localStorage.getItem('selectedCustomer'));
            const customerId = selectedCustomer ? selectedCustomer.id : null;

            const jwtToken = localStorage.getItem('jwtToken');
            if (!jwtToken) {
                console.log('JWT token bulunamadı.');
                navigate('/giris');
                return;
            }

            try {
                const response = await fetch(`http://localhost:8080/api/v1/wallet/balance-currency/${customerId}`, {
                    method: 'GET',
                    headers: {
                        'Authorization': `Bearer ${jwtToken}`,
                        'Content-Type': 'application/json'
                    },
                    credentials: 'include',
                });

                if (!response.ok) {
                    throw new Error('Ağ yanıtı geçerli değil.');
                }

                const data = await response.json();
                if (data.length === 0) {
                    throw new Error('Boş yanıt alındı.');
                }

                const labels = data.map(item => item.currency.code);
                const balances = data.map(item => item.balance.tryAmount);

                setWalletData({
                    labels: labels,
                    balances: balances
                });
            } catch (error) {
                console.error('Veri Çekme Hatası:', error);
            }
        };

        fetchData();
    },);

    const chartData = {
        labels: walletData.labels,
        datasets: [
            {
                label: 'Bakiye Dağılımı',
                data: walletData.balances,
                backgroundColor: [
                    '#FF4500', // Turuncu Kırmızı
                    '#1E90FF', // Dodger Mavisi
                    '#FFD700', // Altın
                    '#32CD32', // Limon Yeşili
                    '#8A2BE2', // Mavi Menekşe
                    '#FF6347', // Domates
                    '#FFFF00', // Sarı
                    '#C0C0C0', // Gümüş
                    '#6A5ACD', // Slate Mavi
                    '#00FA9A', // Orman Yeşili
                    '#FFA500', // Portakal
                    '#BA55D3', // Orkide
                    '#F4A460', // Kumlu Kahverengi
                    '#20B2AA', // Açık Deniz Mavisi
                    '#7CFC00', // Çim Yeşili
                    '#B22222', // Ateş Tuğlası
                ].slice(0, walletData.labels.length),
                borderColor: [
                    '#FF4500',
                    '#1E90FF',
                    '#FFD700',
                    '#32CD32',
                    '#8A2BE2',
                    '#FF6347',
                    '#FFFF00',
                    '#C0C0C0',
                    '#6A5ACD',
                    '#00FA9A',
                    '#FFA500',
                    '#BA55D3',
                    '#F4A460',
                    '#20B2AA',
                    '#7CFC00',
                    '#B22222',
                ].slice(0, walletData.labels.length),
                borderWidth: 2,
            },
        ],
    };

    const options = {
        plugins: {
            legend: {
                display: true,
                position: 'top',
                align: 'center',
                labels: {
                    font: {
                        size: 20,
                    },
                },

            },
            title: {
                display: true,
                text: 'Bakiye Dağılımı',
                font: {
                    size: 16,
                },
            },
        },
        layout: {
            padding: {
                top: 40,
                bottom: 40,
                right: 0,

            },
        },
    };

    return (
        <div style={{ height: '500px', width: '800px' }}>
            <Doughnut data={chartData} options={options} />
        </div>
    );
};

export default WalletDistributionChart;
