import React from 'react';
import { Select, MenuItem } from '@material-ui/core';
import Measurement from './Measurement';

const PointsSettings = ({ measurements, setLayout, filterType, setFilterType, itemsPerPage, setItemsPerPage, itemsPerPageOptions, currentPage, totalPages, prevPage, goToPage, nextPage }) => {
  return (
    <div className="measurementContainer">
        <div className="measurementHeaderContainer">
          <h2 className="measurementTitle">Measurement List</h2>
          <div className="measurementButtonsContainer">
            <button onClick={() => setLayout('points')}>
              <span role="img" aria-label="pin">📌</span>
            </button>
            <button onClick={() => setLayout('grid')}>
              <span role="img" aria-label="grid">🔳</span>
            </button>
          </div>
        </div>
        <hr className="measurementDivider" />
          <div className="measurementSettingsContainer">
            <div className="measurementPageContainer">
              <p className="measurementCurrentPage">Showing page 
              <input type="number" min="1" max={totalPages} 
                  value={currentPage} onChange={(e) => goToPage(Number(e.target.value))} />
                   out of {totalPages}</p>
              <Select
                labelId="filter-label"
                value={filterType}
                onChange={(e) => setFilterType(e.target.value)}
                className="measurementPageSelect"
                style={{ fontSize: '20px' }}
              >
                <MenuItem value="dateAsc">Date Ascending</MenuItem>
                <MenuItem value="dateDesc">Date Descending</MenuItem>
                <MenuItem value="coordinatesAsc">Coordinates Ascending</MenuItem>
                <MenuItem value="coordinatesDesc">Coordinates Descending</MenuItem>
              </Select>
              <Select
                labelId="filter-label"
                value={itemsPerPage}
                onChange={(e) => setItemsPerPage(e.target.value)}
                style={{ fontSize: '20px' }}
              >
                {itemsPerPageOptions.map((option, index) => (
                  <MenuItem key={index} value={option}>{option}</MenuItem>
                ))}
              </Select>
            </div>
            <div className="measurementPageSelection">
              <button onClick={() => prevPage()} disabled={currentPage === 1}>Previous</button>
              <button onClick={() => nextPage()} disabled={currentPage === totalPages}>Next</button>
            </div>
          </div>
          <div className="measurementList">
            {measurements.map((measurement, index) => (
              <Measurement
                key={index}
                measurement={measurement}
                index={index}
              />
            ))}
          </div>
        </div>
  );
};

export default PointsSettings;