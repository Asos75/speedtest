// Dependencies
import React, { useEffect, useState, useCallback } from 'react';
import { MapContainer, TileLayer } from 'react-leaflet';
import { calculateDistance } from '../helpers/helperFunction';

// Styles
import 'leaflet/dist/leaflet.css';
import '../styles/Components/Geolocation.css'; 

// SubComponents
import MeasurementMarker from './subComponents/Geolocation/MeasurementMarker';
import HeatmapSettings from './subComponents/Geolocation/HeatmapSettings';
import PointsSettings from './subComponents/Geolocation/PointsSettings';
import GridHeatmapLayer from './subComponents/Geolocation/GridHeatmapLayer'; // New Grid Heatmap Layer

const Geolocation = () => {
  // Measurements state
  const [measurements, setMeasurements] = useState([]);
  const [allMeasurements, setAllMeasurements] = useState([]);
  const [startDate, setStartDate] = useState('');
  const [endDate, setEndDate] = useState('');

  // Filter state
  const [filterType, setFilterType] = useState('dateAsc');
  const [totalPages, setTotalPages] = useState(1);
  const [heatmapType, setHeatmapType] = useState('speed');
  const [selectedArea, setSelectedArea] = useState(0.0025);
  const [selectedEvent, setSelectedEvent] = useState('none');
  const [timeRange, setTimeRange] = useState('1');
  const [eventTime, setEventTime] = useState(null);
  const [eventLocation, setEventLocation] = useState(null);

  // Pagination state
  const [layout, setLayout] = useState('points');
  const [currentPage, setCurrentPage] = useState(1);
  const [itemsPerPage, setItemsPerPage] = useState(10);
  const itemsPerPageOptions = [10, 20, 50];

  // Loading state
  const [loading, setLoading] = useState(false);

  // Map center coordinates
  const mapCenter = [46.5546, 15.6467];
  // Backend URL for fetching measurements
  const backendUrl = process.env.REACT_APP_BACKEND_URL + '/measurements';

  // Fetch measurements from the backend
  const fetchMeasurements = useCallback(async () => {
    setLoading(true);
    let url = backendUrl;
    if (startDate && endDate) {
      url = `${backendUrl}/timeframe/${startDate}/${endDate}`;
    }

    try {
      const response = await fetch(url);
      const data = await response.json();
      setAllMeasurements(data);
      setTotalPages(Math.ceil(data.length / itemsPerPage));
    } catch (error) {
      console.error('Error:', error);
    }
    setLoading(false);
  }, [backendUrl, itemsPerPage, startDate, endDate]);

  const fetchEvent = useCallback(async () => {
    try {
      const response = await fetch(`${process.env.REACT_APP_BACKEND_URL}/event/${selectedEvent}`);
      const data = await response.json();
      setEventLocation(data.location);
    } catch (error) {
      console.error('Error:', error);
    }
  }, [selectedEvent]);

  useEffect(() => {
    fetchMeasurements();
  }, [fetchMeasurements]);

  useEffect(() => {
    fetchEvent();
  }, [fetchEvent]);

  // Filter and paginate measurements
  const handleFilter = () => {
    if (layout === 'points') {
      const filteredMeasurements = [...allMeasurements].sort((a, b) => {
        switch (filterType) {
          case 'dateAsc':
            return new Date(a.time) - new Date(b.time);
          case 'dateDesc':
            return new Date(b.time) - new Date(a.time);
          case 'coordinatesAsc':
            return calculateDistance(mapCenter, a.location.coordinates) - calculateDistance(mapCenter, b.location.coordinates);
          case 'coordinatesDesc':
            return calculateDistance(mapCenter, b.location.coordinates) - calculateDistance(mapCenter, a.location.coordinates);
          default:
            return 0;
        }
      });
      setMeasurements(filteredMeasurements.slice((currentPage - 1) * itemsPerPage, currentPage * itemsPerPage));
    }
  };

  // Re-filter measurements when dependencies change
  useEffect(() => {
    handleFilter();
  // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [filterType, currentPage, itemsPerPage, allMeasurements]);

  const handleResetDates = () => {
    setStartDate('');
    setEndDate('');
    fetchMeasurements();
  };

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
            <>
              {eventLocation && (
                <MeasurementMarker
                  measurement={{ location: eventLocation, type: 'Event' }}
                  index={0}
                />
              )}
              <GridHeatmapLayer measurements={allMeasurements} heatmapType={heatmapType} setLoading={setLoading} selectedArea={selectedArea}/>
            </>
          )}
        </MapContainer>
        {loading && <div>Loading...</div>}
        {layout === 'points' && (
          <div className="pointsLocationLayout">
            <div className="geolocationDateSelect">
              <input
                type="date"
                value={startDate}
                onChange={(e) => setStartDate(e.target.value)}
                className="dateInput"
              />
              <input
                type="date"
                value={endDate}
                onChange={(e) => setEndDate(e.target.value)}
                className="dateInput"
              />
              <button onClick={fetchMeasurements} className="filterButton">Filter</button>
              <button onClick={handleResetDates} className="resetButton buttonRed">Reset Dates</button>
            </div>
            <PointsSettings
              setLayout={setLayout}
              filterType={filterType}
              setFilterType={setFilterType}
              itemsPerPage={itemsPerPage}
              setItemsPerPage={setItemsPerPage}
              currentPage={currentPage}
              totalPages={totalPages}
              prevPage={() => setCurrentPage(currentPage > 1 ? currentPage - 1 : 1)}
              nextPage={() => setCurrentPage(currentPage < totalPages ? currentPage + 1 : totalPages)}
              goToPage={page => setCurrentPage(page >= 1 && page <= totalPages ? page : currentPage)}
              itemsPerPageOptions={itemsPerPageOptions}
              measurements={measurements}
            />
          </div>
        )}
        {layout === 'grid' && (
          <div className="pointsLocationLayout">
            <div className="geolocationDateSelect">
              <input
                type="date"
                value={startDate}
                onChange={(e) => setStartDate(e.target.value)}
                className="dateInput"
              />
              <input
                type="date"
                value={endDate}
                onChange={(e) => setEndDate(e.target.value)}
                className="dateInput"
              />
              <button onClick={fetchMeasurements} className="filterButton">Filter</button>
              <button onClick={handleResetDates} className="resetButton buttonRed">Reset Dates</button>
            </div>
            <HeatmapSettings
              setLayout={setLayout}
              heatmapType={heatmapType}
              setHeatmapType={setHeatmapType}
              measurements={allMeasurements}
              selectedArea={selectedArea}
              setSelectedArea={setSelectedArea}
              selectedEvent={selectedEvent}
              setSelectedEvent={setSelectedEvent}
              timeRange={timeRange}
              setTimeRange={setTimeRange}
              eventTime={eventTime}
              setEventTime={setEventTime}
              setStartDate={setStartDate}
              setEndDate={setEndDate}
            />
          </div>
        )}
      </div>
    </div>
  );
};

export default Geolocation;
