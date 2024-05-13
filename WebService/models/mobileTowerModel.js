var mongoose = require('mongoose');
var Schema   = mongoose.Schema;

var mobileTowerSchema = new Schema({
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
	'operator' : String,
	'type' : String,
	'confirmed' : Boolean,
	'locator' : {
	 	type: Schema.Types.ObjectId,
	 	ref: 'user'
	}
});

module.exports = mongoose.model('mobileTower', mobileTowerSchema);
