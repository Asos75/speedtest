var UserModel = require('../models/userModel.js');

/**
 * user Controller.js
 *
 * @description :: Server-side logic for managing user s.
 */
module.exports = {

    /**
     * user Controller.list()
     */
    list: function (req, res) {
        UserModel.find(function (err, users) {
            if (err) {
                return res.status(500).json({
                    message: 'Error when getting user .',
                    error: err
                });
            }

            return res.json(users);
        });
    },

    /**
     * user Controller.show()
     */
    show: function (req, res) {
        var id = req.params.id;

        UserModel.findOne({_id: id}, function (err, user ) {
            if (err) {
                return res.status(500).json({
                    message: 'Error when getting user .',
                    error: err
                });
            }

            if (!user ) {
                return res.status(404).json({
                    message: 'No such user '
                });
            }

            return res.json(user );
        });
    },

    /**
     * user Controller.create()
     */
    create: function (req, res) {
        var user  = new UserModel({
			username : req.body.username,
			password : req.body.password,
			measurements : req.body.measurements,
			admin : req.body.admin
        });

        user .save(function (err, user ) {
            if (err) {
                return res.status(500).json({
                    message: 'Error when creating user ',
                    error: err
                });
            }

            return res.status(201).json(user );
        });
    },

    /**
     * user Controller.update()
     */
    update: function (req, res) {
        var id = req.params.id;

        UserModel.findOne({_id: id}, function (err, user ) {
            if (err) {
                return res.status(500).json({
                    message: 'Error when getting user ',
                    error: err
                });
            }

            if (!user ) {
                return res.status(404).json({
                    message: 'No such user '
                });
            }

            user .username = req.body.username ? req.body.username : user .username;
			user .password = req.body.password ? req.body.password : user .password;
			user .measurements = req.body.measurements ? req.body.measurements : user .measurements;
			user .admin = req.body.admin ? req.body.admin : user .admin;
			
            user .save(function (err, user ) {
                if (err) {
                    return res.status(500).json({
                        message: 'Error when updating user .',
                        error: err
                    });
                }

                return res.json(user );
            });
        });
    },

    /**
     * user Controller.remove()
     */
    remove: function (req, res) {
        var id = req.params.id;

        UserModel.findByIdAndRemove(id, function (err, user ) {
            if (err) {
                return res.status(500).json({
                    message: 'Error when deleting the user .',
                    error: err
                });
            }

            return res.status(204).json();
        });
    }
};
