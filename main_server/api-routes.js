// Initialize express router
let router = require('express').Router();

// Set default API response
router.get('/', function (req, res) {
  res.json({
    status: 'API Its Working',
    message: 'Welcome to RESTHub crafted with love!',
  });
});

// Import contact controller
var user_controller = require('./controller/user');
var disaster_controller = require('./controller/disaster');

// Contact routes
router.route('/users')
  .get(user_controller.index)
  .post(user_controller.new);

router.route('/user/:phone')
  .get(user_controller.view)
  // .patch(user_controller.update)
  .put(user_controller.update)
  .delete(user_controller.delete);

router.route('/disasters')
  .get(disaster_controller.index)
  .post(disaster_controller.new);

router.route('/disaster/:mongoId([0-9a-f]{24})')
  .get(disaster_controller.view)
  .put(disaster_controller.update)
  .delete(disaster_controller.delete);

router.route('/disasters/:longitude(-?\\d+((.\\d+)?))/:latitude(-?\\d+((.\\d+)?))/:radius(\\d{1,4})')
  .get(disaster_controller.in_radius);


// Export API routes
module.exports = router;
