// Dependencies
import React, { useEffect, useState, useMemo, useRef } from 'react';
import { useMapEvents } from 'react-leaflet';
import L from 'leaflet';
import { Rectangle, Popup } from 'react-leaflet';
import * as d3 from 'd3';

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
      grid.current[key] = { count: 0, sum: 0, speedSum: 0, creationDateSum: 0, speeds: [], times: [] };
    }
    
    grid.current[key].creationDateSum += new Date(time).getTime();
    grid.current[key].count += 1;
    grid.current[key].sum += heatmapType === 'speed' ? speed : new Date(time).getTime();
    grid.current[key].speedSum += speed;
    grid.current[key].speeds.push(speed);
    grid.current[key].times.push(new Date(time).getTime());
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

// Function to create histogram with count of measurements within each range
const createHistogram = (node, data) => {
  const width = 200;
  const height = 100;
  const margin = { top: 10, right: 10, bottom: 20, left: 30 };
  
  // Remove any existing content
  d3.select(node).selectAll('*').remove();

  // Create a custom tick formatter
  const formatTick = (d) => {
    if (d >= 1000) {
      return `${d / 1000}k`;
    }
    return d;
  };
  
  const svg = d3.select(node)
    .append('svg')
    .attr('width', width + margin.left + margin.right)
    .attr('height', height + margin.top + margin.bottom)
    .append('g')
    .attr('transform', `translate(${margin.left},${margin.top})`);
  
  const maxValue = d3.max(data);
  const x = d3.scaleLinear()
    .domain([0, maxValue])
    .nice()
    .range([0, width]);

  const ticks = 5; // Number of ticks
  const xTicks = d3.ticks(0, maxValue, ticks);
  
  const bins = d3.histogram()
    .domain(x.domain())
    .thresholds(xTicks) // Create bins based on xTicks
    (data);
  
  const y = d3.scaleLinear()
    .domain([0, d3.max(bins, d => d.length)])
    .nice()
    .range([height, 0]);

  const yTicks = d3.ticks(0, d3.max(bins, d => d.length), ticks);
  
  svg.append('g')
    .selectAll('rect')
    .data(bins)
    .enter().append('rect')
    .attr('x', d => x(d.x0) + 1)
    .attr('y', d => y(d.length))
    .attr('width', d => x(d.x1) - x(d.x0) - 1)
    .attr('height', d => height - y(d.length))
    .attr('fill', '#69b3a2');
  
    svg.append('g')
    .attr('transform', `translate(0,${height})`)
    .call(d3.axisBottom(x).tickValues(xTicks).tickFormat(formatTick)); // Tick formater
  
  svg.append('g')
    .call(d3.axisLeft(y).tickValues(yTicks));
};

const Histogram = ({ data }) => {
  const chartRef = useRef();
  
  useEffect(() => {
    if (chartRef.current) {
      createHistogram(chartRef.current, data);
    }
  }, [data]);
  
  return <div ref={chartRef}></div>;
};

const GridHeatmapLayer = ({ measurements, heatmapType, setLoading, selectedArea }) => {
  const [gridData, setGridData] = useState([]);
  const [loading, setIsLoading] = useState(false);
  
  const map = useMapEvents({});
  
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
  // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [heatmapType, measurements, map, cellSize, gridDataMemo, selectedArea]);
  
  // If loading, return that it is in fact, loading
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
        const daysDifference = Math.floor((new Date().getTime() - averageCreationDate.getTime()) / (1000 * 60 * 60 * 24) + 1);
        const chartData = heatmapType === 'speed' ? data.speeds : data.times;
        
        return (
          <Rectangle
            key={`${index}-${heatmapType}`}
            bounds={[
              [latitude, longitude],
              [latitude + cellSize, longitude + cellSize]
            ]}
            color={color}
            pathOptions={{ weight: 1 }}
          >
            <Popup>
              <span className="popup-text">Speed: <b>{Math.floor(data.speedSum / data.count)}</b></span><br/>
              <span className="popup-text">Average Creation Date: <b>{averageCreationDate.toLocaleString()}</b></span><br/>
              <span className="popup-text">Days since creation: <b>{daysDifference}</b></span>

              {heatmapType ==="speed" && <div className="heatmapGraphLayout">
                <Histogram data={chartData} />
              </div>}
            </Popup>
          </Rectangle>
        );
      })}
    </>
  );
};

export default GridHeatmapLayer;
