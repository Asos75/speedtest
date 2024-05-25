// Dependencies
import React, { useEffect, useState, useRef } from 'react';
import { MapContainer, TileLayer} from 'react-leaflet';
import { calculateDistance } from '../helpers/helperFunction';
import { useMap } from 'react-leaflet';
import L from 'leaflet';
import 'leaflet.heat';

// Styles
import 'leaflet/dist/leaflet.css';
import '../styles/Components/Geolocation.css'; 

// SubComponents
import MeasurementMarker from './subComponents/Geolocation/MeasurementMarker';
import HeatmapSettings from './subComponents/Geolocation/HeatmapSettings';
import PointsSettings from './subComponents/Geolocation/PointsSettings';

const HeatmapLayer = ({ points, ...props }) => {
  const layerRef = useRef(null);
  const map = useMap();

  useEffect(() => {
    // Remove the previous layer before adding the new one
    if (layerRef.current) map.removeLayer(layerRef.current);
    layerRef.current = L.heatLayer(points, props).addTo(map);
    return () => layerRef.current && map.removeLayer(layerRef.current);
  }, [map, points, props]);

  return null;
};

const Geolocation = () => {
  // Measurements
  const [measurements, setMeasurements] = useState([]);
  const [allMeasurements, setAllMeasurements] = useState([]); // new state for all measurements

  // Filter
  const [filterType, setFilterType] = useState('dateAsc');
  const [totalPages, setTotalPages] = useState(1);

  // Heatmap
  const [heatmapType, setHeatmapType] = useState('speed'); // 'speed' or 'time'
  const [heatmapData, setHeatmapData] = useState([]);
  const [maxIntensity, setMaxIntensity] = useState(0.01);
  const [radius, setRadius] = useState(20);
  const [blur, setBlur] = useState(15);

  // Layout
  const [layout, setLayout] = useState('points');

  // Pagination
  const [currentPage, setCurrentPage] = useState(1);
  const [itemsPerPage, setItemsPerPage] = useState(10);
  const itemsPerPageOptions = [10, 20, 50];

  // Centering map view
  const mapCenter = [46.5546, 15.6467];

  // Backend URL
  const backendUrl = process.env.REACT_APP_BACKEND_URL + '/measurements';

  // Fetching measurements
  const fetchMeasurements = async () => {
    try {
      const response = await fetch(backendUrl);
      const data = await response.json();
      setAllMeasurements(data);
      setTotalPages(Math.ceil(data.length / itemsPerPage));
    } catch (error) {
      console.error('Error:', error);
    }
  };

  // Heatmap data generation
  const generateHeatmapData = () => {
    const data = allMeasurements.map(measurement => [
      measurement.location.coordinates[1],
      measurement.location.coordinates[0],
      heatmapType === 'speed' ? measurement.speed : new Date(measurement.time).getTime()
    ]);
    setHeatmapData(data);
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
    } else if (page < 1) {
      setCurrentPage(1);
    } else if (page > totalPages) {
      setCurrentPage(totalPages);
    }
  };

  // Filter function
  // Filter function
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

  // Fetching measurements on page load
  useEffect(() => {
    fetchMeasurements();
  }, [itemsPerPage]);

  useEffect(() => {
    handleFilter();
    generateHeatmapData();
  }, [filterType, allMeasurements, currentPage, heatmapType, layout, itemsPerPage]);

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
            points={heatmapData}
            radius={radius}
            blur={blur}
            max={maxIntensity}
          />
        )}
      </MapContainer>
      {layout === 'points' && (
          <PointsSettings setLayout={setLayout} filterType={filterType} setFilterType={setFilterType}
          itemsPerPage={itemsPerPage} setItemsPerPage={setItemsPerPage} currentPage={currentPage}
          totalPages={totalPages} prevPage={prevPage} nextPage={nextPage} goToPage={goToPage}
          itemsPerPageOptions={itemsPerPageOptions} measurements={measurements} />
        )}
      {layout === 'grid' && (
          <HeatmapSettings setLayout={setLayout} heatmapType={heatmapType} setHeatmapType={setHeatmapType}
          maxIntensity={maxIntensity} setMaxIntensity={setMaxIntensity} radius={radius}
          setRadius={setRadius} blur={blur} setBlur={setBlur} />
      )}
      </div>
    </div>
  );
};

export default Geolocation;