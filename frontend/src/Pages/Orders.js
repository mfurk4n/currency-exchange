import React from 'react';
import OrdersTable from '../components/OrdersTable';
import NavBar from '../components/NavBar';
import Footer from '../components/Footer';

const Orders = () => {
  const selectedCustomer = JSON.parse(localStorage.getItem('selectedCustomer'));
  const customerId = selectedCustomer ? selectedCustomer.id : null;

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
      <OrdersTable />
      <Footer />
    </div>
  );
};

export default Orders;
