import React, { useState, useEffect } from "react";
import NavBar from "../components/NavBar";
import Footer from "../components/Footer";
import { useTranslation } from "react-i18next";
import "./Delegation.css";
import { useNavigate } from 'react-router-dom';

const Delegation = () => {
  const [isLoading, setIsLoading] = useState(true);

  const navigate = useNavigate();
  const { t } = useTranslation();

  const [users, setUsers] = useState([]);
  const [userData, setUserData] = useState({
    name: "",
    email: "",
  });
  const [isModalOpen, setModalOpen] = useState(false);
  const [selectedUserId, setSelectedUserId] = useState(null);
  const [successMessage, setSuccessMessage] = useState("");
  const [errorMessage, setErrorMessage] = useState("");

  useEffect(() => {
    const token = localStorage.getItem('jwtToken');
    if (!token) {
      navigate('/giris');
      return;
    }

    const fetchUsers = async () => {
      try {
        const response = await fetch("http://localhost:8080/api/v1/user/all", {
          method: "GET",
          headers: {
            'Authorization': `Bearer ${token}`,
          },
        });

        if (!response.ok) {
          const errorText = await response.text();
          throw new Error(`Network response was not ok: ${response.status} ${response.statusText}. ${errorText}`);
        }

        const data = await response.json();
        setUsers(data);
        setIsLoading(false);

      } catch (error) {
        console.error("Error fetching users", error);
        setErrorMessage(t("Kullanıcıları getirirken bir hata oluştu"));
      }
    };

    fetchUsers();
  }, [navigate, t]);

  const handleUserChange = (event) => {
    const { name, value } = event.target;
    setUserData((prev) => ({ ...prev, [name]: value }));
  };

  const getToken = () => {
    return localStorage.getItem('jwtToken');
  };

  const submitUser = async (event) => {
    event.preventDefault();
    const newUser = {
      name: userData.name,
      email: userData.email,
    };

    const token = getToken();

    if (!token) {
      setErrorMessage(t("tokenNotFound"));
      return;
    }

    try {
      const response = await fetch("http://localhost:8080/api/v1/user/create-user", {
        method: "POST",
        headers: {
          'Authorization': `Bearer ${token}`,
          'Content-Type': 'application/json'
        },
        body: JSON.stringify(newUser),
      });

      const responseData = await response.json();

      if (!response.ok) {
        let errorMessage = t("thereWasAnErrorFetchingUsers");

        if (responseData.message) {
          errorMessage = t("userAlreadyAvailable");
        }

        if (response.status === 400 && responseData.message.includes("already exists")) {
          errorMessage = t("userRegisteredWithThisEmailAlreadyExists");
        }

        setErrorMessage(errorMessage);
        return;
      }

      setUsers((prevUsers) => [...prevUsers, responseData]);
      setUserData({ name: "", email: "" });
      setSuccessMessage(t("userAddedSuccessfully"));
    } catch (error) {
      console.error("Error creating user", error);
      setErrorMessage(t("anErrorOccurredWhileCreatingUser"));
    }
  };

  const openModal = (userId) => {
    setSelectedUserId(userId);
    setModalOpen(true);
  };

  const closeModal = () => {
    setSelectedUserId(null);
    setModalOpen(false);
  };

  const addAdmin = async () => {
    const token = getToken();

    if (!token) {
      setErrorMessage(t("tokenNotFound"));
      return;
    }

    if (selectedUserId) {
      try {
        const response = await fetch(`http://localhost:8080/api/v1/user/create-admin/${selectedUserId}`, {
          method: "POST",
          headers: {
            "Authorization": `Bearer ${token}`,
          },
        });

        const responseData = await response.json();

        if (!response.ok) {
          let errorMessage = "Admin olarak atama sırasında bir hata oluştu";
          if (responseData.message) {
            errorMessage = responseData.message;
          }
          setErrorMessage(errorMessage);
          return;
        }

        if (responseData.message === "Kullanıcı admin yetkisine sahip!") {
          setSuccessMessage(t("thisUserIsAlreadyAnAdmin"));
        } else {
          setUsers((prevUsers) =>
            prevUsers.map((user) =>
              user.id === selectedUserId ? { ...user, admin: true } : user
            )
          );
          setSuccessMessage(t("userSuccessfullyAssignedAsAdmin"));
        }

        closeModal();
      } catch (error) {
        console.error("Error adding admin", error);
        setErrorMessage(error.message || t("anErrorOccurredDuringAssignmentAsAdmin"));
      }
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
          <div style={{ padding: "25px" }}>
            <div className="addAndTable">
              <div className="user-definition-wrapper">
                <form className="Delegation-form" onSubmit={submitUser}>
                  <div className="Delegation-div">
                    <h2>{t("userManagement")}</h2>
                  </div>
                  <div className="input-group">
                    <div className="form-item">
                      <label htmlFor="name">{t("userName")}</label>
                      <input
                        type="text"
                        id="name"
                        name="name"
                        value={userData.name}
                        onChange={handleUserChange}
                        required
                      />
                    </div>
                    <div className="form-item">
                      <label htmlFor="email">{t("userEmail")}</label>
                      <input
                        type="email"
                        id="email"
                        name="email"
                        value={userData.email}
                        onChange={handleUserChange}
                        required
                      />
                    </div>
                    <button className="addButton" type="submit">
                      {t("addUser")}
                    </button>
                  </div>
                </form>
              </div>

              <div className="userTable-wrapper">
                <h2>{t("userList")}</h2>
                <div className="table-scrollable">
                  <table className="userTable">
                    <thead>
                      <tr>
                        <th>{t("userId")}</th>
                        <th>{t("userName")}</th>
                        <th>{t("userEmail")}</th>
                        <th>{t("Authority")}</th>
                        <th>{t("Status")}</th>
                      </tr>
                    </thead>
                    <tbody>
                      {users.map((user) => (
                        <tr key={user.id}>
                          <td>{user.id}</td>
                          <td>{user.name}</td>
                          <td>{user.email}</td>
                          <td>{user.admin ? "Admin" : "Kullanıcı"}</td>
                          <td>
                            <button
                              onClick={() => openModal(user.id)}
                              className="admin-button"
                            >
                              {t("makeAdmin")}
                            </button>
                          </td>
                        </tr>
                      ))}
                    </tbody>
                  </table>
                </div>
              </div>
            </div>
          </div>

          {isModalOpen && (
            <div className="modal-backdrop">
              <div className="modal-content">
                <div className="adminContent">
                  <p>{t("areYouSureYouWantToAddAsAdmin")}?</p>
                  <button onClick={addAdmin} className="confirm-button">
                    {t("Yes")}
                  </button>
                  <button onClick={closeModal} className="close-button-admin">
                    {t("No")}
                  </button>
                </div>
              </div>
            </div>
          )}

          {successMessage && (
            <div className="notification success">
              <p>{successMessage}</p>
              <button
                onClick={() => setSuccessMessage("")}
                className="dismiss-button"
              >
                {t("Ok")}
              </button>
            </div>
          )}

          {errorMessage && (
            <div className="notification error">
              <p>{errorMessage}</p>
              <button
                onClick={() => setErrorMessage("")}
                className="dismiss-button"
              >
                {t("Ok")}
              </button>
            </div>
          )}
        </>
      )}
      <Footer />
    </div>
  );

};

export default Delegation;
