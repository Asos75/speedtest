// Dependency
import React, { useState } from 'react';

// Styles
import '../styles/RegisterLogin.css';

const Login = () => {
  // States for form fields
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");

  // Login user to the website
  async function Login(e){
    e.preventDefault();
    const res = await fetch(`${process.env.REACT_APP_BACKEND_URL}/users/login`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
            username: username,
            password: password
        })
    });
    const data = await res.json();
    console.log("User data: ", data);
    if(data.token){
      // Save the token and username to localStorage or to a cookie
      localStorage.setItem('token', data.token);
      localStorage.setItem('username', username);
      localStorage.setItem('id', data.user._id);
      window.location.href="/";
    }
    else{
        setUsername("");
        setPassword("");
    }
  }

  return (
    <div className="blueBackground">
      <h2 className="loginTitle">Login Page</h2>
      <form onSubmit={Login} className="registerLoginContainer">
        <h3>Username</h3>
        <div>
          <input type="text" name="username" placeholder="Your username" value={username} onChange={(e)=>(setUsername(e.target.value))} required/>
        </div>
        <h3>Password</h3>
        <div>
          <input type="password" name="password" placeholder="Your password" value={password} onChange={(e)=>(setPassword(e.target.value))} required/>
        </div>
        <button type="submit">Login</button>
      </form>
      <p className="existingAccount">Don't have an account? <a href="/register">Register</a></p>
    </div>
  );
};

export default Login;