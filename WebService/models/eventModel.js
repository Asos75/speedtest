var mongoose = require('mongoose');
var Schema   = mongoose.Schema;

var eventSchema = new Schema({
	'name' : String,
	'type' : String,
	'online' : Boolean,
	location: {
    type: String,
    required: function() {
      return this.online; // Location is required if the event is online
    }
}
});

module.exports = mongoose.model('event', eventSchema);
