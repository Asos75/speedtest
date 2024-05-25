// Dependencies
import React, { useState, useEffect} from 'react';

// UI
import { Select, MenuItem, Slider } from '@material-ui/core';

const HeatmapSettings = ({ measurements, setLayout, heatmapType, setHeatmapType, maxIntensity, setMaxIntensity, radius, setRadius, blur, setBlur}) => {
  // States of values / results
  const [minValue, setMinValue] = useState(0);
  const [avgValue, setAvgValue] = useState(0);
  const [maxValue, setMaxValue] = useState(0);
  const [totalResults, setTotalResults] = useState(0);

  // Extract time, speed, and coordinates from measurements
  const speeds = measurements.map(measurement => measurement.speed);

  useEffect(() => {
    setMinValue(Math.min(...speeds));
    setAvgValue(speeds.reduce((a, b) => a + b, 0) / speeds.length);
    setMaxValue(Math.max(...speeds));
    setTotalResults(speeds.length);
  // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [heatmapType, measurements]);

  const legendItems = [
    { range: '>20000', label: 'Really low', color: '#f0f0f0' },
    { range: '20000-45000', label: 'Low', color: '#bdbdbd' },
    { range: '45000-75000', label: 'Medium', color: '#636363' },
    { range: '75000-100000', label: 'High', color: '#252525' },
    { range: '>100000', label: 'Really high', color: '#000000' },
  ];
  
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
      <div className="heatmapIntensitySlider" style={{ width: '50%' }}>
        <Slider
          value={maxIntensity}
          onChange={(e, newValue) => setMaxIntensity(newValue)}
          min={1}
          max={100}
          step={1}
        />
        <Slider
          value={radius}
          onChange={(e, newValue) => setRadius(newValue)}
          min={1}
          max={50}
          step={1} />
        <Slider
          value={blur}
          onChange={(e, newValue) => setBlur(newValue)}
          min={1}
          max={50}
          step={1} />
      </div>
      <div>
        <p>Min {heatmapType}: {minValue}</p>
        <p>Avg {heatmapType}: {avgValue}</p>
        <p>Max {heatmapType}: {maxValue}</p>
        <p>Total results: {totalResults}</p>
      </div>
    </div>
  );
};

export default HeatmapSettings;