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

// Contact routes
router.route('/users')
  .get(user_controller.index)
  .post(user_controller.new);

router.route('/user/:phone')
  .get(user_controller.view)
  // .patch(user_controller.update)
  // .put(user_controller.update)
  .delete(user_controller.delete);

// Export API routes
module.exports = router;
