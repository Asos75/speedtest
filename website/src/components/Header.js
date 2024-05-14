import React from 'react';

const Header = ({ title }) => {
  return (
    <header className="header">
      <h1 className="header-title">
        <a href="/" className="header-title-link">
            {title}
        </a>
    </h1>
    {/* Events will be implemented into user's page */}
      <div className="header-left">
        <a href="/measurement" className="header-Tools">Tools</a>
        <a href="/geolocations" className="header-Geolocations">Geolocations</a>
        <a href="/mobile-towers" className="header-MobileTowers">Mobile Towers</a>
      </div>
      <div className="header-right">
        <a href="/login" className="header-Login">Log in</a>
        <a href="/register" className="header-Register">Register</a>
      </div>
    </header>
  );
};

export default Header;