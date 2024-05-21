// Dependencies
import React, { useState, useEffect } from 'react';

// SpeedTest Component
const SpeedTest = ({ setSpeed }) => {
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    // Function to load the speedtest script
    const loadScript = (src) => {
      return new Promise((resolve, reject) => {
        const script = document.createElement('script');
        script.src = src;
        script.async = true;
        script.onload = resolve;
        script.onerror = reject;
        document.body.appendChild(script);
      });
    };

    // Load the speedtest script
    loadScript('https://cdn.jsdelivr.net/npm/speedtest-js')
      .then(() => {
        console.log('Speedtest script loaded successfully');
      })
      .catch((error) => {
        console.error('Error loading the speedtest script:', error);
      });
  }, []);

  const measureSpeed = () => {
    setLoading(true);
    const test = new window.Speedtest();

    test.onupdate = (data) => {
      if (data.testState === 4) { // 4 indicates test completed
        setLoading(false);
        setSpeed(data.dlStatus); // Download speed in Mbps
      }
    };

    test.start();
  };

  return (
    <div className="speedTest">
      <button onClick={measureSpeed} disabled={loading}>
        {loading ? 'Measuring...' : 'Measure Speed'}
      </button>
    </div>
  );
};

export default SpeedTest;
