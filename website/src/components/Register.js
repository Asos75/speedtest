// Dependencies
import React from 'react';

// Styles
import '../styles/RegisterLogin.css';

const Register = () => {

  // Function to handle form submission
  const handleSubmit = (e) => {
    e.preventDefault();
    const username = e.target.username.value;
    const email = e.target.email.value;
    const password = e.target.password.value;
    console.log
    (`Username: ${username}\nEmail: ${email}\nPassword: ${password}`);
  }

  return (
    <>
      <div className="backgroundImage"></div>
      <h2 className="registerTitle">Register</h2>
      <form onSubmit={handleSubmit} className="registerLoginContainer">
        <h3>Username</h3>
        <div className="usernameField">
          <input type="text" name="username" placeholder="Your username" required/>
        </div>
        <h3>Email</h3>
        <div className="emailField">
          <input type="email" name="email" placeholder="Your email" required/>
        </div>
        <h3>Password</h3>
        <div className="passwordField">
          <input type="password" name="password" placeholder="Your password" required/>
        </div>
        <button type="submit">Register</button>
      </form>
      <p className="existingAccount">Already have an account? <a href="/login">Login</a></p>
    </>
  );
};

export default Register;