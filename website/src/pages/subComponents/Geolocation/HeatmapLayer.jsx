import { useEffect, useRef } from 'react';
import { useMap } from 'react-leaflet';
import L from 'leaflet';

const HeatmapLayer = ({ allMeasurements, heatmapType, cellSize, colorScale }) => {
  const layerRef = useRef(null);
  const map = useMap();

  useEffect(() => {
    if (layerRef.current) map.removeLayer(layerRef.current);

    const createGridLayer = () => {
      const bounds = map.getBounds();
      const topLeft = bounds.getNorthWest();
      const bottomRight = bounds.getSouthEast();
      const cellWidth = cellSize / Math.cos(topLeft.lat * Math.PI / 180); // Adjust for latitude

      let cells = [];
      for (let lat = topLeft.lat; lat > bottomRight.lat; lat -= cellSize) {
        for (let lng = topLeft.lng; lng < bottomRight.lng; lng += cellWidth) {
          const cellBounds = L.latLngBounds(
            [lat, lng],
            [lat - cellSize, lng + cellWidth]
          );
          const measurementsInCell = allMeasurements.filter(measurement => 
            cellBounds.contains(L.latLng(
              measurement.location.coordinates[1],
              measurement.location.coordinates[0]
            ))
          );

          const intensity = heatmapType === 'speed'
            ? measurementsInCell.reduce((sum, m) => sum + m.speed, 0) / measurementsInCell.length || 0
            : measurementsInCell.reduce((sum, m) => sum + new Date(m.time).getTime(), 0) / measurementsInCell.length || 0;

          cells.push({ bounds: cellBounds, intensity });
        }
      }

      layerRef.current = L.layerGroup(cells.map(cell => {
        const color = colorScale(cell.intensity);
        return L.rectangle(cell.bounds, { color, weight: 0, fillOpacity: 0.6 });
      })).addTo(map);
    };

    createGridLayer();

    map.on('moveend', createGridLayer);
    return () => {
      map.off('moveend', createGridLayer);
      if (layerRef.current) map.removeLayer(layerRef.current);
    };
  }, [map, allMeasurements, heatmapType, cellSize, colorScale]);

  return null;
};

export default HeatmapLayer;
