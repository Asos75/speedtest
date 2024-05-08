var mongoose = require('mongoose');
var Schema   = mongoose.Schema;

var measurementSchema = new Schema({
	'speed' : Number,
	tip: {
    type: String,
    enum: ['wifi', 'data']
	},
	'provider' : String,
	'time' : Date,
	'location' : String,
	'user' : {
	 	type: Schema.Types.ObjectId,
	 	ref: 'user'
	}
});

module.exports = mongoose.model('measurement', measurementSchema);
