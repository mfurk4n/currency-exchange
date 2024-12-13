import React, { useState } from 'react';
import { useTranslation } from 'react-i18next';
import { NavLink, useLocation } from 'react-router-dom';
import './DropdownMenu.css';
import './NavBar.css';


const DropdownMenu = ({ label, items, activePath }) => {
    const { t } = useTranslation();
    const [isOpen, setIsOpen] = useState(false);
    const location = useLocation();
    const isActive = location.pathname.startsWith(activePath);

    const toggleDropdown = () => setIsOpen(!isOpen);

    return (
        <div className="dropdown">
            <NavLink
                to="javascript:void(0);"
                onClick={toggleDropdown}
                className={`navbar-link ${isActive ? 'active' : ''}`}
            >
                {t(label)}
            </NavLink>

            {isOpen && (
                <div className="dropdown-menu">
                    {items.map((item, index) => (
                        <NavLink to={item.path} className="dropdown-item" key={index}>
                            {t(item.label)}
                        </NavLink>
                    ))}
                </div>
            )}
        </div>
    );
};

export default DropdownMenu;
