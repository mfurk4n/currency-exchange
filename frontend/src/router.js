import React from 'react';
import { BrowserRouter as Router, Route, Routes, Navigate } from 'react-router-dom';
import LoginPage from './LoginPage/LoginPage.js';
import HomePage from './HomePage/HomePage.js';
import Delegation from './Pages/Delegation.js';
import CustomerDelegation from './Pages/CustomerDelegation.js';
import Market from './Pages/Market.js';
import Assets from './Pages/Assets.js';
import Transactions from './Pages/Transactions.js';
import Orders from './Pages/Orders.js';
import Profile from './Pages/Profile.js';
import PasswordChange from './Pages/PasswordChange.js';
import CreateWallet from './Pages/CreateWallet.js';
import CustomerWallets from './Pages/CustomerWallets.js';
import AddBalance from './Pages/AddBalance.js';

const AppRouter = () => {
    return (
        <Router>
            <Routes>
                <Route path="/" element={<Navigate replace to="/giris" />} />
                <Route path="/giris" element={<LoginPage />} />
                <Route path="/anasayfa" element={<HomePage />} />
                <Route path="/yetkilendirme/kullanici" element={<Delegation />} />
                <Route path="/yetkilendirme/musteri" element={<CustomerDelegation />} />
                <Route path="/exchange/alis-satis" element={<Market />} />
                <Route path="/Assets" element={<Assets />} />
                <Route path="/islemlerim/gecmis-islemler" element={<Transactions />} />
                <Route path="/islemlerim/emirlerim" element={<Orders />} />
                <Route path="/profilim" element={<Profile />} />
                <Route path="/profilim/sifre-degistirme" element={<PasswordChange />} />
                <Route path="/cuzdan-olusturma" element={<CreateWallet />} />
                <Route path="/musteri-cuzdan-listele" element={<CustomerWallets />} />
                <Route path="/bakiye-yukle" element={<AddBalance />} />

            </Routes>
        </Router>
    );
};

export default AppRouter;
