import React, { useState, useEffect } from 'react';
import NavBar from '../components/NavBar';
import Footer from '../components/Footer';
import { useTranslation } from 'react-i18next';
import './CustomerDelegation.css';
import { useNavigate } from 'react-router-dom';


const CustomerDelegation = () => {
    const [isLoading, setIsLoading] = useState(true); // Loading state eklendi
    const navigate = useNavigate();
    const { t } = useTranslation();
    let customersData = JSON.parse(localStorage.getItem('customersData')) || [];

    const [customers, setCustomers] = useState([]);
    const [customerData, setCustomerData] = useState({
        legal: 'tuzel',
        name: '',
        nationalId: '',
        taxId: '',
    });
    const [successMessage, setSuccessMessage] = useState("");
    const [errorMessage, setErrorMessage] = useState("");
    const [errors, setErrors] = useState({
        name: '',
        nationalId: '',
        taxId: '',
    });

    const getUserIdFromToken = () => {
        const token = localStorage.getItem('jwtToken');
        if (!token) {
            return null;
        }
    };

    const fetchCustomers = async () => {
        const token = localStorage.getItem('jwtToken');

        if (!token) {
            console.log('Token bulunamadı');
            navigate('/giris');
            return;
        }

        const userId = getUserIdFromToken();

        try {
            const url = new URL('http://localhost:8080/api/v1/customer/all');
            const response = await fetch(url, {
                method: "GET",
                headers: {
                    'Authorization': `Bearer ${token}`,
                },
            });

            if (!response.ok) {
                const errorText = await response.text();
                console.log(`Error ${response.status}: ${errorText}`);
                setErrorMessage(`Error ${response.status}: ${errorText}`);
                return;
            }

            const data = await response.json();
            setCustomers(data);
            setIsLoading(false);
        } catch (error) {
            console.error("Error fetching customers", error);
            setErrorMessage("Müşterileri getirirken bir hata oluştu.");
        }
    };

    useEffect(() => {
        fetchCustomers();
    }, []);

    const handleCustomerChange = (event) => {
        const { name, value } = event.target;
        setCustomerData(prevData => ({
            ...prevData,
            [name]: value,
        }));

        // Validation logic
        if (name === 'nationalId' && customerData.legal === 'gercek') {
            if (!/^\d+$/.test(value)) {
                setErrors(prevErrors => ({
                    ...prevErrors,
                    nationalId: t('enterValidIDNumber'),
                }));
            } else {
                setErrors(prevErrors => ({
                    ...prevErrors,
                    nationalId: '',
                }));
            }
        }

        if (name === 'taxId' && customerData.legal === 'tuzel') {
            if (!/^\d+$/.test(value)) {
                setErrors(prevErrors => ({
                    ...prevErrors,
                    taxId: t('enterValidTaxNumber'),
                }));
            } else {
                setErrors(prevErrors => ({
                    ...prevErrors,
                    taxId: '',
                }));
            }
        }
    };

    const validateForm = () => {
        const newErrors = {};

        if (!customerData.name) {
            newErrors.name = t('thisFieldCannotBeLeftBlank');
        }

        if (customerData.legal === 'gercek' && !/^\d{11}$/.test(customerData.nationalId)) {
            newErrors.nationalId = t('enterValidIDNumber');
        }

        if (customerData.legal === 'tuzel' && !/^\d{10}$/.test(customerData.taxId)) {
            newErrors.taxId = t('enterValidTaxNumber');
        }

        setErrors(newErrors);
        return Object.keys(newErrors).length === 0;
    };

    const submitCustomer = async (event) => {
        event.preventDefault();

        if (!validateForm()) {
            setErrorMessage(t("makeSureThatYouFillInAllMandatoryFieldsCorrectly"));
            return;
        }

        const newCustomer = {
            legal: customerData.legal === 'gercek' ? true : false,
            name: customerData.name,
            nationalId: customerData.legal === 'gercek' ? customerData.nationalId : '',
            taxId: customerData.legal === 'tuzel' ? customerData.taxId : '',
        };

        const token = localStorage.getItem('jwtToken');

        if (!token) {
            setErrorMessage("Token bulunamadı.");
            return;
        }

        try {
            const response = await fetch("http://localhost:8080/api/v1/customer/create", {
                method: "POST",
                headers: {
                    'Authorization': `Bearer ${token}`,
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(newCustomer),
            });

            const responseData = await response.json();

            if (!response.ok) {
                let errorMessage = "Müşteri oluşturulurken bir hata oluştu.";
                if (responseData.message) {
                    errorMessage = responseData.message;
                }
                setErrorMessage(errorMessage);
                return;
            }

            setCustomers((prevCustomers) => [...prevCustomers, responseData]);
            setCustomerData({ legal: 'tuzel', name: "", nationalId: "", taxId: "" });
            setSuccessMessage(t("customerSuccessfullyAdded"));

            const customerResponse = {
                id: responseData.id,
                name: responseData.name,
                legal: responseData.legal,
                nationalId: responseData.nationalId,
                taxId: responseData.taxId
            };

            customersData.push(customerResponse);

            customersData.sort((a, b) => a.name.localeCompare(b.name));
            localStorage.setItem('customersData', JSON.stringify(customersData));
            const selectedCustomer = JSON.parse(localStorage.getItem('selectedCustomer'));
            if (!selectedCustomer) {
                localStorage.setItem('selectedCustomer', JSON.stringify(customerResponse));
            }
            window.location.reload();
        } catch (error) {
            console.error("Error creating customer", error);
            setErrorMessage("Müşteri oluşturulurken bir hata oluştu.");
        }
    };

    return (
        <div>
            <NavBar />
            {isLoading ? (
                <div className="loading-container">
                    <div className="loading-spinner"></div>
                </div>
            ) : (
                <>
                    <div style={{ padding: '25px' }}>
                        <div className='customerAddAndTable'>
                            <div className="customer-identification-container">
                                <form className="customer-delegation-form" onSubmit={submitCustomer}>
                                    <h2>{t('customerManagement')}</h2>
                                    <div className="customer-input-group">
                                        <div className="customer-form-item">
                                            <label htmlFor="legal">{t('legalReal')}</label>
                                            <select
                                                id="legal"
                                                name="legal"
                                                value={customerData.legal}
                                                onChange={handleCustomerChange}
                                                required
                                            >
                                                <option value="tuzel">{t('Legal')}</option>
                                                <option value="gercek">{t('Real')}</option>
                                            </select>
                                        </div>
                                        <div className="customer-form-item">
                                            <label htmlFor="name">{t('customerName')}</label>
                                            <input
                                                type="text"
                                                id="name"
                                                name="name"
                                                value={customerData.name}
                                                onChange={handleCustomerChange}
                                                required
                                            />
                                            {errors.name && <p className="validation-error">{errors.name}</p>}
                                        </div>
                                        {customerData.legal === 'gercek' && (
                                            <div className="customer-form-item">
                                                <label htmlFor="nationalId">{t('identityNo')}</label>
                                                <input
                                                    type="text"
                                                    id="nationalId"
                                                    name="nationalId"
                                                    value={customerData.nationalId}
                                                    onChange={handleCustomerChange}
                                                    required
                                                />
                                                {errors.nationalId && <p className="validation-error">{errors.nationalId}</p>}
                                            </div>
                                        )}
                                        {customerData.legal === 'tuzel' && (
                                            <div className="customer-form-item">
                                                <label htmlFor="taxId">{t('taxNumber')}</label>
                                                <input
                                                    type="text"
                                                    id="taxId"
                                                    name="taxId"
                                                    value={customerData.taxId}
                                                    onChange={handleCustomerChange}
                                                    required={customerData.legal === 'tuzel'}
                                                    disabled={customerData.legal !== 'tuzel'}
                                                    maxLength={10}
                                                />
                                                {errors.taxId && <p className="validation-error">{errors.taxId}</p>}
                                            </div>
                                        )}
                                        <button className='customer-addButton' type="submit">
                                            {t('addUser')}
                                        </button>
                                    </div>
                                </form>
                            </div>
                            <div className="customerTable-wrapper">
                                <h2>{t("customerList")}</h2>
                                <div className="table-scrollable">
                                    <table className="customerTable">
                                        <thead>
                                            <tr>
                                                <th>{t("customerName")}</th>
                                                <th>{t("customerNationalId")}</th>
                                                <th>{t("taxNumber")}</th>
                                                <th>{t("customerStatus")}</th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            {customers.map((customer, index) => (
                                                <tr key={index}>
                                                    <td>{customer.name}</td>
                                                    <td>{customer.legal ? customer.nationalId : '-'}</td>
                                                    <td>{customer.legal ? '-' : customer.taxId}</td>
                                                    <td>{customer.legal ? t('Real') : t('Legal')}</td>
                                                </tr>
                                            ))}
                                        </tbody>
                                    </table>
                                </div>
                            </div>
                        </div>
                    </div>
                    {successMessage && (
                        <div className="notification success">
                            <p>{successMessage}</p>
                            <button onClick={() => setSuccessMessage("")} className="dismiss-button">
                                {t("Tamam")}
                            </button>
                        </div>
                    )}

                    {errorMessage && (
                        <div className="notification error">
                            <p>{errorMessage}</p>
                            <button onClick={() => setErrorMessage("")} className="dismiss-button">
                                {t("Tamam")}
                            </button>
                        </div>
                    )}
                </>
            )}
            <Footer />
        </div>
    );

};

export default CustomerDelegation;
