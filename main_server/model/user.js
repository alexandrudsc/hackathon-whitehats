var mongoose = require('mongoose');

// Setup schema
var user_schema = mongoose.Schema({
  _id: String,
  name: {
    type: String,
    required: true
  },
  email: {
    type: String,
    required: true
  },
  reg_date: {
    type: Date,
    default: Date.now
  }
});

// Export Contact model
var User = module.exports = mongoose.model('user', user_schema);


module.exports.get = function (callback, limit) {
  User.find(callback).limit(limit);
}
