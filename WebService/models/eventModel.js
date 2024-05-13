var mongoose = require('mongoose');
var Schema   = mongoose.Schema;

var eventSchema = new Schema({
	'name' : String,
	'type' : String,
	'online' : Boolean,
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
});

module.exports = mongoose.model('event', eventSchema);
