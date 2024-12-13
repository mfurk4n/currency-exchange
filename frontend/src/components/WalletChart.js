import React from 'react';
import { LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, Legend, ResponsiveContainer, Dot } from 'recharts';

const WalletChart = ({ data }) => {
  return (
    <ResponsiveContainer width="100%" height={200}>
      <LineChart data={data}>
        <CartesianGrid strokeDasharray="3 3" />
        <XAxis dataKey="date" />
        <YAxis />
        <Tooltip />
        <Legend />
        <Line
          type="monotone"
          dataKey="profitLoss"
          stroke="#8884d8"
          dot={<Dot stroke="#8884d8" strokeWidth={2} fill="#fff" r={4} />}
          activeDot={<Dot stroke="#8884d8" strokeWidth={2} fill="#fff" r={6} />}
        />
      </LineChart>
    </ResponsiveContainer>
  );
};

export default WalletChart;
