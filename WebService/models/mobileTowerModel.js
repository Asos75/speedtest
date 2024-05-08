var mongoose = require('mongoose');
var Schema   = mongoose.Schema;

var mobileTowerSchema = new Schema({
	'location' : String,
	'operator' : String,
	'type' : String,
	'confirmed' : Boolean,
	'locator' : String
});

module.exports = mongoose.model('mobileTower', mobileTowerSchema);
