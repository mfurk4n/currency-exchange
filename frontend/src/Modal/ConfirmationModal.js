import React from 'react';
import './ConfirmationModal.css';
import { useTranslation } from 'react-i18next';

const ConfirmationModal = ({ isOpen, onConfirm, onCancel }) => {
  const { t } = useTranslation();
  if (!isOpen) return null;

  return (
    <div className="modal-overlay">
      <div className="modal-content">
        <div className="content-container">
          <h2>{t("aboutToExit")}</h2>
          <p>{t("wantToLogOut")}?</p>
          <div className="button-container">
            <button onClick={onConfirm}>{t("Yes")}</button>
            <button onClick={onCancel}>{t("No")}</button>
          </div>
        </div>
      </div>
    </div>
  );
};
export default ConfirmationModal;
