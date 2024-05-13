import React from 'react';

const Home = () => {
return (
    <>
    <div className="backgroundImage"></div>
    <div className="homePage">
        <h1 className="homePageTitle">Welcome to <b>Speedtest Reader</b></h1>
        <div class="homePageDescription">
            {/* Change this part to ul > li list */}
            <p>This is a website for reading internet speed test results, 
                data from various internet speed test sites, 
                and for creating your own speed test results, 
                based on your geopgrahic location. <br/>
                Below you can explore the different tools available on this website. <br/>
                Project created by Andraž, David and Domen.
                </p>
        </div>
        <div class="homePageSubLayout">
            <h3 className="homePageSubtitle">Explore the tools available on this website</h3>
            <div class="mainContentLayout">
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