// Dependencies
import React, {useState} from 'react';

// Styles
import '../styles/RegisterLogin.css';

const Register = () => {
  // States for form fields
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [email, setEmail] = useState("");
  
  // Regsiter user to the website
  async function Register(e){
    e.preventDefault();
    // console.log("Fetching" + process.env.REACT_APP_BACKEND_URL);
    const res = await fetch(`${process.env.REACT_APP_BACKEND_URL}/users/register`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
            email: email,
            username: username,
            password: password
        })
    });
    const data = await res.json();
    if(data.token){
        // Save the token to localStorage or to a cookie
        localStorage.setItem('token', data.token);
        window.location.href="/";
    }
    else{
        setUsername("");
        setPassword("");
        setEmail("");
    }
}

  return (
    <div className="blueBackground">
      {/* <div className="blueBackground"></div> */}
      <h2 className="registerTitle">Create an account</h2>
      <form onSubmit={Register} className="registerLoginContainer">
        <h3>Username</h3>
        <div className="usernameField">
          <input type="text" name="username" placeholder="Your username" value={username} onChange={(e)=>(setUsername(e.target.value))} required/>
        </div>
        <h3>Email</h3>
        <div className="emailField">
          <input type="email" name="email" placeholder="Your email" value={email} onChange={(e)=>(setEmail(e.target.value))} required/>
        </div>
        <h3>Password</h3>
        <div className="passwordField">
          <input type="password" name="password" placeholder="Your password" value={password} onChange={(e)=>(setPassword(e.target.value))} required/>
        </div>
        <button type="submit">Register</button>
      </form>
      <p className="existingAccount">Already have an account? <a href="/login">Login</a></p>
    </div>
  );
};

export default Register;