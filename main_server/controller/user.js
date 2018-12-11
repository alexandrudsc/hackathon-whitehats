// Import contact model
User = require('../model/user');

// Handle index actions
exports.index = function(req, res) {
  User.get(function(err, users) {
    if (err) {
      return res.json({
        status: "error",
        message: err,
      });
    }
    res.json({
      status: "success",
      message: "Users retrieved successfully",
      data: users
    });
  });
};

// Handle create user actions
exports.new = function(req, res) {
  if( !req.body.phone || req.body.phone.length != 10){
    return res.json({
      status: "error",
      message: "Incorrect phone number",
    });
  }
  var user = new User();
  user.name = req.body.name ? req.body.name : user.name;
  user.email = req.body.email;
  user.token = req.body.token;
  user._id = req.body.phone;
  user.friends = req.body.friends ? req.body.friends : user.friends;

  // save the user and check for errors
  user.save(function(err) {
    if (err) {
      return res.json({
        status: "error",
        message: err,
      });
    }
    res.json({
      status: 'success',
      message: 'New user created!',
      data: user
    });
  });
};


// Handle view user info
exports.view = function(req, res) {
  User.findById(req.params.phone, function(err, user) {
    if (err)
      res.send(err);
    res.json({
      status: 'success',
      message: 'User details loading..',
      data: user
    });
  });
};

// Handle update user info
exports.update = function (req, res) {

    User.findById(req.params.phone, function (err, user) {
        if (err){
          res.send(err);
          return;
        }

        if( !user ){
          res.json({
            status: 'error',
            message: 'User not found'
          })
          return;
        }

        user.name = req.body.name ? req.body.name : user.name;
        user.email = req.body.email;
        user.token = req.body.token;
        user._id = req.body.phone ? req.body.phone : user._id;
        user.friends = req.body.friends ? req.body.friends : user.friends;

    // save the contact and check for errors
        user.save(function (err) {
            if (err){
              res.json(err);
              return;
            }
            res.json({
              status: 'success',
              message: 'User Info updated',
              data: contact
            });
        });
    });
};


// Handle delete user
exports.delete = function(req, res) {
  User.deleteOne({
    _id: req.params.phone
  }, function(err, user) {
    if (err || !user){
      res.send(err);
      return;
    }

    if(user.n < 1){
      res.json({
        status: "error",
        message: 'user does not exist'
      })
      return;
    }

    res.json({
      status: "success",
      message: 'user deleted'
    });
  });
};
