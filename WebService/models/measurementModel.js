var mongoose = require('mongoose');
var Schema   = mongoose.Schema;

var measurementSchema = new Schema({
	'speed' : Number,
	'type': {
    type: String,
    enum: ['wifi', 'data']
	},
	'provider' : String,
	'time' : Date,
	'location': {
    type: {
      type: String, 
      enum: ['Point'], 
      required: true
    },
    coordinates: {
      type: [Number],
      required: true
    }
  },
	'measuredBy' : {
	 	type: Schema.Types.ObjectId,
	 	ref: 'user'
	}
});

measurementSchema.index({ location: '2dsphere' });

module.exports = mongoose.model('measurement', measurementSchema);
