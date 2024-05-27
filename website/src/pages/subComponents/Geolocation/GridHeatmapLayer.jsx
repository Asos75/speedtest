import React, { useEffect, useState, useMemo, useRef } from 'react';
import { useMapEvents } from 'react-leaflet';
import L from 'leaflet';
import { Rectangle, Popup } from 'react-leaflet';
// import { debounce } from 'lodash';

// Calculate color based on heatmap type
const calculateColor = (data, heatmapType) => {
  let value;
  if (heatmapType === 'speed') {
    value = data.speedSum / data.count;
  } else if (heatmapType === 'time') {
    value = data.creationDateSum / data.count; // calculate average creation date
  }
  return getColor(value, heatmapType);
};

// Calculate grid data for the heatmap
const calculateGridData = (measurements, heatmapType, map, cellSize, grid) => {
  const bounds = map.getBounds();
  const measurementsInView = measurements.filter(({ location }) => bounds.contains(L.latLng(location.coordinates[1], location.coordinates[0])));

  measurementsInView.forEach(({ location, speed, time }) => {
    const lat = Math.floor(location.coordinates[1] / cellSize);
    const lng = Math.floor(location.coordinates[0] / cellSize);
    const key = `${lat},${lng}`;

    if (!grid.current[key]) {
      grid.current[key] = { count: 0, sum: 0, speedSum: 0, creationDateSum: 0 };
    }
    
    grid.current[key].creationDateSum += new Date(time).getTime();
    grid.current[key].count += 1;
    grid.current[key].sum += heatmapType === 'speed' ? speed : new Date(time).getTime();
    grid.current[key].speedSum += speed;
  });

  return Object.entries(grid.current).map(([key, data]) => {
    const [lat, lng] = key.split(',').map(Number);
    return {
      lat: lat * cellSize,
      lng: lng * cellSize,
      data
    };
  });
};

// Get color based on value and heatmap type
const getColor = (value, heatmapType) => {
  if (heatmapType === 'speed') {
    if (value < 25000) return 'lightblue';
    if (value < 35000) return 'darkblue';
    if (value < 50000) return 'green';
    if (value < 75000) return 'yellow';
    if (value < 100000) return 'orange';
    return 'red';
  } else if (heatmapType === 'time') {
    const monthInMilliseconds = 30 * 24 * 60 * 60 * 1000;
    const timeDifference = new Date().getTime() - value;
    if (timeDifference < monthInMilliseconds) return 'green';
    if (timeDifference < 3 * monthInMilliseconds) return 'yellow';
    if (timeDifference < 6 * monthInMilliseconds) return 'orange';
    return 'red';
  }
};

const GridHeatmapLayer = ({ measurements, heatmapType, setLoading, selectedArea }) => {
  const [gridData, setGridData] = useState([]);
  // Disabled zooming for now
  // const [zoomLevel, setZoomLevel] = useState(13);
  const [loading, setIsLoading] = useState(false);

  // Map events to handle zoom changes
  const map = useMapEvents({
    /*zoomend: debounce((e) => { // Debounce the zoomend event
      const newZoom = e.target.getZoom();
      setZoomLevel(newZoom);
    }, 250),*/
  });

  const cellSize = Number(selectedArea); // Use selectedArea as cell size
  const grid = useRef({});

  // Memoize the grid data calculation
  const gridDataMemo = useMemo(() => {
    return calculateGridData(measurements, heatmapType, map, cellSize, grid);
  }, [heatmapType, measurements, map, cellSize]);

  // Update grid data when dependencies change
useEffect(() => {
  if (heatmapType) {
    setIsLoading(true); // Set loading to true at the start of the effect
    setLoading(true);
    grid.current = {};
    setGridData(gridDataMemo);
    setIsLoading(false); // Set loading to false after the data has been set
    setLoading(false);
  }
}, [heatmapType, measurements, map, cellSize, gridDataMemo, selectedArea]);

  // If loading, return a loading spinner
  if (loading) {
    return <div>Loading...</div>;
  }

  return (
    <>
      {gridData.map(({ lat, lng, data }, index) => {
        const color = calculateColor(data, heatmapType);
        const latitude = Number(lat);
        const longitude = Number(lng);
        const averageCreationDate = new Date(data.creationDateSum / data.count);
        const daysDifference = Math.floor((new Date().getTime() - averageCreationDate.getTime()) / (1000 * 60 * 60 * 24));

        return (
          <Rectangle
            key={`${index}-${heatmapType}`} // Add heatmapType to the key
            bounds={[
              [latitude, longitude],
              [latitude + cellSize, longitude + cellSize]
            ]}
            color={color}
          >
            <Popup>
              <span>Speed: {Math.floor(data.speedSum / data.count)}</span><br/>
              <span>Average Creation Date: {averageCreationDate.toLocaleString()}</span><br/>
              <span>Days since Average Creation Date: {daysDifference}</span>
            </Popup>
          </Rectangle>
        );
      })}
    </>
  ); 
};

export default GridHeatmapLayer;