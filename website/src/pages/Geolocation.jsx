// Dependencies
import React, { useEffect, useState, useCallback } from 'react';
import { MapContainer, TileLayer} from 'react-leaflet';
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

  // Filter state
  const [filterType, setFilterType] = useState('dateAsc');
  const [totalPages, setTotalPages] = useState(1);
  const [heatmapType, setHeatmapType] = useState('speed');
  const [selectedArea, setSelectedArea] = useState(0.0025);

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
    try {
      const response = await fetch(backendUrl);
      const data = await response.json();
      setAllMeasurements(data);
      setTotalPages(Math.ceil(data.length / itemsPerPage));
    } catch (error) {
      console.error('Error:', error);
    }
  }, [backendUrl, itemsPerPage]);

  // Fetch measurements on component mount
  useEffect(() => {
    fetchMeasurements();
  }, [fetchMeasurements]);

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
  }, [filterType, currentPage, itemsPerPage, allMeasurements]);

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
            <GridHeatmapLayer measurements={allMeasurements} heatmapType={heatmapType} setLoading={setLoading} selectedArea={selectedArea}/>
          )}
        </MapContainer>
        {loading && <div>Loading...</div>}
        {layout === 'points' && (
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
        )}
        {layout === 'grid' && (
          <HeatmapSettings
            setLayout={setLayout}
            heatmapType={heatmapType}
            setHeatmapType={setHeatmapType}
            measurements={allMeasurements}
            selectedArea={selectedArea}
            setSelectedArea={setSelectedArea}
          />
        )}
      </div>
    </div>
  );
};

export default Geolocation;
