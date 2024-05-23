// Dependencies
import React, { useEffect, useState, useRef } from 'react';
import { MapContainer, TileLayer} from 'react-leaflet';
import { calculateDistance } from '../helpers/helperFunction';
import { useMap } from 'react-leaflet';
import L from 'leaflet';
import 'leaflet.heat';
import { Select, MenuItem, InputLabel } from '@material-ui/core';

// Styles
import 'leaflet/dist/leaflet.css';
import '../styles/Components/Geolocation.css'; 

// SubComponents
import Measurement from './subComponents/Measurement';
import MeasurementMarker from './subComponents/MeasurementMarker';

const HeatmapLayer = ({ points, ...props }) => {
  const layerRef = useRef(null);
  const map = useMap();

  useEffect(() => {
    if (layerRef.current) {
      map.removeLayer(layerRef.current);
    }

    layerRef.current = L.heatLayer(points, props).addTo(map);
  }, [map, points, props]);

  return null;
};

const Geolocation = () => {
  // Measurements
  const [measurements, setMeasurements] = useState([]);
  const [allMeasurements, setAllMeasurements] = useState([]); // new state for all measurements

  // Filter
  const [filterType, setFilterType] = useState('dateAsc');
  const [totalPages, setTotalPages] = useState(1);

  // Layout
  const [layout, setLayout] = useState('points');

  // Pagination
  const [currentPage, setCurrentPage] = useState(1);
  const itemsPerPage = 10;

  // Centering map view
  const mapCenter = [46.5546, 15.6467];

  // Backend URL
  const backendUrl = process.env.REACT_APP_BACKEND_URL + '/measurements';

  // Fetching measurements
  const fetchMeasurements = async () => {
    try {
      const response = await fetch(backendUrl);
      const data = await response.json();
      setAllMeasurements(data); // store all measurements
      setTotalPages(Math.ceil(data.length / itemsPerPage));
    } catch (error) {
      console.error('Error:', error);
    }
  };

  // Pagination functions
  const prevPage = () => {
    if (currentPage > 1) {
      setCurrentPage(currentPage - 1);
    }
  };

  const nextPage = () => {
    setCurrentPage(currentPage + 1);
  };

  const goToPage = (page) => {
    if (page >= 1 && page <= totalPages) {
      setCurrentPage(page);
    } else if (page < 1) {
      setCurrentPage(1);
    } else if (page > totalPages) {
      setCurrentPage(totalPages);
    }
  };

  // Filter function
  const handleFilter = () => {
    let filteredMeasurements;
    switch (filterType) {
      case 'dateAsc':
        filteredMeasurements = [...allMeasurements].sort((a, b) => new Date(a.time) - new Date(b.time));
        break;
      case 'dateDesc':
        filteredMeasurements = [...allMeasurements].sort((a, b) => new Date(b.time) - new Date(a.time));
        break;
      case 'coordinatesAsc':
        filteredMeasurements = [...allMeasurements].sort((a, b) => calculateDistance(mapCenter, a.location.coordinates) - calculateDistance(mapCenter, b.location.coordinates));
        break;
      case 'coordinatesDesc':
        filteredMeasurements = [...allMeasurements].sort((a, b) => calculateDistance(mapCenter, b.location.coordinates) - calculateDistance(mapCenter, a.location.coordinates));
        break;
      default:
        filteredMeasurements = allMeasurements;
    }
    setMeasurements(filteredMeasurements.slice((currentPage - 1) * itemsPerPage, currentPage * itemsPerPage));
  };

  // Fetching measurements on page load
  useEffect(() => {
    fetchMeasurements();
  }, []);

  // Apply filter whenever filterType, allMeasurements or currentPage changes
  useEffect(() => {
    handleFilter();
  }, [filterType, allMeasurements, currentPage]);

  return (
    <div className="blueBackground">
      <h2 className="geolocationTitle">Geolocation Page</h2>
      <div className="geolocationLayout">
      <MapContainer center={mapCenter} zoom={13} className="measurementMap">
        <TileLayer
          url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
          attribution='&copy; <a href="http://osm.org/copyright">OpenStreetMap</a> contributors'
        />
        {layout === 'points' && measurements.map((measurement, index) => (
          <MeasurementMarker
            key={index}
            measurement={measurement}
            index={index}
          />
        ))}
        {layout === 'grid' && (
          <HeatmapLayer
            points={measurements.map(measurement => [measurement.location.coordinates[1], measurement.location.coordinates[0]])}
            radius={20}
            blur={15}
            max={0.5}
          />
        )}
      </MapContainer>
      <div className="measurementContainer">
        <div className="measurementHeaderContainer">
          <h2 className="measurementTitle">Measurement List</h2>
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
          <div className="measurementSettingsContainer">
            <div className="measurementPageContainer">
              <p className="measurementCurrentPage">Showing page {currentPage} out of {totalPages}</p>
              <Select
                labelId="filter-label"
                value={filterType}
                onChange={(e) => setFilterType(e.target.value)}
                className="measurementPageSelect"
                style={{ fontSize: '20px' }}
              >
                <MenuItem value="dateAsc">Date Ascending</MenuItem>
                <MenuItem value="dateDesc">Date Descending</MenuItem>
                <MenuItem value="coordinatesAsc">Coordinates Ascending</MenuItem>
                <MenuItem value="coordinatesDesc">Coordinates Descending</MenuItem>
              </Select>
            </div>
            <div className="measurementPageSelection">
              <button onClick={() => prevPage()} disabled={currentPage === 1}>Previous Page</button>
              <div className="measurementPageInput">
                <p>Choose page</p>
                <input type="number" min="1" max={totalPages} 
                  value={currentPage} onChange={(e) => goToPage(Number(e.target.value))} />
              </div>
              <button onClick={() => nextPage()}>Next Page</button>
            </div>
          </div>
          <div className="measurementList">
            {measurements.map((measurement, index) => (
              <Measurement
                key={index}
                measurement={measurement}
                index={index}
              />
            ))}
          </div>
          <div className="measurementButtonContainer">
            <button onClick={prevPage} disabled={currentPage === 1}>Previous Page</button>
            <button onClick={nextPage}>Next Page</button>
          </div>
        </div>
      </div>
      <div className="geolocationBackground"></div>
    </div>
  );
};

export default Geolocation;