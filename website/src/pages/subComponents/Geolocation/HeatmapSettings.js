// Dependencies
import React, { useState, useEffect, useMemo } from 'react';

// Material UI
import { Select, MenuItem } from '@material-ui/core';

// Helpers
import { formatTime } from '../../../helpers/helperFunction';

const HeatmapSettings = ({ measurements, setLayout, heatmapType, setHeatmapType, selectedArea, setSelectedArea}) => {
  const speeds = measurements.map(measurement => measurement.speed);
  const times = measurements.map(measurement => Date.parse(measurement.time));

  const {minSpeed, avgSpeed, maxSpeed, minTime, avgTime, maxTime, totalResults } = useMemo(() => {
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
  }, [heatmapType, measurements]);

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
      <div>
        <p>Min speed: {minSpeed}</p>
        <p>Avg speed: {avgSpeed}</p>
        <p>Max speed: {maxSpeed}</p>
        <p>Min time: {minTime}</p>
        <p>Avg time: {avgTime}</p>
        <p>Max time: {maxTime}</p>
        <p>Total results: {totalResults}</p>
      </div>
    </div>
  );
};

export default HeatmapSettings;