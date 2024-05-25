import { useState, useEffect } from 'react';
import { calculateDistance } from '../../helpers/helperFunction';

export const useMeasurements = (backendUrl, mapCenter, filterType, itemsPerPage, currentPage, heatmapType, setHeatmapData, layout) => {
  const [allMeasurements, setAllMeasurements] = useState([]);
  const [measurements, setMeasurements] = useState([]);
  const [totalPages, setTotalPages] = useState(1);

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

  // Filter function
  const handleFilter = () => {
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

  useEffect(() => {
    fetchMeasurements();
  }, [itemsPerPage]);

  useEffect(() => {
    handleFilter();
    generateHeatmapData();
  }, [filterType, allMeasurements, currentPage, itemsPerPage, heatmapType, layout]);

  return { measurements, totalPages, fetchMeasurements, handleFilter };
};