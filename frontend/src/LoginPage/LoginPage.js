import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import './LoginPage.css';
import MessageBox from '../components/MessageBox';
import logo from '../Images/logo.png';

const LoginPage = () => {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [errorMessage, setErrorMessage] = useState('');
  const navigate = useNavigate();

  const handleLogin = async (e) => {
    e.preventDefault();

    try {
      const response = await fetch('http://localhost:8080/api/v1/auth/login', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify({ email, password })
      });

      if (response.ok) {
        const data = await response.json();
        localStorage.setItem('jwtToken', data.jwtToken);
        localStorage.setItem('userRole', data.admin);

        if (data.customers && data.customers.length > 0) {
          const customersData = data.customers.map(customer => ({
            id: customer.id,
            name: customer.name,
            legal: customer.legal,
            nationalId: customer.nationalId,
            taxId: customer.taxId
          }));

          if (customersData && customersData.length > 0) {
            customersData.sort((a, b) => a.name.localeCompare(b.name));
            localStorage.setItem('customersData', JSON.stringify(customersData));
            localStorage.setItem('selectedCustomer', JSON.stringify(customersData[0]));
          }

        } else {
          console.log('Müşteri bilgisi yok.');
        }

        navigate('/anasayfa');
      } else {
        const errorData = await response.json();
        setErrorMessage(errorData.message || 'Hatalı giriş');
      }
    } catch (error) {
      console.error('Login error:', error);
      setErrorMessage('Bir hata oluştu. Lütfen tekrar deneyin.');
    }
  };

  const closeModal = () => {
    setErrorMessage('');
  };

  return (
    <div className="container-login">
      <img src={logo} alt="Finexchange Logo" style={{ height: '300px', width: 'auto', marginRight: '220px' }} />
      <div className="loginBox">
        <h1 className="title-login">Sign In</h1>
        <form className="form" onSubmit={handleLogin}>
          <label className="label">Mail</label>
          <input
            className="input-login"
            type="email"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            required
          />
          <label className="label">Password</label>
          <input
            className="input-login"
            type="password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            required
          />
          <button className="loginButton" type="submit">Login</button>
        </form>
      </div>
      {errorMessage && <MessageBox message={errorMessage} onClose={closeModal} />}
    </div>
  );
};

export default LoginPage;
