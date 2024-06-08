import React, { useMemo, useState, useEffect } from 'react';
import axios from 'axios';
import { Select, MenuItem } from '@material-ui/core';
import { formatTime } from '../../../helpers/helperFunction';

const fetchEvents = async () => {
  try {
    const response = await axios.get(`${process.env.REACT_APP_BACKEND_URL}/event`);
    return response.data;
  } catch (error) {
    console.error('Error fetching events:', error);
    return [];
  }
};

const HeatmapSettings = ({
  measurements, setLayout, heatmapType, setHeatmapType, selectedArea, setSelectedArea,
  selectedEvent, setSelectedEvent, timeRange, setTimeRange, eventTime, setEventTime,
  setStartDate, setEndDate
}) => {
  const [events, setEvents] = useState([]);

  useEffect(() => {
    const getEvents = async () => {
      const eventList = await fetchEvents();
      setEvents(eventList);
    };
    getEvents();
  }, []);

  const eventOptions = useMemo(() => events.map(event => ({
    label: event.name,
    value: event._id,
    time: event.time,
  })), [events]);

  const speeds = measurements.map(measurement => measurement.speed);
  const times = measurements.map(measurement => Date.parse(measurement.time));

  const stats = useMemo(() => {
    const minSpeed = Math.min(...speeds);
    const avgSpeed = speeds.reduce((a, b) => a + b, 0) / speeds.length;
    const maxSpeed = Math.max(...speeds);
    const minTime = formatTime(new Date(Math.min(...times)));
    const avgTime = formatTime(new Date(times.reduce((a, b) => a + b, 0) / times.length));
    const maxTime = formatTime(new Date(Math.max(...times)));
    const totalResults = speeds.length;

    return {
      minSpeed, avgSpeed, maxSpeed, minTime, avgTime, maxTime, totalResults,
    };
  }, [measurements]);

  useEffect(() => {
    if (selectedEvent !== 'none') {
      const date = new Date(eventTime);
      const formattedDate = date.toISOString().split('T')[0];
      setStartDate(formattedDate);
      const endDate = new Date(date.getTime() + (timeRange * 24 * 60 * 60 * 1000));
      const formattedEndDate = endDate.toISOString().split('T')[0];
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
          value={selectedEvent}
          onChange={(e) => {
            setSelectedEvent(e.target.value);
            const selectedOption = eventOptions.find(option => option.value === e.target.value);
            if (selectedOption) setEventTime(selectedOption.time);
          }}
          className="eventSelect"
          style={{ fontSize: '20px' }}
        >
          <MenuItem value="none">None</MenuItem>
          {eventOptions.map(option => (
            <MenuItem key={option.value} value={option.value}>
              {option.label}
            </MenuItem>
          ))}
        </Select>
      </div>
      {selectedEvent !== 'none' && (
        <div className="heatmapFilterLayout">
          <p>Measurement time range</p>
          <Select
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
      )}
      <div className="heatmapFilterLayout">
        <p>Filter mode</p>
        <Select
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
          value={selectedArea}
          onChange={(e) => setSelectedArea(e.target.value)}
          className="areaSizeSelect"
          style={{ fontSize: '20px' }}
        >
          <MenuItem value="0.0025">25m²</MenuItem>
          <MenuItem value="0.0050">50m²</MenuItem>
          <MenuItem value="0.0100">100m²</MenuItem>
          <MenuItem value="0.0250">250m²</MenuItem>
        </Select>
      </div>
      <div className="heatmapStatisticsLayout">
        <h3 className="heatmapStatisticsTitle">Statistics</h3>
        <p>Minimum speed: <b>{(stats.minSpeed / 1e6).toFixed(2)}</b> Mb/s</p>
        <p>Average speed: <b>{(stats.avgSpeed / 1e6).toFixed(2)}</b> Mb/s</p>
        <p>Maximum speed: <b>{(stats.maxSpeed / 1e6).toFixed(2)}</b> Mb/s</p>
        <p>Earliest time: <b>{stats.minTime}</b></p>
        <p>Average time: <b>{stats.avgTime}</b></p>
        <p>Latest time: <b>{stats.maxTime}</b></p>
        <p>Total results: <b>{stats.totalResults}</b></p>
      </div>
    </div>
  );
};

export default HeatmapSettings;
