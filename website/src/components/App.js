// Dependencies
import React, { useState } from 'react';
import { BrowserRouter, Routes, Route } from 'react-router-dom';

// Components
import Header from './Header';
// MAIN USE FOR / PATH
import Home from '../pages/Home';
import Register from '../pages/Register';
import Login from '../pages/Login';
import MobileTower from '../pages/MobileTower';
import Tools from '../pages/Tools';
import Geolocation from '../pages/Geolocation';

// Styles
import '../styles/App.css';
import '../styles/Home.css';
import '../styles/Header.css';

// Testing
// import AboutUs from '../pages/AboutUs';

const App = () => {
  const [username, setUsername] = useState(localStorage.getItem('username') || "");

  const handleLogout = () => {
    localStorage.removeItem('token');
    localStorage.removeItem('username');
    setUsername("");
  };

  return (
    <>
      <Header title="Speedtest Reader" username={username} onLogout={handleLogout} />
      <BrowserRouter>
        <Routes>
          <Route path="/" element={<Home />} />
          {/* <Route path="/" element={<AboutUs />} /> */}
          {/* Left side */}
          <Route path="/tools" element={<Tools />} />
          <Route path="/geolocation" element={<Geolocation />} />
          <Route path="/mobile-tower" element={<MobileTower />} />
          {/* Right side */}
          <Route path="/register" element={<Register />} />
          <Route path="/login" element={<Login />} />
        </Routes>
      </BrowserRouter>
    </>
  );
};

export default App;