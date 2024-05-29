// Dependencies
import React from 'react';

const Footer = () => {
    return (
        <footer className="oneColumnContainer footerLayout blackBackground">
            <div class="footerLinks">
                <a href="/measure" class="footerLink">Speedtest</a>
                <a href="/geolocation" class="footerLink">Measurements</a>
                <a href="/mobile-tower" class="footerLink">Mobile Towers</a>
                <a href="/events" class="footerLink">Events</a>
            </div>
            <p className="footerCredits"> <b>© 2024 Speedii</b> | School project created by Andraž, David and Domen. <a href="/about-us" className="button">More about us</a></p>
        </footer>
    );
};

export default Footer;