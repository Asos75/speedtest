// Dependencies
import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { MapContainer, TileLayer, Marker, Popup } from 'react-leaflet';
import { Icon } from 'leaflet';
import 'leaflet/dist/leaflet.css';

// Styles
import '../styles/Components/Events.css';

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

const Event = ({ event, index, selectedEvent, setSelectedEvent }) => {
  const coordinates = [event.location.coordinates[1], event.location.coordinates[0]];

  if (coordinates.some(isNaN)) {
    console.error(`Invalid coordinates for event at index ${index}:`, coordinates);
    return null;
  }

  return (
    <div key={index} className={`event ${selectedEvent === index ? 'highlight' : ''}`} /*onClick={() => setSelectedEvent(index)}*/>
      <p><b>{event.name}</b> | Coordinates: <b>{coordinates.join(', ')}</b> | {event.online ? 'Online' : 'Offline'}</p>
      <p>Type: <b>{event.type}</b> | Time: <b>{new Date(event.time).toLocaleString()}</b></p>
    </div>
  );
};

const EventMarker = ({ event, index }) => {
  const coordinates = [event.location.coordinates[1], event.location.coordinates[0]];

  if (coordinates.some(isNaN)) {
    console.error(`Invalid coordinates for event at index ${index}:`, coordinates);
    return null;
  }

  return (
    <Marker key={index} position={coordinates} icon={event.online ? blueIcon : customIcon}>
      <Popup>
        <p><b>{event.name}</b></p>
        <p>Coordinates: <b>{coordinates.join(', ')}</b></p>
        <p>Type: <b>{event.type}</b></p>
        <p>Time: <b>{new Date(event.time).toLocaleString()}</b></p>
        <p>{event.online ? 'Online' : 'Offline'}</p>
      </Popup>
    </Marker>
  );
};

function EventList() {
  const [events, setEvents] = useState([]);
  const [selectedEvent, setSelectedEvent] = useState(null);
  const [mapCenter, setMapCenter] = useState([46.5546, 15.6467]);

  useEffect(() => {
    axios.get(`${process.env.REACT_APP_BACKEND_URL}/event`)
      .then(response => {
        setEvents(response.data);
      })
      .catch(error => {
        console.error('There was an error!', error);
      });
  }, []);

  return (
    <>
      <h1 className="eventTitle">Events</h1>
      <div className="eventLayout">
        <MapContainer center={mapCenter} zoom={13} className="myCustomMap">
          <TileLayer
            url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
            attribution='&copy; <a href="http://osm.org/copyright">OpenStreetMap</a> contributors'
          />
          {events.map((event, index) => (
            <EventMarker
              key={index}
              event={event}
              index={index}
            />
          ))}
        </MapContainer>
        <div className="eventList">
          <h2>Event List</h2>
          {events.map((event, index) => (
            <Event
              key={index}
              event={event}
              index={index}
              selectedEvent={selectedEvent}
              setSelectedEvent={setSelectedEvent}
            />
          ))}
        </div>
      </div>
    </>
  );
}

export default EventList;
