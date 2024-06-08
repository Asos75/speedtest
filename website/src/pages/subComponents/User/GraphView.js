import React from 'react';
import { Line, Bar } from 'react-chartjs-2';
import 'chartjs-adapter-date-fns';

const GraphView = ({ measurements }) => {
  const speedData = measurements.map(m => ({ x: new Date(m.time), y: m.speed / 1024 / 1024 }));
  const timeData = measurements.map(m => ({ x: new Date(m.time), y: new Date(m.time).getTime() }));

  const lineOptions = {
    scales: {
      x: {
        type: 'time',
        time: {
          unit: 'day'
        }
      },
      y: {
        title: {
          display: true,
          text: 'Speed (MB/s)'
        }
      }
    }
  };

  const barOptions = {
    scales: {
      x: {
        type: 'time',
        time: {
          unit: 'day'
        }
      },
      y: {
        title: {
          display: true,
          text: 'Count'
        }
      }
    }
  };

  return (
    <div className="graphViewContainer">
      <h4>Speed Over Time</h4>
      <Line
        data={{
          datasets: [{
            label: 'Speed (MB/s)',
            data: speedData,
            borderColor: 'blue',
            backgroundColor: 'rgba(0, 0, 255, 0.1)',
            fill: true,
          }]
        }}
        options={lineOptions}
      />
      <h4>Measurements Over Time</h4>
      <Bar
        data={{
          datasets: [{
            label: 'Measurements Count',
            data: timeData,
            backgroundColor: 'green',
          }]
        }}
        options={barOptions}
      />
    </div>
  );
};

export default GraphView;
