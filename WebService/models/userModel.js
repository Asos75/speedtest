var mongoose = require('mongoose');
var Schema   = mongoose.Schema;

var userSchema = new Schema({
	'username' : String,
	'password' : String,
	'measurements' : {
	 	type: Schema.Types.ObjectId,
	 	ref: 'measurement'
	},
	'admin' : Boolean
});

module.exports = mongoose.model('user ', userSchema);
