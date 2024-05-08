var MobiletowerModel = require('../models/mobileTowerModel.js');

/**
 * mobileTowerController.js
 *
 * @description :: Server-side logic for managing mobileTowers.
 */
module.exports = {

    /**
     * mobileTowerController.list()
     */
    list: function (req, res) {
        MobiletowerModel.find(function (err, mobileTowers) {
            if (err) {
                return res.status(500).json({
                    message: 'Error when getting mobileTower.',
                    error: err
                });
            }

            return res.json(mobileTowers);
        });
    },

    /**
     * mobileTowerController.show()
     */
    show: function (req, res) {
        var id = req.params.id;

        MobiletowerModel.findOne({_id: id}, function (err, mobileTower) {
            if (err) {
                return res.status(500).json({
                    message: 'Error when getting mobileTower.',
                    error: err
                });
            }

            if (!mobileTower) {
                return res.status(404).json({
                    message: 'No such mobileTower'
                });
            }

            return res.json(mobileTower);
        });
    },

    /**
     * mobileTowerController.create()
     */
    create: function (req, res) {
        var mobileTower = new MobiletowerModel({
			location : req.body.location,
			operator : req.body.operator,
			type : req.body.type,
			confirmed : req.body.confirmed,
			locator : req.body.locator
        });

        mobileTower.save(function (err, mobileTower) {
            if (err) {
                return res.status(500).json({
                    message: 'Error when creating mobileTower',
                    error: err
                });
            }

            return res.status(201).json(mobileTower);
        });
    },

    /**
     * mobileTowerController.update()
     */
    update: function (req, res) {
        var id = req.params.id;

        MobiletowerModel.findOne({_id: id}, function (err, mobileTower) {
            if (err) {
                return res.status(500).json({
                    message: 'Error when getting mobileTower',
                    error: err
                });
            }

            if (!mobileTower) {
                return res.status(404).json({
                    message: 'No such mobileTower'
                });
            }

            mobileTower.location = req.body.location ? req.body.location : mobileTower.location;
			mobileTower.operator = req.body.operator ? req.body.operator : mobileTower.operator;
			mobileTower.type = req.body.type ? req.body.type : mobileTower.type;
			mobileTower.confirmed = req.body.confirmed ? req.body.confirmed : mobileTower.confirmed;
			mobileTower.locator = req.body.locator ? req.body.locator : mobileTower.locator;
			
            mobileTower.save(function (err, mobileTower) {
                if (err) {
                    return res.status(500).json({
                        message: 'Error when updating mobileTower.',
                        error: err
                    });
                }

                return res.json(mobileTower);
            });
        });
    },

    /**
     * mobileTowerController.remove()
     */
    remove: function (req, res) {
        var id = req.params.id;

        MobiletowerModel.findByIdAndRemove(id, function (err, mobileTower) {
            if (err) {
                return res.status(500).json({
                    message: 'Error when deleting the mobileTower.',
                    error: err
                });
            }

            return res.status(204).json();
        });
    }
};
