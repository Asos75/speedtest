// Dependencies
import React, { useState, useEffect } from 'react';
import axios from 'axios';

// Styles
import '../styles/User.css';

function UserPage() {
  const [user, setUser] = useState(null);

  useEffect(() => {
    const id = localStorage.getItem('id');
    const token = localStorage.getItem('token');

    axios.get(`${process.env.REACT_APP_BACKEND_URL}/users/users/${id}`, {
      headers: {
        'Authorization': `Bearer ${token}`
      }
    })
    .then(response => {
      setUser(response.data);
    })
    .catch(error => {
      console.error('There was an error!', error);
    });
  }, []);

  return (
    <div className="userContainer">
        <h1 className="mobileTowerTitle">User page</h1>
        <div className="oneColumnContainer blueBackground">
          <div className="userDescription">
            {user ? (
              <>
                <div className="userInfo">
                  <h3>User: <b>{user.username}</b></h3>
                  <p>Email: <b>{user.email}</b></p>
                  <p>{user.admin ? 'Admin' : 'Default user'}</p>
                </div>
                <h4>Add data to database</h4>
                <div className="userAddInfo">
                  <a href="/measure">Add a new measurement</a>
                  <a href="/geolocation">Add an event</a>
                </div>
              </>
            ) : (
              <p>Loading...</p>
            )}
          </div>
          <div className="ad"></div>
      </div>
    </div>
  );
}

export default UserPage;
