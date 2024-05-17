var express = require('express');
var router = express.Router();
var userController = require('../controllers/userController.js');
var authenticateToken = require('../middleware/authenticateToken.js');
var adminCheck = require('../middleware/adminCheck.js');

// GET routes
router.get('/', authenticateToken, userController.list);
router.get('/register', userController.showRegister);
router.get('/login', userController.showLogin);
router.get('/profile', userController.profile);
router.get('/profile/:id', userController.profile);
router.get('/logout', authenticateToken, userController.logout);
router.get('/:id', authenticateToken, userController.show);
router.get('/users/:id', authenticateToken, userController.show);

// POST routes
router.post('/register', userController.create);
router.post('/login', userController.login);

// PUT routes
router.put('/:id', authenticateToken, userController.update);

// DELETE routes
router.delete('/:id', authenticateToken, userController.remove);

// Preizkusna pot, ki zahteva veljaven JWT žeton
router.get('/test', authenticateToken, function(req, res) {
	// Če smo tukaj, pomeni, da je JWT žeton veljaven
	// Uporabnikov ID je na voljo v req.user.userId
	res.json({ message: 'JWT token is valid', userId: req.user.userId });
});

module.exports = router;
