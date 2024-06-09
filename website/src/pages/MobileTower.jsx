// Dependencies
import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { MapContainer, TileLayer, Marker, Popup } from 'react-leaflet';
import { Icon } from 'leaflet';
import 'leaflet/dist/leaflet.css';

// Styles
import '../styles/Components/MobileTower.css';

// Components
import pinIcon from '../assets/Icons/pin.png';
import bluePinIcon from '../assets/Icons/blue-pin.png';

function createIcon(url) {
  return new Icon({
    iconUrl: url,
    iconSize: [30, 30],
    iconAnchor: [12, 41],
    popupAnchor: [1, -34],
    shadowSize: [41, 41]
  });
}

const customIcon = createIcon(pinIcon);
const blueIcon = createIcon(bluePinIcon);

const MobileTower = ({ mobileTower, index, selectedTower, setSelectedTower }) => {
  const coordinates = [mobileTower.location.coordinates[1], mobileTower.location.coordinates[0]];

  if (coordinates.some(isNaN)) {
    console.error(`Invalid coordinates for mobile tower at index ${index}:`, coordinates);
    return null;
  }

  return (
    <div key={index} className={`mobileTower ${selectedTower === index ? 'highlight' : ''}`} onClick={() => setSelectedTower(index)}>
      <p><b>{mobileTower.location.type}</b> | Coordinates: <b>{coordinates.join(', ')}</b> | {mobileTower.locator ? 'Locator: ' + mobileTower.locator.$oid : 'No locator'}</p>
      <p>Operator: <b>{mobileTower.operator}</b> | Type: <b>{mobileTower.type}</b> | Status: <b>{mobileTower.confirmed ? 'Confirmed' : 'Not Confirmed'}</b></p>
    </div>
  );
};

const MobileTowerMarker = ({ mobileTower, index }) => {
  const coordinates = [mobileTower.location.coordinates[1], mobileTower.location.coordinates[0]];

  if (coordinates.some(isNaN)) {
    console.error(`Invalid coordinates for mobile tower at index ${index}:`, coordinates);
    return null;
  }

  return (
    <Marker key={index} position={coordinates} icon={mobileTower.confirmed ? customIcon : blueIcon}>
      <Popup>
        <p><b>{mobileTower.location.type}</b> | Coordinates: <b>{coordinates.join(', ')}</b></p>
        <p>Operator: <b>{mobileTower.operator}</b> | {mobileTower.locator ? 'Locator: ' + mobileTower.locator.$oid : 'No locator'}</p>
        <p>Type: <b>{mobileTower.type}</b> | Status: <b>{mobileTower.confirmed ? 'Confirmed' : 'Not Confirmed'}</b></p>
      </Popup>
    </Marker>
    );
};

function MobileTowerList() {
  const [mobileTowers, setMobileTowers] = useState([]);
  const [selectedTower, setSelectedTower] = useState(null);
  const [mapCenter, setMapCenter] = useState([46.5546, 15.6467]);

  useEffect(() => {
    axios.get(`${process.env.REACT_APP_BACKEND_URL}/mobile`)
      .then(response => {
        setMobileTowers(response.data);
      })
      .catch(error => {
        console.error('There was an error!', error);
      });
  }, []);

  return (
    <>
    <h1 className="mobileTowerTitle">Mobile Towers</h1>
    <div className="mobileTowerLayout">
      <MapContainer center={mapCenter} zoom={13} className="myCustomMap">
        <TileLayer
          url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
          attribution='&copy; <a href="http://osm.org/copyright">OpenStreetMap</a> contributors'
        />
        {mobileTowers.map((mobileTower, index) => (
          <MobileTowerMarker
            key={index}
            mobileTower={mobileTower}
            index={index}
          />
        ))}
      </MapContainer>
      <div className="mobileTowerList">
        <h2>Mobile Tower list</h2>
        {mobileTowers.map((mobileTower, index) => (
          <MobileTower
            key={index}
            mobileTower={mobileTower}
            index={index}
            selectedTower={selectedTower}
            setSelectedTower={setSelectedTower}
          />
        ))}
      </div>
    </div>
    </>
  );
}

export default MobileTowerList;