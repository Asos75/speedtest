// Dependencies
import React, { useMemo, useState, useEffect } from 'react';
import axios from 'axios';

// Material UI
import { Select, MenuItem } from '@material-ui/core';

// Helpers
import { formatTime } from '../../../helpers/helperFunction';

// Fetch Events Function
const fetchEvents = async () => {
  try {
    const response = await axios.get(`${process.env.REACT_APP_BACKEND_URL}/event`);
    return response.data;
  } catch (error) {
    console.error('Error fetching events:', error);
    return [];
  }
};

const HeatmapSettings = ({ measurements, setLayout, heatmapType, setHeatmapType, selectedArea, setSelectedArea, selectedEvent, setSelectedEvent, timeRange, setTimeRange, eventTime, setEventTime, setStartDate, setEndDate }) => {
  const [events, setEvents] = useState([]);

  useEffect(() => {
    const getEvents = async () => {
      const eventList = await fetchEvents();
      setEvents(eventList);
    };
    getEvents();
  }, []);

  const eventOptions = events.map(event => ({ 
    label: event.name, 
    value: event._id,
    time: event.time
  }));

  const speeds = measurements.map(measurement => measurement.speed);
  const times = measurements.map(measurement => Date.parse(measurement.time));

  const { minSpeed, avgSpeed, maxSpeed, minTime, avgTime, maxTime, totalResults } = useMemo(() => {
    const minSpeed = Math.min(...speeds);
    const avgSpeed = speeds.reduce((a, b) => a + b, 0) / speeds.length;
    const maxSpeed = Math.max(...speeds);
    const minTime = formatTime(new Date(Math.min(...times)));
    const avgTime = formatTime(new Date(times.reduce((a, b) => a + b, 0) / times.length));
    const maxTime = formatTime(new Date(Math.max(...times)));
    const totalResults = speeds.length;

    return {
      minSpeed,
      avgSpeed,
      maxSpeed,
      minTime,
      avgTime,
      maxTime,
      totalResults
    };
  // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [heatmapType, measurements]);

  useEffect(() => {
    if (selectedEvent !== 'none') {
      // Start date
      const date = new Date(eventTime);
      const formattedDate = `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`;
      setStartDate(formattedDate);
      // End 
      const endDate = new Date(date.getTime() + (timeRange * 24 * 60 * 60 * 1000));
      const formattedEndDate = `${endDate.getFullYear()}-${String(endDate.getMonth() + 1).padStart(2, '0')}-${String(endDate.getDate()).padStart(2, '0')}`;
      setEndDate(formattedEndDate);
    }
  }, [eventTime, selectedEvent, timeRange, setStartDate, setEndDate]);

  return (
    <div className="measurementContainer">
      <div className="measurementHeaderContainer">
        <h2 className="measurementTitle">Heatmap settings</h2>
        <div className="measurementButtonsContainer">
          <button onClick={() => setLayout('points')}>
            <span role="img" aria-label="pin">📌</span>
          </button>
          <button onClick={() => setLayout('grid')}>
            <span role="img" aria-label="grid">🔳</span>
          </button>
        </div>
      </div>
      <hr className="measurementDivider" />
      <div className="heatmapFilterLayout">
        <p>Choose an event</p>
        <Select
          labelId="event-select-label"
          value={selectedEvent}
          onChange={(e) => {
            setSelectedEvent(e.target.value);
            const selectedOption = eventOptions.find(option => option.value === e.target.value);
            if (selectedOption) {
              setEventTime(selectedOption.time);
            }
          }}
          className="eventSelect"
          style={{ fontSize: '20px' }}
        >
          <MenuItem value="none">None</MenuItem>
          {eventOptions.map(option => (
            <MenuItem key={option.value} value={option.value} time={option.time}>{option.label}</MenuItem>
          ))}
        </Select>
      </div>
      {selectedEvent !== 'none' && 
      <div className="heatmapFilterLayout">
        <p>Measurement time range</p>
        <Select
          labelId="time-range-select-label"
          value={timeRange}
          onChange={(e) => setTimeRange(e.target.value)}
          className="timeRangeSelect"
          style={{ fontSize: '20px' }}
        >
          <MenuItem value="1">1 day</MenuItem>
          <MenuItem value="2">2 days</MenuItem>
          <MenuItem value="5">5 days</MenuItem>
        </Select>
      </div>
      }
      <div className="heatmapFilterLayout">
        <p>Filter mode</p>
        <Select
          labelId="heatmap-type-label"
          value={heatmapType}
          onChange={(e) => setHeatmapType(e.target.value)}
          className="heatmapTypeSelect"
          style={{ fontSize: '20px' }}
        >
          <MenuItem value="speed">Speed</MenuItem>
          <MenuItem value="time">Time</MenuItem>
        </Select>
      </div>
      <div className="heatmapSizeLayout">
        <p>Grid sizes</p>
        <Select
          labelId="area-size-label"
          value={selectedArea}
          onChange={(e) => setSelectedArea(e.target.value)}
          className="areaSizeSelect"
          style={{ fontSize: '20px' }}
        >
          <MenuItem value="0.0025">25m2</MenuItem>
          <MenuItem value="0.0050">50m2</MenuItem>
          <MenuItem value="0.0100">100m2</MenuItem>
          <MenuItem value="0.0250">250m2</MenuItem>
        </Select>
      </div>
      <div className="heatmapStatisticsLayout">
        <h3 className="heatmapStatisticsTitle">Statistics</h3>
        <p>Minimum speed: <b>{parseInt(minSpeed)}</b></p>
        <p>Avarage speed: <b>{parseInt(avgSpeed)}</b></p>
        <p>Maximum speed: <b>{parseInt(maxSpeed)}</b></p>
        <p>Earliest time: <b>{minTime}</b></p>
        <p>Avarage time: <b>{avgTime}</b></p>
        <p>Latest time: <b>{maxTime}</b></p>
        <p>Total results: <b>{totalResults}</b></p>
      </div>
    </div>
  );
};

export default HeatmapSettings;
