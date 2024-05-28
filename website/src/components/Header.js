// Dependencies
import React from 'react';

// Assets
import Speedii from '../assets/Icons/speedii.png';

const Header = ({ title, username, onLogout }) => {
  return (
    <header className="header">
      <h1 className="header-title">
        <a href="/" className="header-title-link">
            {/* {title} */}
            <img src={Speedii} alt="Speedii" className="header-logo" />
        </a>
    </h1>
    {/* Events will be implemented into user's page */}
      <div className="header-left">
        <a href="/measure">⚡Measure</a>
        <a href="/geolocation">🌍Geolocations</a>
        <a href="/mobile-tower">🗼Mobile Towers</a>
      </div>
      {username ? (
        <div className="header-right-loggedIn">
          <span>Hello, <b>{username}</b></span>
          <button onClick={onLogout}>Logout</button>
        </div>
      ) : (
        <div className="header-right">
          <a href="/login" className="header-Login">Log in</a>
          <a href="/register" className="header-Register">Register</a>
        </div>
      )}
    </header>
  );
};

export default Header;