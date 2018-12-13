var mongoose = require('mongoose');

const point_schema_time = new mongoose.Schema({
  type: {
    type: String,
    enum: ['Point'],
    default: 'Point'
  },
  coordinates: {
    type: [Number],
    required: true
  },
  time: {
    type: Date,
    default: Date.now
  }
});

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
  }],
  objects: [{
    _id: String,
    name: String
  }],
  last_locations: {
    type: [point_schema_time]
  }
});

// Export Contact model
var User = module.exports = mongoose.model('user', user_schema);

module.exports.get = function (callback, limit) {
  User.find(callback).limit(limit);
}
