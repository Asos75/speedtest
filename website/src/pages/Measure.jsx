// Dependencies
import React, { useState, useEffect } from 'react';
import axios from 'axios';
import SpeedTest from './subComponents/SpeedTest';

// Styles
import '../styles/Components/Measure.css';

// Measure Component
const Measure = ({ measure, index, selectedMeasure, setSelectedMeasure }) => {
  const speed = measure.speed;
  const location = measure.location;
  const isp = measure.isp;

  return (
    <div key={index} className={`measure ${selectedMeasure === index ? 'highlight' : ''}`} onClick={() => setSelectedMeasure(index)}>
      <p>Speed: <b>{speed}</b> | Location: <b>{location}</b> | ISP: <b>{isp}</b></p>
    </div>
  );
};

// MeasureList Component
function MeasureList() {
  const [measures, setMeasures] = useState([]);
  const [selectedMeasure, setSelectedMeasure] = useState(null);
  const [userSpeed, setUserSpeed] = useState(null);

  useEffect(() => {
    axios.get('https://your-api-url.com/measures')
      .then(response => {
        setMeasures(response.data);
      })
      .catch(error => {
        console.error('There was an error!', error);
      });
  }, []);

  return (
    <>
      <h1 className="measureTitle">Measure Page - TODO</h1>
      <div className="measureLayout">
        <div className="measureList">
          <h2>Measure list</h2>
          {measures.map((measure, index) => (
            <Measure
              key={index}
              measure={measure}
              index={index}
              selectedMeasure={selectedMeasure}
              setSelectedMeasure={setSelectedMeasure}
            />
          ))}
        </div>
        <div className="userSpeed">
          <h2>Your Internet Speed</h2>
          <SpeedTest setSpeed={setUserSpeed} />
          {userSpeed && <p>Your speed: <b>{userSpeed} Mbps</b></p>}
        </div>
      </div>
    </>
  );
}

export default MeasureList;
