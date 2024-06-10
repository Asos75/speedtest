// Dependencies
import React, { useEffect, useState} from 'react';

// Assets
import Speedii from '../assets/Icons/speedii.png';
import { Squeeze as Hamburger } from 'hamburger-react'

const DropdownMenu = ({ isOpen, children, side}) => {
  return isOpen ? 
  ( side === "left" ? <div className="dropdown-menu show">{children}</div> : <div className="dropdown-menu-right show">{children}</div> )
  : null;
};

const Header = ({ username, onLogout }) => {
  const [isOpenLeft, setOpenLeft] = useState(false);
  const [isOpenRight, setOpenRight] = useState(false);
  const [windowWidth, setWindowWidth] = useState(window.innerWidth);

  const updateWindowWidth = () => {
    setWindowWidth(window.innerWidth);
  };

  useEffect(() => {
    window.addEventListener('resize', updateWindowWidth);
    return () => window.removeEventListener('resize', updateWindowWidth);
  }, []);

  return (
    <header className="header">
      <h1 className="header-title">
        <a href="/" className="header-title-link">
          <img src={Speedii} alt="Speedii" className="header-logo" />
        </a>
      </h1>
      {windowWidth <= 1290 ? (
        <div className="header-left-hamburger">
          <Hamburger toggled={isOpenLeft} toggle={setOpenLeft} className="hamburger-left"/>
          <DropdownMenu isOpen={isOpenLeft} side="left">
            <a href="/measure">⚡Measure</a>
            <a href="/geolocation">🌍Geolocations</a>
            <a href="/mobile-tower">🗼Mobile Towers</a>
          </DropdownMenu>
        </div>
      ) : (
        <div className="header-left">
          <a href="/measure">⚡Measure</a>
          <a href="/geolocation">🌍Geolocations</a>
          <a href="/mobile-tower">🗼Mobile Towers</a>
        </div>
      )}
      {windowWidth <= 750 ? (
          <div className="header-right-hamburger">
            <Hamburger toggled={isOpenRight} toggle={setOpenRight} className="hamburger-right"/>
            <DropdownMenu isOpen={isOpenRight} side="right">
              {username ? (
                <>
                  <span>Hello, <a href="/user">{username} </a></span>
                  <button onClick={onLogout}>Logout</button>
                </>
              ) : (
                <>
                  <a href="/login">Login</a>
                  <a href="/register">Register</a>
                </>
              )}
            </DropdownMenu>
          </div>
        ) : (
          username ? (
            <div className="header-right-loggedIn">
              <span>Hello, <b> <a href="/user">{username} </a></b></span>
              <button onClick={onLogout}>Logout</button>
            </div>
          ) : (
            <div className="header-right">
              <a href="/login">Login</a>
              <a href="/register">Register</a>
            </div>
          )
        )}
    </header>
  );
};

export default Header;