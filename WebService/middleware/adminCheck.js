const userController = require('../controllers/userController.js');
var UserModel = require('../models/userModel.js');

async function adminCheck (req, res, next) {
    var current = await UserModel.findById(req.user.userId).exec()
    if(req.user && current.admin == true){
        next()
    } else {
        res.status(403).json({ message: 'Forbidden: Admins only' });
    }
}

module.exports = adminCheck;