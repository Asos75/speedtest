// Dependencies
import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { MapContainer, TileLayer, Marker, Popup } from 'react-leaflet';
import { Icon } from 'leaflet';
import { Link } from 'react-router-dom'; // Ensure you have react-router-dom installed
import '../styles/User.css';
import { formatTime } from '../helpers/helperFunction';
import pinIcon from '../assets/Icons/pin.png';

// Custom marker icon
const customIcon = new Icon({
  iconUrl: pinIcon,
  iconSize: [30, 30],
  iconAnchor: [12, 41],
  popupAnchor: [1, -34],
  shadowSize: [41, 41],
});

function UserPage() {
  const [user, setUser] = useState(null);
  const [measurements, setMeasurements] = useState([]);
  const [mapCenter, setMapCenter] = useState([46.5546, 15.6467]); // Default center, to be updated

  useEffect(() => {
    const id = localStorage.getItem('id');
    const token = localStorage.getItem('token');

    const fetchUserData = async () => {
      try {
        const userResponse = await axios.get(`${process.env.REACT_APP_BACKEND_URL}/users/users/${id}`, {
          headers: {
            'Authorization': `Bearer ${token}`
          }
        });
        setUser(userResponse.data);
      } catch (error) {
        console.error('There was an error fetching user data!', error);
      }
    };

    const fetchUserMeasurements = async () => {
      try {
        const measurementsResponse = await axios.get(`${process.env.REACT_APP_BACKEND_URL}/measurements/user/${id}`, {
          headers: {
            'Authorization': `Bearer ${token}`
          }
        });
        const measurementsData = measurementsResponse.data;
        setMeasurements(measurementsData);

        if (measurementsData.length > 0) {
          setMapCenter([measurementsData[0].location.coordinates[1], measurementsData[0].location.coordinates[0]]);
        }
      } catch (error) {
        console.error('There was an error fetching measurements!', error);
      }
    };

    fetchUserData();
    fetchUserMeasurements();
  }, []);

  return (
    <div className="userContainer">
      <h1 className="mobileTowerTitle">User Page</h1>
      <div className="oneColumnContainer blueBackground">
        <div className="userDescription">
          <div className="userInfoContainer">
            {user ? (
              <>
                <div className="userInfo">
                  <h3>User: <b>{user.username}</b></h3>
                  <p>Email: <b>{user.email}</b></p>
                  <p className="userInfoRole">{user.admin ? 'Admin' : 'Default user'}</p>
                </div>
                <h4>Add data to database</h4>
                <div className="userAddInfo">
                  <a href="/measure">Add a new measurement</a>
                </div>
              </>
            ) : (
              <p>Loading...</p>
            )}
          </div>
          <div className="userExtraInfo">
            <h4>Your Measurements</h4>
            {measurements.length > 0 ? (
              <MapContainer center={mapCenter} zoom={13} className="measurementUserMap">
                <TileLayer
                  url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
                  attribution='&copy; <a href="http://osm.org/copyright">OpenStreetMap</a> contributors'
                />
                {measurements.map((measurement, index) => (
                  <Marker
                    key={index}
                    position={[measurement.location.coordinates[1], measurement.location.coordinates[0]]}
                    icon={customIcon}
                  >
                    <Popup>
                      <p>Location: <b>{measurement.location.coordinates.join(', ')}</b></p>
                      <p>Time: <b>{formatTime(new Date(measurement.time).toLocaleString())}</b></p>
                      <p>Speed: <b>{(measurement.speed / 1024 / 1024).toFixed(2)} MB/s</b></p>
                    </Popup>
                  </Marker>
                ))}
              </MapContainer>
            ) : (
              <div>
                <p>No measurements found.</p>
                <Link to="/measure" className="button">Go to Speedtest</Link>
              </div>
            )}
          </div>
        </div>
      </div>
    </div>
  );
}

export default UserPage;
