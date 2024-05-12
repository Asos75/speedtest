var MeasurementModel = require('../models/measurementModel.js');

/**
 * measurementController.js
 *
 * @description :: Server-side logic for managing measurements.
 */
module.exports = {

    /**
     * measurementController.list()
     */
    list: function (req, res) {
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
        });
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
			user : req.body.user
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
			measurement.user = req.body.user ? req.body.user : measurement.user;
			
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
    remove: function (req, res) {
        var id = req.params.id;

        MeasurementModel.findByIdAndRemove(id, function (err, measurement) {
            if (err) {
                return res.status(500).json({
                    message: 'Error when deleting the measurement.',
                    error: err
                });
            }

            return res.status(204).json();
        });
    }
};
