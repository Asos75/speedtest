import React, { useState, useEffect } from 'react';
import { Pie } from 'react-chartjs-2';
import axios from 'axios';

const AdminContainer = () => {
  const [graphType, setGraphType] = useState('events');
  const [events, setEvents] = useState([]);
  const [towers, setTowers] = useState([]);
  const [measurements, setMeasurements] = useState([]);

  useEffect(() => {
    axios.get(`${process.env.REACT_APP_BACKEND_URL}/event`)
      .then(response => {
        setEvents(response.data);
      })
      .catch(error => {
        console.error('There was an error!', error);
      });

    axios.get(`${process.env.REACT_APP_BACKEND_URL}/mobile`)
      .then(response => {
        setTowers(response.data);
      })
      .catch(error => {
        console.error('There was an error!', error);
      });

    axios.get(`${process.env.REACT_APP_BACKEND_URL}/measurements`)
      .then(response => {
        setMeasurements(response.data);
      })
      .catch(error => {
        console.error('There was an error!', error);
      });
  }, []);

  const handleGraphTypeChange = (event) => {
    setGraphType(event.target.value);
  };

  const getEventsData = () => {
    const currentDateTime = new Date();
    const eventsHappened = events.filter(event => new Date(event.time) <= currentDateTime).length;
    const eventsNotHappened = events.length - eventsHappened;
  
    return {
      labels: ['Events Happened', 'Events Not Happened'],
      datasets: [{
        data: [eventsHappened, eventsNotHappened],
        backgroundColor: ['rgba(75,192,192,0.4)', 'rgba(192,75,75,0.4)'],
      }],
    };
  };

  const getOnlineData = () => {
    const onlineEvents = events.filter(event => event.online).length;
    const offlineEvents = events.length - onlineEvents;

    return {
      labels: ['Online Events', 'Offline Events'],
      datasets: [{
        data: [onlineEvents, offlineEvents],
        backgroundColor: ['rgba(75,192,192,0.4)', 'rgba(192,75,75,0.4)'],
      }],
    };
  };

  const getConfirmedTowersData = () => {
    const confirmedTowers = towers.filter(tower => tower.confirmed).length;
    const unconfirmedTowers = towers.length - confirmedTowers;

    return {
      labels: ['Confirmed Towers', 'Unconfirmed Towers'],
      datasets: [{
        data: [confirmedTowers, unconfirmedTowers],
        backgroundColor: ['rgba(75,192,192,0.4)', 'rgba(192,75,75,0.4)'],
      }],
    };
  };

  const getSpeedData = () => {
    const speedCategories = measurements.reduce((categories, measurement) => {
      const speed = measurement.speed;
      if (speed < 25000) categories['< 25,000']++;
      else if (speed < 35000) categories['25,000 - 34,999']++;
      else if (speed < 50000) categories['35,000 - 49,999']++;
      else if (speed < 75000) categories['50,000 - 74,999']++;
      else if (speed < 100000) categories['75,000 - 99,999']++;
      return categories;
    }, { '< 25,000': 0, '25,000 - 34,999': 0, '35,000 - 49,999': 0, '50,000 - 74,999': 0, '75,000 - 99,999': 0 });

    return {
      labels: Object.keys(speedCategories),
      datasets: [{
        data: Object.values(speedCategories),
        backgroundColor: ['lightblue', 'darkblue', 'green', 'yellow', 'orange'],
      }],
    };
  };

  const getTimeData = () => {
    const oneMonthAgo = new Date();
    oneMonthAgo.setMonth(oneMonthAgo.getMonth() - 1);
    const twoMonthsAgo = new Date();
    twoMonthsAgo.setMonth(twoMonthsAgo.getMonth() - 2);

    const timeCategories = measurements.reduce((categories, measurement) => {
      const time = new Date(measurement.time);
      if (time >= oneMonthAgo) categories['Within Last Month']++;
      else if (time >= twoMonthsAgo) categories['1-2 Months Ago']++;
      else categories['More than 2 Months Ago']++;
      return categories;
    }, { 'Within Last Month': 0, '1-2 Months Ago': 0, 'More than 2 Months Ago': 0 });

    return {
      labels: Object.keys(timeCategories),
      datasets: [{
        data: Object.values(timeCategories),
        backgroundColor: ['green', 'darkgreen', 'red'],
      }],
    };
  };

  const getTypeData = () => {
    const typeCategories = measurements.reduce((categories, measurement) => {
      categories[measurement.type]++;
      return categories;
    }, { 'wifi': 0, 'data': 0 });

    return {
      labels: Object.keys(typeCategories),
      datasets: [{
        data: Object.values(typeCategories),
        backgroundColor: ['blue', 'red'],
      }],
    };
  };

  const renderGraph = () => {
    switch (graphType) {
      case 'events':
        return <Pie data={getEventsData()} />;
      case 'online':
        return <Pie data={getOnlineData()} />;
      case 'towers':
        return <Pie data={getConfirmedTowersData()} />;
      case 'speed':
        return <Pie data={getSpeedData()} />;
      case 'time':
        return <Pie data={getTimeData()} />;
      case 'type':
        return <Pie data={getTypeData()} />;
      default:
        return null;
    }
  };

  return (
    <div className="adminContainer">
      <h3>Admin Dashboard</h3>
      <div className="graphTabs">
        <label htmlFor="graphType">Select Graph Type:</label>
        <select id="graphType" onChange={handleGraphTypeChange} value={graphType}>
          <option value="events">Event Availability</option>
          <option value="online">Event Status</option>
          <option value="towers">Confirmed Towers</option>
          <option value="speed">Speed</option>
          <option value="time">Time</option>
          <option value="type">Type</option>
        </select>
      </div>
      <div className="graphContainer">
        {renderGraph()}
      </div>
      <div>
        {graphType === 'events' && <p>All events: {events.length}</p>}
        {graphType === 'online' && <p>All events: {events.length}</p>}
        {graphType === 'towers' && <p>All towers: {towers.length}</p>}
        {graphType === 'speed' && <p>All measurements: {measurements.length}</p>}
        {graphType === 'time' && <p>All measurements: {measurements.length}</p>}
        {graphType === 'type' && <p>All measurements: {measurements.length}</p>}
      </div>
    </div>
  );
};

export default AdminContainer;
