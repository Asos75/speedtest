// Dependencies
import React, { useEffect, useState } from 'react';
import { MapContainer, TileLayer, Marker, Popup } from 'react-leaflet';

// Styles
import 'leaflet/dist/leaflet.css';
import '../styles/Components/Geolocation.css'; 

// Assets
import { Icon } from 'leaflet';
import pinIcon from '../assets/Icons/pin.png';
const customIcon = new Icon({
  iconUrl: pinIcon,
  iconSize: [30, 30],
  iconAnchor: [12, 41],
  popupAnchor: [1, -34],
  shadowSize: [41, 41]
});

const MeasurementMarker = ({ measurement, index }) => {
  // Address state
  const [address, setAddress] = useState('');
  // Coordinates are stored in reverse order
  // eslint-disable-next-line react-hooks/exhaustive-deps
  const coordinates = [
    measurement.location.coordinates[1],
    measurement.location.coordinates[0]
  ];

  // FIX ADDRESS FETCHING LATER
  useEffect(() => {
    const fetchAddress = async () => {
      try {
        const response = await fetch(`https://nominatim.openstreetmap.org/reverse?format=json&lat=${coordinates[0]}&lon=${coordinates[1]}`);
        const data = await response.json();
        setAddress(data.display_name);
      } catch (error) {
        // console.error('Error:', error);
      }
    };

    fetchAddress();
  });

  // Check if coordinates are valid
  if (coordinates.some(isNaN)) {
    console.error(`Invalid coordinates for measurement at index ${index}:`, coordinates);
    return null;
  }

  return (
    <Marker key={index} position={coordinates} icon={customIcon}>
      <Popup>
        <p><b>{measurement.type}</b></p>
        <p>Coordinates: <b>{coordinates.join(', ')}</b></p>
        <p>Address: <b>{address}</b></p>
        <p>Speed: <b>{measurement.speed.$numberInt ? measurement.speed.$numberInt : 'Currently unvailable'}</b></p>
        <p>Provider: <b>{measurement.provider}</b></p>
      </Popup>
    </Marker>
  );
};

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

  return (
    <div key={index} className="measurement">
      <p><b>{measurement.type}</b> | Coordinates: <b>{coordinates.join(', ')}</b></p>
      {showDetails && (
        <>
          <p>Speed: <b>{measurement.speed.$numberInt ? measurement.speed.$numberInt : 'Currently unvailable'}</b> | Provider: <b>{measurement.provider}</b></p>
        </>
      )}
      <button onClick={() => setShowDetails(!showDetails)}>{!showDetails ? "Show More" : "Show less"}</button>
    </div>
  );
};

const Geolocation = () => {
  // Measurements
  const [measurements, setMeasurements] = useState([]);
  const [preloadedMeasurements, setPreloadedMeasurements] = useState([]);

  // Pagination
  const [currentPage, setCurrentPage] = useState(1);
  const itemsPerPage = 10;

  // Centering map view
  // const [mapCenter, setMapCenter] = useState([46.5546, 15.6467]);
  const mapCenter = [46.5546, 15.6467];

  // Backend URL
  const backendUrl = process.env.REACT_APP_BACKEND_URL + '/measurements';

  // Fetching measurements
  const fetchMeasurements = async (page) => {
    try {
      const response = await fetch(`${backendUrl}?page=${page}`);
      const data = await response.json();
      const paginatedData = data.slice((page - 1) * itemsPerPage, page * itemsPerPage);
      return paginatedData;
    } catch (error) {
      console.error('Error:', error);
    }
  };

  // Pagination functions
  const prevPage = () => currentPage > 1 && setCurrentPage(currentPage - 1);
  const nextPage = () => {
    setCurrentPage(currentPage + 1);
    setMeasurements(preloadedMeasurements);
  };

  // Fetching measurements on page load
  useEffect(() => {
    const loadMeasurements = async () => {
      const currentMeasurements = await fetchMeasurements(currentPage);
      setMeasurements(currentMeasurements);
      const nextMeasurements = await fetchMeasurements(currentPage + 1);
      setPreloadedMeasurements(nextMeasurements);
    };
    loadMeasurements();
  // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [currentPage]);

  return (
    <div className="blueBackground">
      <h2 className="geolocationTitle">Geolocation Page</h2>
      <div className="geolocationLayout">
        <MapContainer center={mapCenter} zoom={13} className="measurementMap">
          <TileLayer
            url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
            attribution='&copy; <a href="http://osm.org/copyright">OpenStreetMap</a> contributors'
          />
          {measurements.map((measurement, index) => (
            <MeasurementMarker
              key={index}
              measurement={measurement}
              index={index}
            />
          ))}
        </MapContainer>
        <div className="measurementContainer">
        <h2 className="measurementTitle">Measurement list</h2>
        <div className="measurementButtonContainer">
            <button onClick={prevPage} disabled={currentPage === 1}>Previous Page</button>
            <button onClick={nextPage}>Next Page</button>
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