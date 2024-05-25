import React from 'react';
import { Select, MenuItem, Slider } from '@material-ui/core';

const HeatmapSettings = ({ setLayout, heatmapType, setHeatmapType, maxIntensity, setMaxIntensity, radius, setRadius, blur, setBlur }) => {
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
    </div>
  );
};

export default HeatmapSettings;