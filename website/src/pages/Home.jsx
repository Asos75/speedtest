// Dependencies
import React from 'react';
import { Link } from 'react-router-dom';

const Home = () => {
        //BACKEND URL -> process.env.REACT_APP_BACKEND_URL
return (
    <div className="homeContainer">
    <div className="homePage">
        <div className="oneColumnContainer blueBackground">
                <div className="homePageDescription">
                    <p><b>Speedii</b> allows you to read internet speed test results, 
                            data from various internet speed test sites, 
                             creating your own speed test results based on your geographic location and more.
                    </p>
                </div>
        <div className="oneColumnContainer ">
                <h4 className="homePageSubtitle">Below you can explore the different tools available on this website.</h4>
                <div className="homePageFeatures">
                    <div>
                        <h3>⚡Measure</h3>
                        <p>Check your internet speed, location and ISP info in <Link to="/measure">speedtest</Link></p>
                        <Link to="/measure" className="button">Go to Speedtest</Link>
                    </div>
                    <div>
                        <h3>🌍 Geolocations</h3>
                        <p>Read, measure, and create <Link to="/geolocation">geolocations</Link> on the map</p>
                        <Link to="/geolocation" className="button">Go to Geolocations</Link>
                    </div>
                    <div>
                        <h3>🗼 Mobile Tower</h3>
                        <p>Pinpoint exact <Link to="/mobile-tower">mobile tower</Link> location and explore their data</p>
                        <Link to="/mobile-tower" className="button">Go to Mobile Towers</Link>
                    </div>
                    <div>
                        <h3>📌 Events </h3>
                        <p>Find interesting <Link to="/events">events</Link> nearby</p>
                        <Link to="/events" className="button">Go to Events</Link>
                    </div>
                </div>
            </div>
        </div>
        {/* <div className="homePageSubLayout">
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
        </div> */}
        </div>
    </div>
);
};

/*
ASSETS USED:
MAIN PICTURE - https://unsplash.com/photos/a-body-of-water-with-buildings-along-it-0mRQj088z_o
*/

export default Home;