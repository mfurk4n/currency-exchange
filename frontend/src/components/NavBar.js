import React, { useState, useEffect } from 'react';
import { useTranslation } from 'react-i18next';
import { Link, NavLink } from 'react-router-dom';
import './NavBar.css';
import logo from '../Images/logo.png';
import "../HomePage/HomePage.css";
import DropdownMenu from './DropdownMenu';

const NavBar = () => {
  const { t, i18n } = useTranslation();
  const [dateTime, setDateTime] = useState(new Date());
  const [userRole, setUserRole] = useState(() => localStorage.getItem('userRole') === 'true');
  const [customersData, setCustomersData] = useState([]);
  const [selectedCustomer, setSelectedCustomer] = useState(() => {
    const storedCustomer = localStorage.getItem('selectedCustomer');
    return storedCustomer ? JSON.parse(storedCustomer) : {};
  });


  const authItems = [
    { label: 'userManagement', path: '/yetkilendirme/kullanici' },
    { label: 'customerManagement', path: '/yetkilendirme/musteri' }
  ];

  useEffect(() => {
    localStorage.setItem('userRole', userRole);
  }, [userRole]);

  useEffect(() => {
    const savedLanguage = localStorage.getItem('language');
    if (savedLanguage) {
      i18n.changeLanguage(savedLanguage);
    }
  }, [i18n]);

  useEffect(() => {
    const interval = setInterval(() => {
      setDateTime(new Date());
    }, 1000);

    return () => clearInterval(interval);
  }, []);

  useEffect(() => {
    const storedCustomersData = JSON.parse(localStorage.getItem('customersData')) || [];
    setCustomersData(storedCustomersData);
  }, []);

  const handleLanguageChange = (event) => {
    const selectedLanguage = event.target.value;
    i18n.changeLanguage(selectedLanguage);
    localStorage.setItem('language', selectedLanguage);
  };

  const handleCustomerChange = (event) => {
    const selectedCustomerId = event.target.value;
    const selectedCustomerObj = customersData.find(customer => customer.id === selectedCustomerId);
    setSelectedCustomer(selectedCustomerObj);
    localStorage.setItem('selectedCustomer', JSON.stringify(selectedCustomerObj));
    window.location.reload();
  };

  return (
    <div>
      <div className="navbar-container">
        <div className="logo">
          <Link to="/anasayfa">
            <img src={logo} alt="Finexchange Logo" style={{ height: '180px', width: 'auto', marginRight: '5px' }} />
          </Link>
        </div>
        <div className="navbar-links">
          <NavLink to="/anasayfa" className="navbar-link">{t('home')}</NavLink>
          <NavLink to="/exchange/alis-satis" className="navbar-link">{t('buy_sell')}</NavLink>
          <NavLink to="/islemlerim/emirlerim" className="navbar-link">{t('orders')}</NavLink>
          <NavLink to="/islemlerim/gecmis-islemler" className="navbar-link">{t('myTransactions')}</NavLink>
          <NavLink to="/Assets" className="navbar-link">{t('assets')}</NavLink>
          {userRole ? (
            <DropdownMenu label="management" items={authItems} activePath="/yetkilendirme" />
          ) : <NavLink to="/yetkilendirme/musteri" className="navbar-link">{t('management')}</NavLink>}
          {/*<DropdownMenu label="buy_sell" items={exchangeItems} activePath="/exchange"/>
          <DropdownMenu label="myTransactions" items={menuItems} activePath="/islemlerim"/>*/}
          <NavLink to="/profilim" className="navbar-link">{t('profile')}</NavLink>
        </div>
        <div className="options-container">
          <div className='navHolder' style={{ marginTop: '25px' }} >
            <div className='navSel'>
              <select className="select-admin" value={userRole} disabled>
                {userRole ? (
                  <option value="admin">Admin</option>
                ) : (
                  <option value="user">{t('user')}</option>
                )}
              </select>
              <select className="select" value={i18n.language} onChange={handleLanguageChange}>
                <option value="tr">TR</option>
                <option value="en">EN</option>
              </select>
            </div>
            <div className="customer-dropdown">
              <select className="select" value={selectedCustomer.id || ''} onChange={handleCustomerChange}>
                {customersData.map((customer) => {
                  const customerDisplayName = customer.legal
                    ? `Gerçek - ${customer.name} (${customer.nationalId})`
                    : `Tüzel - ${customer.name} (${customer.taxId})`;
                  return (
                    <option key={customer.id} value={customer.id}>
                      {customerDisplayName}
                    </option>
                  );
                })}
              </select>
            </div>
            <div className="navDate" style={{ marginTop: '5px' }} >
              <p>{dateTime.toLocaleDateString()} {/*dateTime.toLocaleTimeString()*/}</p>
            </div>
          </div>
        </div>
      </div>
      <div className='black-line'></div>
    </div>
  );
};

export default NavBar;
