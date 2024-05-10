var mongoose = require('mongoose');
var Schema   = mongoose.Schema;
const bcrypt = require('bcrypt');



var userSchema = new Schema({
	'username' : String,
	'password' : String,
	'email': String,
	'measurements' : {
	 	type: Schema.Types.ObjectId,
	 	ref: 'measurement'
	},
	'admin' : Boolean
});

userSchema.pre('save', function(next){
	var user = this;
	bcrypt.hash(user.password, 10, function(err, hash){
			if(err){
					return next(err);
			}
			user.password = hash;
			next();
	});
});

module.exports = mongoose.model('user', userSchema);
