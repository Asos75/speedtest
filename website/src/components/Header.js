import React from 'react';

const Header = ({ title }) => {
  return (
    <header className="header">
      <h1 className="header-title">
        <a href="/" className="header-title-link">
            {title}
        </a>
    </h1>
      <div className="header-right">
        <a href="/login" className="header-Login">Log in</a>
        <a href="/register" className="header-Register">Register</a>
    </div>
    </header>
  );
};

export default Header;