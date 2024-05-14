import React from 'react';

const Login = () => {

  // Function to handle form submission
  const handleSubmit = (e) => {
    e.preventDefault();
    const username = e.target.username.value;
    const password = e.target.password.value;
    console.log(`Username: ${username}\nPassword: ${password}`);
  }

  return (
    <>
      <div className="backgroundImage"></div>
      <h2 className="loginTitle">Login Page</h2>
      <form onSubmit={handleSubmit} className="registerLoginContainer">
        <h3>Username</h3>
        <div>
          <input type="text" name="username" placeholder="Your username" required/>
        </div>
        <h3>Password</h3>
        <div>
          <input type="password" name="password" placeholder="Your password" required/>
        </div>
        <button type="submit">Login</button>
      </form>
      <p className="existingAccount">Don't have an account? <a href="/register">Register</a></p>
    </>
  );
};

export default Login;