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

// Helpers
import { formatTime, getTimeDifference } from '../helpers/helperFunction';

const createIcon = (url) => new Icon({
  iconUrl: url,
  iconSize: [30, 30],
  iconAnchor: [12, 41],
  popupAnchor: [1, -34],
  shadowSize: [41, 41]
});

const customIcon = createIcon(pinIcon);
const blueIcon = createIcon(bluePinIcon);

const Event = ({ event, index, selectedEvent, setSelectedEvent }) => {
  const coordinates = [event.location.coordinates[1], event.location.coordinates[0]];

  if (coordinates.some(isNaN)) {
    console.error(`Invalid coordinates for event at index ${index}:`, coordinates);
    return null;
  }

  return (
    <div key={index} className={`event ${selectedEvent === index ? 'highlight' : ''}`}>
      <div className="eventsContainerLayout">
        <p><b>{event.name}</b> | Coordinates: <b>{coordinates.join(', ')}</b> | {event.online ? 'Online' : 'Offline'}</p>
        <p>Type: <b>{event.type}</b> | Time: <b>{formatTime(new Date(event.time).toLocaleString())}</b></p>
        <p className="eventsContainerStartTime">{getTimeDifference(event.time)}</p>
      </div>
      {/* <button className="eventContainerButton" onClick={() => setSelectedEvent(index)}>Select Event</button> */}
    </div>
  );
};

const EventMarker = ({ events, selectedEventIndex, setSelectedEventIndex }) => {
  const coordinates = [events[0].location.coordinates[1], events[0].location.coordinates[0]];

  if (coordinates.some(isNaN)) {
    console.error(`Invalid coordinates for events:`, coordinates);
    return null;
  }

  const nextEvent = () => {
    setSelectedEventIndex((prevIndex) => (prevIndex + 1) % events.length);
  };

  const prevEvent = () => {
    setSelectedEventIndex((prevIndex) => (prevIndex - 1 + events.length) % events.length);
  };

  return (
    <Marker position={coordinates} icon={events[0].online ? blueIcon : customIcon}>
      <Popup onOpen={() => setSelectedEventIndex(0)}>
        <div>
          {events[selectedEventIndex] && (
            <>
              <p><b>{events[selectedEventIndex].name}</b></p>
              <p>Coordinates: <b>{coordinates.join(', ')}</b></p>
              <p>Type: <b>{events[selectedEventIndex].type}</b></p>
              <p>Time: <b>{formatTime(new Date(events[selectedEventIndex].time).toLocaleString())}</b></p>
              <p>{events[selectedEventIndex].online ? 'Online' : 'Offline'}</p>
            </>
          )}
          {events.length > 1 && (
            <div className="eventNavigation">
              <button onClick={prevEvent}>⬅️</button>
              <span>Showing {selectedEventIndex + 1} of {events.length}</span>
              <button onClick={nextEvent}>➡️</button>
            </div>
          )}
        </div>
      </Popup>
    </Marker>
  );
};

const EventList = () => {
  const [events, setEvents] = useState([]);
  const [selectedEvent, setSelectedEvent] = useState(null);
  const [mapCenter, setMapCenter] = useState([46.5546, 15.6467]);
  const [showPastEvents, setShowPastEvents] = useState(false);
  const [selectedEventIndex, setSelectedEventIndex] = useState(0);

  useEffect(() => {
    axios.get(`${process.env.REACT_APP_BACKEND_URL}/event`)
      .then(response => {
        setEvents(response.data);
      })
      .catch(error => {
        console.error('There was an error!', error);
      });
  }, []);

  const currentDateTime = new Date();

  const upcomingEvents = events.filter(event => new Date(event.time) >= currentDateTime);
  const pastEvents = events.filter(event => new Date(event.time) < currentDateTime);

  const displayedEvents = showPastEvents ? pastEvents : upcomingEvents;

  const groupEventsByLocation = (events) => {
    const groupedEvents = {};
    events.forEach(event => {
      const coordinates = event.location.coordinates.join(',');
      if (!groupedEvents[coordinates]) {
        groupedEvents[coordinates] = [];
      }
      groupedEvents[coordinates].push(event);
    });
    return Object.values(groupedEvents);
  };

  const groupedEvents = groupEventsByLocation(displayedEvents);

  return (
    <>
      <h1 className="eventTitle">Events</h1>
      <div className="eventLayout">
        <MapContainer center={mapCenter} zoom={13} className="myCustomMap">
          <TileLayer
            url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
            attribution='&copy; <a href="http://osm.org/copyright">OpenStreetMap</a> contributors'
          />
          {groupedEvents.map((events, index) => (
            <EventMarker
              key={index}
              events={events}
              selectedEventIndex={selectedEventIndex}
              setSelectedEventIndex={setSelectedEventIndex}
            />
          ))}
        </MapContainer>
        <div className="eventList">
          <h2>Event List</h2>
          <p onClick={() => setShowPastEvents(!showPastEvents)} className="toggleEvents">
            {showPastEvents ? 'Back to Upcoming Events' : 'Show Past Events'}
          </p>
          {/* Logic to deselect currently seelcted event if one is selected*/}
          {displayedEvents.map((event, index) => (
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
export { Event, EventMarker};
