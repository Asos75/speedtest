// Dependencies
import React from 'react';
import { BrowserRouter, Routes, Route } from 'react-router-dom';

// Components
import Header from './Header';
import Home from './Home';
import Register from './Register';
import Login from './Login';

// Styles
import '../styles/App.css';
import '../styles/Home.css';
import '../styles/Header.css';


const App = () => (
  <>
  <Header title="Speedtest reader" />
  <BrowserRouter>
    <Routes>
      <Route path="/" element={<Home />} />
      <Route path="/register" element={<Register />} />
      <Route path="/login" element={<Login />} />
    </Routes>
  </BrowserRouter>
  </>
);

export default App;
