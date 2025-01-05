var MeasurementModel = require('../models/measurementModel.js');
const turf = require('@turf/turf');

/**
 * Vrne točke znotraj določenega radija okoli dane točke.
 *
 * @param {Array} center Koordinate središčne točke v obliki [lon, lat].
 * @param {Array} points Seznam točk, kjer vsaka točka ima koordinate [lon, lat].
 * @param {number} radius Radij v kilometrih.
 * @return {Array} Seznam točk znotraj radija.
 */

// Funkcija, ki vrne točke znotraj določenega radija od dane točke
function findPointsWithinRadius(center, points, radius) {
    const centerPoint = turf.point(center);
    const searchArea = turf.circle(centerPoint, radius, { steps: 64, units: 'kilometers' });

    const results = points.filter(point => {
        const targetPoint = turf.point(point.coords);
        if (turf.booleanPointInPolygon(targetPoint, searchArea)) {
            return {
                coords: point.coords,
                speed: point.speed
            };
        }
    });

    return results;
}

    /**
 * Ustvari pravokotnik glede na dve koordinati (levi zgornji kot in desni spodnji kot).
 * @param {Array} topLeftKoordinate Koordinate levega zgornjega kota pravokotnika [lat, lon].
 * @param {Array} bottomRightKoordinate Koordinate desnega spodnjega kota pravokotnika [lat, lon].
 * @param {Array} tocke Koordinate preverjanja [lat, lon].
 * @returns {boolean} true, če je koordinata znotraj pravokotnika, sicer false.
 */

function isWithinRectangle(topLeftKoordinate, bottomRightKoordinate, tocke) {
    const topLeft = turf.point(topLeftKoordinate);
    const bottomRight = turf.point(bottomRightKoordinate);
    const bbox = turf.bbox(turf.featureCollection([topLeft, bottomRight]));
    const bboxPolygon = turf.bboxPolygon(bbox);

    const results = tocke.filter(point => {
        
    const tocka = turf.point(point);
    return turf.booleanPointInPolygon(tocka, bboxPolygon);
    });

    return results;
}
    

  

/**
 * measurementController.js
 *
 * @description :: Server-side logic for managing measurements.
 */
module.exports = {

    /**
     * measurementController.list()
     */
    listAll: function (req, res) {
        MeasurementModel.find()
        .populate('measuredBy')
        .exec(function (err, measurements) {
            if (err) {
                return res.status(500).json({
                    message: 'Error when getting photo.',
                    error: err
                });
            }
            var data = [];
            data.measurements = measurements;
            return res.json(measurements);
        });
    },

    listByUser: function (req, res){
        var userId = req.params.user_id;
        MeasurementModel.find({measuredBy: userId})
        .populate('measuredBy')
        .exec(function (err, measurements) {
            if (err) {
                return res.status(500).json({
                    message: 'Error when getting photo.',
                    error: err
                });
            }
            var data = [];
            data.measurements = measurements;
            return res.json(measurements);
        });
    },

    listByTimeFrame: function (req, res){
        var start_time = req.params.date_start;
        var end_time = req.params.date_end;
        MeasurementModel.find({
            time: {
                $gte: start_time,
                $lt: end_time
            }
        }).populate('measuredBy')
        .exec(function (err, measurements) {
            if (err) {
                return res.status(500).json({
                    message: 'Error when getting photo.',
                    error: err
                });
            }
            var data = [];
            data.measurements = measurements;
            return res.json(measurements);
        });
    },

    listNearby: async function (req, res) {
        try {
            const measurements = await MeasurementModel.find()
                .select('speed location.coordinates -_id')
                .populate('measuredBy');
    
            const coordinates = measurements.map(measurement => {
                return {
                    coords: [
                        measurement.location.coordinates[1],
                        measurement.location.coordinates[0]
                    ],
                    speed: measurement.speed
                };
            });
    
            const center = [req.params.lat, req.params.lon]; // Središčna točka
            const radius = req.params.radius; // Radij v kilometrih
    
            // Najdi koordinate znotraj določenega radija
            const pointsWithinRadius = findPointsWithinRadius(center, coordinates, radius);
    
            return res.status(200).json({ pointsWithinRadius });
        } catch (error) {
            return res.status(500).json({
                message: 'Error when getting measurements.',
                error: error
            });
        }
    },
    
    listWithinRectangle: async function (req, res) {
        try {
            const measurements = await MeasurementModel.find()
                .select('location.coordinates -_id')
                .populate('measuredBy');
    
            const coordinates = measurements.map(measurement => {
                return [
                    measurement.location.coordinates[1],
                    measurement.location.coordinates[0]
                    
                ];
            });
            
            //const topLeftKoordinate = [46.572, 15.64]; // Levo zgoraj
            //const bottomRightKoordinate = [46.562, 15.65]; // Desno spodaj
          
            const topLeftKoordinate = [req.params.lat1, req.params.lon1]
            const bottomRightKoordinate = [req.params.lat2, req.params.lon2]
            const pointsWithinRectangle = isWithinRectangle(topLeftKoordinate, bottomRightKoordinate, coordinates);
    
            return res.status(200).json({ pointsWithinRectangle });
        } catch (error) {
            return res.status(500).json({
                message: 'Error when getting measurements.',
                error: error
            });
        }
    },
    
    
    /**
     * measurementController.show()
     */
    show: function (req, res) {
        var id = req.params.id;

        MeasurementModel.findOne({_id: id}, function (err, measurement) {
            if (err) {
                return res.status(500).json({
                    message: 'Error when getting measurement.',
                    error: err
                });
            }

            if (!measurement) {
                return res.status(404).json({
                    message: 'No such measurement'
                });
            }

            return res.json(measurement);
        }).populate("measuredBy");
    },

    /**
     * measurementController.create()
     */
    create: function (req, res) {
        var measurement = new MeasurementModel({
			speed : req.body.speed,
			type : req.body.type,
			provider : req.body.provider,
			time : req.body.time,
			location : req.body.location,
			measuredBy : req.body.measuredBy
        });

        measurement.save(function (err, measurement) {
            if (err) {
                return res.status(500).json({
                    message: 'Error when creating measurement',
                    error: err
                });
            }

            return res.status(201).json(measurement);
        });
    },

    createMany: function (req, res){
        var measurements = req.body.measurements;

        if (!Array.isArray(measurements)) {
            return res.status(400).json({
                message: 'Invalid request format. Expected an array of measurements.'
            });
        }
    
        MeasurementModel.insertMany(measurements, function(err, createdMeasurements) {
            if (err) {
                return res.status(500).json({
                    message: 'Error when creating measurements',
                    error: err
                });
            }
    
            return res.status(201).json(createdMeasurements);
        });
    },

    /**
     * measurementController.update()
     */
    update: function (req, res) {
        var id = req.params.id;

        MeasurementModel.findOne({_id: id}, function (err, measurement) {
            if (err) {
                return res.status(500).json({
                    message: 'Error when getting measurement',
                    error: err
                });
            }

            if (!measurement) {
                return res.status(404).json({
                    message: 'No such measurement'
                });
            }

            measurement.speed = req.body.speed ? req.body.speed : measurement.speed;
			measurement.type = req.body.type ? req.body.type : measurement.type;
			measurement.provider = req.body.provider ? req.body.provider : measurement.provider;
			measurement.time = req.body.time ? req.body.time : measurement.time;
			measurement.location = req.body.location ? req.body.location : measurement.location;
			measurement.measuredBy = req.body.hasOwnProperty('measuredBy')  ? req.body.measuredBy : measurement.measuredBy;


            measurement.save(function (err, measurement) {
                if (err) {
                    return res.status(500).json({
                        message: 'Error when updating measurement.',
                        error: err
                    });
                }

                return res.json(measurement);
            });
        });
    },

    /**
     * measurementController.remove()
     */
    remove: async function (req, res) {
        var id = req.params.id;
    
        try {
            // Find the measurement by its ID
            const measurement = await MeasurementModel.findById(id);
    
            // If measurement not found
            if (!measurement) {
                return res.status(404).json({ message: 'Measurement not found' });
            }
    
            // Check if the user is an admin or the user who made the measurement
            if (req.user && (req.user.userId === measurement.measuredBy.toString() || req.user.admin === true)) {
                // Proceed to remove the measurement
                await MeasurementModel.findByIdAndRemove(id);
                return res.status(204).json();  // Successfully deleted, no content to return
            } else {
                return res.status(403).json({ message: 'Forbidden: Only admins or the user who created the measurement can delete it' });
            }
        } catch (err) {
            return res.status(500).json({
                message: 'Error when deleting the measurement.',
                error: err
            });
        }
    }
};
