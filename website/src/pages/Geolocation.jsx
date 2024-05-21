import React, { useEffect, useState } from 'react';
import { MapContainer, TileLayer, Marker, Popup } from 'react-leaflet';
import { Icon } from 'leaflet';
import 'leaflet/dist/leaflet.css';
import '../styles/Components/Geolocation.css';
import pinIcon from '../assets/Icons/pin.png';

const createIcon = (url) => new Icon({
  iconUrl: url,
  iconSize: [30, 30],
  iconAnchor: [12, 41],
  popupAnchor: [1, -34],
  shadowSize: [41, 41]
});

const customIcon = createIcon(pinIcon);

const MeasurementMarker = ({ measurement, index }) => {
  const [address, setAddress] = useState('');
  const coordinates = [
    measurement.location.coordinates[1],
    measurement.location.coordinates[0]
  ];

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
  }, [coordinates]);

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
  const [showDetails, setShowDetails] = useState(false);
  const coordinates = [
    measurement.location.coordinates[1],
    measurement.location.coordinates[0]
  ];

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
      <button onClick={() => setShowDetails(!showDetails)}>Show More</button>
    </div>
  );
};

const Geolocation = () => {
  const [measurements, setMeasurements] = useState([]);
  const [currentPage, setCurrentPage] = useState(1);
  const [mapCenter, setMapCenter] = useState([46.5546, 15.6467]);

  const backendUrl = process.env.REACT_APP_BACKEND_URL + '/measurements';
  const itemsPerPage = 10;

  const fetchMeasurements = async (endpoint) => {
    try {
      const response = await fetch(`${backendUrl}/${endpoint}`);
      const data = await response.json();
      const paginatedData = data.slice((currentPage - 1) * itemsPerPage, currentPage * itemsPerPage);
      setMeasurements(paginatedData);
    } catch (error) {
      console.error('Error:', error);
    }
  };

  const nextPage = () => setCurrentPage(currentPage + 1);
  
  const prevPage = () => currentPage > 1 && setCurrentPage(currentPage - 1);

  useEffect(() => {
    fetchMeasurements('');
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