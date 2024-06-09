import React, { useState } from 'react';
import { ThreeDots } from 'react-loader-spinner';
import '../styles/Components/Measure.css';

// Function to get user location
const getLocation = async () => {
  return new Promise((resolve, reject) => {
    if ('geolocation' in navigator) {
      navigator.geolocation.getCurrentPosition(
        (position) => {
          const lat = position.coords.latitude;
          const lng = position.coords.longitude;
          resolve([lat, lng]);
        },
        (error) => {
          console.error('Error getting user location:', error);
          reject(error);
        }
      );
    } else {
      const error = new Error('Geolocation is not supported by this browser.');
      console.error(error.message);
      reject(error);
    }
  });
};

// Function to get IP info
const getIPInfo = async () => {
  try {
    const ipResponse = await fetch('https://api.ipify.org?format=json');
    const ipData = await ipResponse.json();
    const ispResponse = await fetch(`https://ipinfo.io/${ipData.ip}?token=${process.env.REACT_APP_IPINFO_TOKEN}`);
    return ispResponse.json();
  } catch (error) {
    console.error('Error:', error);
    return 'ISP information could not be retrieved';
  }
};

// Speed test related constants
const downloadSize = 5616998; // Size of the image used for speed test
const imageAddr = "http://wallpaperswide.com/download/shadow_of_the_tomb_raider_2018_puzzle_video_game-wallpaper-7680x4800.jpg";
const bytesInAKilobyte = 1024;
const roundedDecimals = 2;

const downloadImg = async () => {
  return new Promise((resolve, reject) => {
    const download = new Image();
    download.onload = () => {
      const endTime = new Date().getTime();
      resolve(endTime - startTime);
    };
    download.onerror = (err) => {
      reject(err);
    };
    const startTime = new Date().getTime();
    download.src = `${imageAddr}?cacheBuster=${startTime}`;
  });
};

const speedtest = async (duration, setCurrentSpeed) => {
  const endTime = Date.now() + duration * 1000;
  const results = [];
  const interval = 0.5 * 1000; // 0.5 seconds interval

  while (Date.now() < endTime) {
    try {
      const time = await downloadImg();
      const speedBps = (downloadSize * 8) / (time / 1000);
      const speedMbps = (speedBps / bytesInAKilobyte / bytesInAKilobyte).toFixed(roundedDecimals);
      results.push(parseFloat(speedMbps));
      setCurrentSpeed(speedMbps);
    } catch (error) {
      console.error('Error during speed test:', error);
      break;
    }
    await new Promise((resolve) => setTimeout(resolve, interval));
  }

  const avgSpeed = (results.reduce((a, b) => a + b, 0) / results.length).toFixed(roundedDecimals);
  return avgSpeed;
};

const MeasureList = ({ username }) => {
  const [userSpeed, setUserSpeed] = useState(null);
  const [currentSpeed, setCurrentSpeed] = useState(null);
  const [userLocation, setUserLocation] = useState(null);
  const [userISP, setUserISP] = useState(null);
  const [loading, setLoading] = useState({ speed: false, location: false, isp: false });
  const [showMoreInfo, setShowMoreInfo] = useState(false);

  const handleMoreInfoClick = () => {
    setShowMoreInfo(!showMoreInfo);
  };

  const fetchUserDetails = async () => {
    setLoading({ speed: true, location: true, isp: true });

    // Remove all the previous data
    setUserSpeed(null);
    setCurrentSpeed(null);
    setUserLocation(null);
    setUserISP(null);

    try {
      const [avgSpeed, location, isp] = await Promise.all([
        speedtest(10, setCurrentSpeed), // Run speed test for 10 seconds
        getLocation(),
        getIPInfo()
      ]);
      setUserSpeed(avgSpeed); // Set the final average speed
      setUserLocation(location.join(', '));
      //console.log("ISP: ", isp);
      setUserISP(isp);
    } catch (error) {
      console.error('Error fetching user details:', error);
    } finally {
      setLoading({ speed: false, location: false, isp: false });
    }
  };

  const addMeasurement = async (anonymous) => {
    const connectionType = navigator.connection?.type === 'wifi' ? 'wifi' : 'data';
    const userId = localStorage.getItem('id');
    const measurement = {
      speed: Math.floor(userSpeed * bytesInAKilobyte * bytesInAKilobyte),
      type: connectionType,
      provider: userISP.company.name,
      time: new Date(),
      location: {
        type: 'Point',
        coordinates: userLocation.split(', ').map(Number).reverse() // Reverse the coordinates
      },
      ...(anonymous ? {} : { measuredBy: userId })
    };

    try {
      const response = await fetch(`${process.env.REACT_APP_BACKEND_URL}/measurements`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify(measurement)
      });

      if (response.ok) {
        // Reset values
        setUserSpeed(null);
        setCurrentSpeed(null);
        setUserLocation(null);
        setUserISP(null);
      } else {
        console.error('Error adding measurement');
      }
    } catch (error) {
      console.error('Error:', error);
    }
  };

  return (
    <>
      <h1 className="measureTitle">Measure Page</h1>
      <div className="measureLayout">
        <div className="userDetails">
          <h2>Your Internet Speed</h2>
          {loading.speed ? (
            <div className="measureLoadingContainer">
              <p>Current Speed: {currentSpeed} Mbps</p>
              <div className="loading-bar">
                <div className="loading-bar-inner"></div>
              </div>
            </div>
          ) : (
            userSpeed ? <p>Your speed: <b>{userSpeed} Mbps</b></p> : <p><b>No speed currently available</b></p>
          )}
          <h2>Your GPS Location</h2>
          {loading.location ? (
            <div className="measureLoadingContainer">
              <p>Loading location...</p>
              <ThreeDots color="#00BFFF" height={80} width={80} />
            </div>
          ) : (
            userLocation ? <p>Your GPS location: <b>{userLocation}</b></p> : <p><b>No location currently available</b></p>
          )}
          <h2>Your ISP Information</h2>
          {loading.isp ? (
            <div className="measureLoadingContainer">
              <p>Loading ISP...</p>
              <ThreeDots color="#00BFFF" height={80} width={80} />
            </div>
          ) : (
            <>
              <button onClick={handleMoreInfoClick} className="measureButton">
                {showMoreInfo ? 'Hide ISP info' : 'Info about ISP'}
              </button>
              {showMoreInfo && typeof userISP === 'object' && userISP !== null && (
                <div>
                  <p>IP: <b>{userISP.ip}</b></p>
                  <p>City: <b>{userISP.city}</b></p>
                  <p>Region: <b>{userISP.region}</b></p>
                  <p>Country: <b>{userISP.country}</b></p>
                  <p>Location: <b>{userISP.loc}</b></p>
                  <p>Postal: <b>{userISP.postal}</b></p>
                  <p>Timezone: <b>{userISP.timezone}</b></p>
                </div>
              )}
            </>
          )}
          {loading.location && <p className="measureLoadingText">This depends on your internet speed</p>}
          <button className="measureButton buttonRed" onClick={fetchUserDetails} disabled={loading.speed || loading.location || loading.isp}>
            {(loading.speed || loading.location || loading.isp) ? 'Measuring...' : 'Measure Speed'}
          </button>
          {(userSpeed && userLocation && userISP) && 
          (<div className="addMeasurementLayout">
            <h3>Add your measurement</h3>
            {username ? (
              <div className="addMeasurementButtons">
                <button className="measureButton" onClick={() => addMeasurement(false)}>Add Measurement as <b>{username}</b></button>
                <button className="measureButton" onClick={() => addMeasurement(true)}>Add Measurement anonymously</button>
              </div>
            ) : (
              <>
                <p>You need to be logged in to add a measurement</p>
                <a href="/login" className="measureButton buttonRed">Login</a>
              </>
            )}
          
          </div>)} 
        </div>
      </div>
    </>
  );
};

export default MeasureList;
