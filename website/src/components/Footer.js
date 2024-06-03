// Dependencies
import React from 'react';


const Footer = ({username }) => {
    console.log("Username: " + username.stri);
    return (
        <footer className="oneColumnContainer footerLayout blackBackground">
            <div className="footerLinks">
                <a href="/measure" className="footerLink">Speedtest</a>
                <a href="/geolocation" className="footerLink">Measurements</a>
                <a href="/mobile-tower" className="footerLink">Mobile Towers</a>
                <a href="/events" className="footerLink">Events</a>
                { username  && (<a href="/user" className="footerLink">Your profile</a>)}
            </div>
            <p className="footerCredits"> <b>© 2024 Speedii</b> | School project created by Andraž, David and Domen. <a href="/about-us" className="button">More about us</a></p>
        </footer>
    );
};

export default Footer;