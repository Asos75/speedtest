var mongoose = require('mongoose');
var Schema   = mongoose.Schema;
const bcrypt = require('bcrypt');

var userSchema = new Schema({
	'username' : String,
	'password' : String,
	'email': String,
	'admin' : Boolean
});

userSchema.statics.authenticate = function(username, password, callback){
	User.findOne({username: username})
	.exec(function(err, user){
		if(err){
			return callback(err);
		} else if(!user) {
			var err = new Error("User not found.");
			err.status = 401;
			return callback(err);
		}
		
		bcrypt.compare(password, user.password, function(err, result){
			console.log("err", err)
			if(result === true){
				return callback(null, user);
			} else{
				return callback();
			}
		});
		 
	});
}

var User = mongoose.model('user', userSchema);
module.exports = User;
