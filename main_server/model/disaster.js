var mongoose = require('mongoose');

// Setup schema
var disaster_schema = mongoose.Schema({
  name: {
    type: String,
    required: true
  },
  email: {
    type: String,
    required: true
  },
  token: {
    type: String,
    required: true
  },
  reg_date: {
    type: Date,
    default: Date.now
  },
  friends: [{
    _id: String,
    name: String
  }]
});

// Export Contact model
var Disaster = module.exports = mongoose.model('disaster', disaster_schema);


module.exports.get = function (callback, limit) {
  Disaster.find(callback).limit(limit);
}
