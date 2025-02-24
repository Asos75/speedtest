// Dependencies
import React, { useState } from 'react';
import { Marker, Popup } from 'react-leaflet';
import { formatTime } from '../../../helpers/helperFunction';

// Assets
import { Icon } from 'leaflet';
import pinIcon from '../../../assets/Icons/pin.png';


const customIcon = new Icon({
  iconUrl: pinIcon,
  iconSize: [30, 30],
  iconAnchor: [12, 41],
  popupAnchor: [1, -34],
  shadowSize: [41, 41]
});

const MeasurementMarker = ({ measurement, index }) => {
  // Address state
  // const [address, setAddress] = useState('');
  // Coordinates are stored in reverse order
  // eslint-disable-next-line react-hooks/exhaustive-deps
  const coordinates = [
    measurement.location.coordinates[1],
    measurement.location.coordinates[0]
  ];

  // FIX ADDRESS FETCHING LATER
  /*useEffect(() => {
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
  });*/

  // Check if coordinates are valid
  if (coordinates.some(isNaN)) {
    console.error(`Invalid coordinates for measurement at index ${index}:`, coordinates);
    return null;
  }

  const speedInMBps = (measurement.speed * 8 / (1024 * 1024)).toFixed(2);
  return (
    <Marker key={index} position={coordinates} icon={customIcon}>
      <Popup>
        <p><b>{measurement.type}</b></p>
        {coordinates && <p>Coordinates: <b>{coordinates.join(', ')}</b></p>}
        {/* {address && <p>Address: <b>{address}</b></p>} */}
        {measurement.speed && <p>Speed: <b>{speedInMBps} Mb/s</b></p>}
        {measurement.provider && <p>Provider: <b>{measurement.provider}</b></p>}
        {measurement.time && <p>Time: <b>{formatTime(measurement.time)}</b></p>}
      </Popup>
    </Marker>
  );
};

export default MeasurementMarker;