import React from 'react';

const Home = () => {
        //BACKEND URL -> process.env.REACT_APP_BACKEND_URL
return (
    <>
        <div className="backgroundImage"></div>
        <div className="homePage">
            <h1 className="homePageTitle">Welcome to <a href="/">Speedtest Reader</a></h1>
            <div className="homePageDescription">
            <p>This is a website for reading internet speed test results, 
                data from various internet speed test sites, 
                and for creating your own speed test results, 
                based on your geopgrahic location. <br/>
                Below you can explore the different tools available on this website.
            </p>
            <h4 className="homePageSubtitle">Features on this website</h4>
            <ul className="homePageFeatures">
                <li>Explore, create and edit <a href="/event">events</a></li>
                <li>Read, mesaure, and create <a href="/geolocation">geolocations</a> on the map</li>
                <li>Pinpoint exact <a href="/mobile-tower">mobile tower</a> location and explore their data</li>
                <li>And more</li>
            </ul>
            <p className="homePageCredits">Project created by Andraž, David and Domen.</p>
           
        </div>
        <div className="homePageSubLayout">
            <h3 className="homePageSubtitle">Tools that are available on mobile</h3>
            <div className="mainContentLayout">
                <div className="mainContentElement">
                        <a href="/measurement" className="mainContentLink">⚡Measure Speed</a>
                </div>
                <div className="mainContentElement">
                        <a href="/tower-confirm" className="mainContentLink">🗼Tower Confirm</a>
                </div>
                <div className="mainContentElement">
                        <a href="/dslcity-editor" className="mainContentLink">✍️DSLCity Editor</a>
                </div>
                <div className="mainContentElement">
                        <a href="/scraper" className="mainContentLink">🌐Scraper</a>
                </div>
                <div className="mainContentElement">
                        <a href="/generator" className="mainContentLink">⚙️Generator</a>
                </div>
            </div>
        </div>
        </div>
    </>
);
};

/*
ASSETS USED:
MAIN PICTURE - https://unsplash.com/photos/a-body-of-water-with-buildings-along-it-0mRQj088z_o
*/

export default Home;