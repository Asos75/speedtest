// Dependencies
import React, { useState } from 'react';

// Helpers
import { formatTime } from '../../../helpers/helperFunction';

const Measurement = ({ measurement, index }) => {
  // Show details for each measurement
  const [showDetails, setShowDetails] = useState(false);
  // Coordinates are stored in reverse order
  const coordinates = [
    measurement.location.coordinates[1],
    measurement.location.coordinates[0]
  ];

  // Check if coordinates are valid
  if (coordinates.some(isNaN)) {
    console.error(`Invalid coordinates for measurement at index ${index}:`, coordinates);
    return null;
  }

  // Calculate speed in MB/s
  const speedInMBps = measurement.speed ? (measurement.speed / (1024 * 1024)).toFixed(2) : 'Currently unavailable';

  return (
    <div key={index} className="measurement">
      <p><b>{measurement.type}</b> | Coordinates: <b>{coordinates.join(', ')}</b></p>
      {showDetails && (
        <>
          <p>Speed: <b>{speedInMBps} MB/s</b></p>
          {measurement.provider && (<p>Provider: <b>{measurement.provider}</b></p>)}
          {measurement.time && (<p>Time: <b>{formatTime(measurement.time)}</b></p>)}
        </>
      )}
      <button onClick={() => setShowDetails(!showDetails)}>{!showDetails ? "Show More" : "Show less"}</button>
    </div>
  );
};

export default Measurement;