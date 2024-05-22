// Dependencies
import React, { useEffect, useState } from 'react';
import { MapContainer, TileLayer} from 'react-leaflet';
import { HeatmapLayer } from 'leaflet-heatmap';
import { calculateDistance } from '../helpers/helperFunction';

// Styles
import 'leaflet/dist/leaflet.css';
import '../styles/Components/Geolocation.css'; 

// SubComponents
import Measurement from './subComponents/Measurement';
import MeasurementMarker from './subComponents/MeasurementMarker';

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
            fitBoundsOnLoad
            fitBoundsOnUpdate
            points={measurements}
            longitudeExtractor={m => m.location.coordinates[0]}
            latitudeExtractor={m => m.location.coordinates[1]}
            intensityExtractor={m => parseFloat(m.value)}
          />
        )}
      </MapContainer>
      <div className="measurementContainer">
        <div className="layoutButtons">
          <button onClick={() => setLayout('points')}>Points</button>
          <button onClick={() => setLayout('grid')}>Grid</button>
        </div>
        <h2 className="measurementTitle">Measurement List</h2>
          <div className="measurementButtonContainer">
            <button onClick={() => prevPage()} disabled={currentPage === 1}>Previous Page</button>
            <p>Showing page {currentPage} out of {totalPages}</p>
            <button onClick={() => nextPage()}>Next Page</button>
            <input type="number" min="1" max={totalPages} onChange={(e) => goToPage(Number(e.target.value))} />
            <select onChange={(e) => setFilterType(e.target.value)}>
              <option value="dateAsc">Date Ascending</option>
              <option value="dateDesc">Date Descending</option>
              <option value="coordinatesAsc">Coordinates Ascending</option>
              <option value="coordinatesDesc">Coordinates Descending</option>
            </select>
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