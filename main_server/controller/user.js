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
    if (err){
      res.send(err);
      return;
    }
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
        //user._id = req.body.phone ? req.body.phone : user._id;
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
              data: user
            });
        });
    });
};

exports.append_friend = function (req, res) {
  User.findById(req.params.phone, function (err, user) {
    if (err){
      res.send({
        status: 'error',
        message: err
      });
      return;
    }

    if( !user ){
      res.json({
        status: 'error',
        message: 'User not found'
      })
      return;
    }

    if(req.body.friend){
      user.friends.push({
        _id: req.body.friend.phone,
        name: req.body.friend.name
      });
    }
    else {
      res.json({
        status: 'error',
        message: 'Friend not given'
      })
      return;
    }

    user.save(function (err) {
      if (err){
        res.json(err);
        return;
      }
      res.json({
        status: 'success',
        message: 'User friends added',
        data: user
      });
    });
  });
};

exports.get_friends = function(req, res) {
  User.findById(req.params.phone, 'friends', function (err, friends) {
    if (err){
      res.json({
        status: 'error',
        message: err
      });
      return;
    }
    res.json({
      status: 'success',
      message: 'User friends loading..',
      data: friends
    });
  })
}

exports.delete_friend = function(req, res) {
  User.findById(req.params.phone, 'friends', function (err, friends) {
    if (err){
      res.json({
        status: 'error',
        message: err
      });
      return;
    }
    var index = friends.friends.findIndex(function(item, i){
      return item._id === req.params.fphone;
    });

    if(index === -1){
      res.json({
        status: 'error',
        message: 'Friend does not exist'
      })
      return;
    }

    friends.friends.splice(index, 1);

    friends.save(function (err) {
      if (err){
        res.json(err);
        return;
      }
      res.json({
        status: 'success',
        message: 'Friend deleted successfully',
        data: friends.friends
      });
    });
  })
}

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

//*** NON_DB controls ***//

var admin = require("firebase-admin");

var serviceAccount = require("../.bonopastore-firebase-adminsdk-n2vya-0f72757f69.json");

admin.initializeApp({
  credential: admin.credential.cert(serviceAccount),
  databaseURL: "https://bonopastore.firebaseio.com"
});

exports.notify = function(phone, msg) {
  User.findById(phone, function(err, user) {
    if(!user){
      throw new Error("User not found");
    }

    var message = {
      data: msg,
      token: user.token
    }

    // Send a message to the device corresponding to the provided
    // registration token.
    try{
      admin.messaging().send(message)
        .then((response) => {
          // Response is a message ID string.
          console.log('Successfully sent message:', response);
        })
        .catch((error) => {
          console.log('Error sending message:', error);
        });
    }
    catch(er){
      throw er;
    }
  });
}
