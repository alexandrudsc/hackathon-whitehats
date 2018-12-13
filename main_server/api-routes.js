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

router.route('/users/:name')
  .get(user_controller.search);

router.route('/user/:phone')
  .get(user_controller.view)
  .put(user_controller.update)
  .delete(user_controller.delete);

router.route('/user/:phone/friends')
  .get(user_controller.get_friends)
  .post(user_controller.append_friend);

router.route('/user/:phone/friend/:fphone/:fname?')
  .delete(user_controller.delete_friend);

router.route('/user/:phone/objects')
  .get(user_controller.get_objects)
  .post(user_controller.add_object);

router.route('/user/:phone/object/:ophone/:oname?')
  .delete(user_controller.delete_object);

router.route('/user/:phone/locations')
  .get(user_controller.get_locations)
  .post(user_controller.add_location);

router.route('/disasters')
  .get(disaster_controller.index)
  .post(disaster_controller.new)
  .delete(disaster_controller.delete_all);

router.route('/disaster/:mongoId([0-9a-f]{24})')
  .get(disaster_controller.view)
  .put(disaster_controller.update)
  .delete(disaster_controller.delete);

router.route('/disaster/:mongoId([0-9a-f]{24})/disable')
  .put(disaster_controller.disable);

router.route('/disaster/:mongoId([0-9a-f]{24})/enable')
  .put(disaster_controller.enable);

router.route('/disasters/:longitude(-?\\d+((.\\d+)?))/:latitude(-?\\d+((.\\d+)?))/:radius(\\d{1,5})')
  .get(disaster_controller.in_radius);


// Export API routes
module.exports = router;
