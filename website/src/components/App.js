// Dependencies
import React, { useState } from 'react';
import { BrowserRouter, Routes, Route } from 'react-router-dom';

// Components
import Header from './Header';
import Footer from './Footer';
// MAIN USE FOR / PATH
import Home from '../pages/Home';
import Register from '../pages/Register';
import Login from '../pages/Login';
import MobileTower from '../pages/MobileTower';
// import Tools from '../pages/Tools';
import Measure from '../pages/Measure';
import Geolocation from '../pages/Geolocation';
import AboutUs from '../pages/AboutUs';
import Events from '../pages/Events';
import User from '../pages/User';

// Styles
import '../styles/App.css';
import '../styles/Home.css';
import '../styles/Header.css';
import '../styles/Footer.css';

// Testing
// import AboutUs from '../pages/AboutUs';

const App = () => {
  const [username, setUsername] = useState(localStorage.getItem('username') || "");

  const handleLogout = () => {
    localStorage.removeItem('token');
    localStorage.removeItem('username');
    setUsername("");
    window.location.href="/";
  };

  return (
    <>
      <Header title="Speedtest Reader" username={username} onLogout={handleLogout} />
      <BrowserRouter>
        <Routes>
          <Route path="/" element={<Home />} />
          {/* <Route path="/" element={<AboutUs />} /> */}
          {/* Left side */}
          <Route path="/measure" element={<Measure username={username}/>} />
          <Route path="/geolocation" element={<Geolocation />} />
          <Route path="/mobile-tower" element={<MobileTower />} />
          {/* Right side */}
          <Route path="/register" element={<Register />} />
          <Route path="/login" element={<Login />} />
          {/* About us */}
          <Route path="/about-us" element={<AboutUs />} />
          {/* Extra router */}
          <Route path="/events" element={<Events />} />
          <Route path="/user" element={<User />} />
        </Routes>
      </BrowserRouter>
      <Footer username={username}/>
    </>
  );
};

export default App;