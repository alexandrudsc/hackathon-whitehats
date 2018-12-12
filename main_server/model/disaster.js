var mongoose = require('mongoose');

const point_schema = new mongoose.Schema({
  type: {
    type: String,
    enum: ['Point'],
    default: 'Point'
  },
  coordinates: {
    type: [Number],
    required: true
  }
});

// Setup schema
var disaster_schema = mongoose.Schema({
  title: {
    type: String,
    required: true
  },
  description: String,
  location: {
    type: point_schema,
    required: true
  },
  radius: {
    type: Number,
    default: 1
  },
  active: {
    type: Boolean,
    default: true
  },
  notifier: {
    type: String,
    required: true
  },
  to_notify: {
    type: String,
    enum: ['public', 'private', 'public+private'],
    default: 'public'
  },
  start_time: {
    type: Date,
    default: Date.now
  },
  end_time: {
    type: Date
  }
});

// Export Contact model
var Disaster = module.exports = mongoose.model('disaster', disaster_schema);


module.exports.get = function (callback, limit) {
  Disaster.find(callback).limit(limit);
}
