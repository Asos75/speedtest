const { Error } = require('mongoose');
var UserModel = require('../models/userModel.js');
var bcrypt = require('bcrypt');
const jwt = require('jsonwebtoken');


/**
 * user Controller.js
 *
 * @description :: Server-side logic for managing user s.
 */
module.exports = {

    /**
     * user Controller.list()
     */
    list: async function (req, res) {
        try {
            const users = await UserModel.find();
            return res.status(200).json({users});
        } catch (error) {
            return res.status(500).json({
                message: 'Error when getting user .',
                error: err
            });
        }
        
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
    create: async function (req, res) {
        // Preverimo, ali je geslo prisotno v zahtevku
        if (!req.body.password) {
            return res.status(400).json({
                message: 'Password is required',
                error: 'Password is missing in request body'
            });
        }
    
        try {
            // Hashiraj geslo
            const hashedPassword = await bcrypt.hash(req.body.password, 10);
    
            // Ustvari nov zapis uporabnika s hashiranim geslom
            const user = new UserModel({
                username : req.body.username,
                password : hashedPassword,
                email : req.body.email,
                measurements : req.body.measurements,
                admin : req.body.admin
            });
    
            await user.save();
    
            const token = jwt.sign({ userId: user._id }, 'nice', { expiresIn: '1h' });
    
            // Vrne JWT žeton in preusmeri uporabnika na stran za prijavo
            return res.status(201).json({
                message: 'User created successfully',
                token: token
            });
        } catch (err) {
            console.error('Error when creating user:', err);
            return res.status(500).json({
                message: 'Error when creating user',
                error: err
            });
        }
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

            user.username = req.body.username ? req.body.username : user.username;
			user.password = req.body.password ? req.body.password : user.password;
			user.measurements = req.body.measurements ? req.body.measurements : user.measurements;
			user.admin = req.body.admin ? req.body.admin : user.admin;
			
            user.save(function (err, user) {
                if (err) {
                    return res.status(500).json({
                        message: 'Error when updating user.',
                        error: err
                    });
                }

                return res.json(user);
            });
        });
    },

    showRegister: function(req, res){
        res.render('user/register');
    },

    showLogin: function(req, res){
        res.render('user/login');
    },

    login: function(req, res, next){
        UserModel.authenticate(req.body.username, req.body.password, function(err, user){
            if(err || !user){
                var err = new Error('Wrong username or paassword');
                err.status = 401;
                return next(err);
            }
            req.session.userId = user._id;
            res.json(user);
        });
    },
     
    login: function(req, res, next){
        UserModel.authenticate(req.body.username, req.body.password, function(err, user){
            if(err || !user){
                var err = new Error('Wrong username or password');
                err.status = 401;
                return next(err);
            }
            // Ustvari JWT žeton in ga pošlje kot odgovor
            const jwt = require('jsonwebtoken');
            const token = jwt.sign({ userId: user._id }, 'your-secret-key');
            res.json({ token: token });
        });
    },

    profile: function(req, res, next) {
        UserModel.findById(req.session.userId)
            .exec(function(error, user) {
                if (error) {
                    return next(error);
                } else {
                    if (user === null) {
                        var err = new Error('Not authorized, go back!');
                        err.status = 400;
                        return next(err);
                    } else {
                        PhotoModel.findOne({ postedBy: req.session.userId })
                            .exec(function(err, photo) {
                                if (err) {
                                    return next(err);
                                }
                                if (photo) {
                                    user.path = photo.path;
                                } else {
                                    user.path = '/images/default.jpg';
                                }
                                return res.json(user);
                            });
                    }
                }
            });
    },


    logout: function(req, res, next){
        if(req.session){
            req.session.destroy(function(err){
                if(err){
                    return next(err);
                } else{
                    return res.status(201).json({});
                }
            });
        }
    },

    /**
     * userController.remove()
     */
    remove: function (req, res) {
        var id = req.params.id;

        UserModel.findByIdAndRemove(id, function (err, user) {
            if (err) {
                return res.status(500).json({
                    message: 'Error when deleting the user.',
                    error: err
                });
            }

            return res.status(204).json();
        });
    }
};
