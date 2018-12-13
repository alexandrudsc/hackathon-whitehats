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
  if( !req.body.phone || req.body.phone.length != 15){
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


exports.get_locations = function(req, res) {
  User.findById(req.params.phone, 'last_locations', function (err, l) {
    if (err){
      res.json({
        status: 'error',
        message: err
      });
      return;
    }
    if( !l ){
      res.json({
        status: 'error',
        message: 'User not found'
      })
      return;
    }
    res.json({
      status: 'success',
      message: 'Loading locations...',
      data: l.last_locations
    });
  })
}

exports.add_location = function (req, res) {
  User.findById(req.params.phone, 'last_locations', function (err, ll) {
    if (err){
      res.send({
        status: 'error',
        message: err
      });
      return;
    }

    if( !ll ){
      res.json({
        status: 'error',
        message: 'User not found'
      })
      return;
    }

    if(req.body.location){
      ll.last_locations.unshift({
        coordinates: req.body.location.coordinates
      });
      var maxl = 5;
      if(ll.last_locations.length > maxl){
        ll.last_locations.splice(maxl, ll.last_locations.length - maxl)
      }
    }
    else {
      res.json({
        status: 'error',
        message: 'Location not given'
      })
      return;
    }

    ll.save(function (err) {
      if (err){
        res.json(err);
        return;
      }
      res.json({
        status: 'success',
        message: 'Location added',
        data: ll
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

exports.add_object = function (req, res) {
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

    if(req.body.object){
      user.objects.push({
        _id: req.body.object.phone,
        name: req.body.object.name
      });
    }
    else {
      res.json({
        status: 'error',
        message: 'Object not given'
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
        message: 'User object added',
        data: user
      });
    });
  });
};

exports.get_objects = function(req, res) {
  User.findById(req.params.phone, 'objects', function (err, objects) {
    if (err){
      res.json({
        status: 'error',
        message: err
      });
      return;
    }
    res.json({
      status: 'success',
      message: 'User objects loading..',
      data: objects
    });
  })
}

exports.delete_object = function(req, res) {
  User.findById(req.params.phone, 'objects', function (err, objects) {
    if (err){
      res.json({
        status: 'error',
        message: err
      });
      return;
    }
    var index = objects.objects.findIndex(function(item, i){
      return item._id === req.params.ophone;
    });

    if(index === -1){
      res.json({
        status: 'error',
        message: 'Object does not exist'
      })
      return;
    }

    objects.objects.splice(index, 1);

    objects.save(function (err) {
      if (err){
        res.json(err);
        return;
      }
      res.json({
        status: 'success',
        message: 'Object deleted successfully',
        data: objects.objects
      });
    });
  })
}

//*** NON_DB controls ***//

var admin = require("firebase-admin");

var serviceAccount = require("../.bonopastore-firebase-adminsdk-n2vya-0f72757f69.json");

admin.initializeApp({
  credential: admin.credential.cert(serviceAccount),
  databaseURL: "https://bonopastore.firebaseio.com"
});

function notify(phone, msg) {
  User.findById(phone, function(err, user) {
    if(!user){
      throw new Error("User not found");
    }

    var message = {
      data: { msg: msg },
      token: user.token
    }

    // Send a message to the device corresponding to the provided
    // registration token.
    console.log(message);
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
      console.log(`There was an error sending message to ${user.name}(${user._id})`)
    }
  });
}

exports.notify = notify;

function notify_all_in_radius(lng, lat, rad, msg, notify_friends = false) {
  User.find({
    last_locations: {
      $geoWithin: {
        $centerSphere: [[lng, lat], rad / 6378.13]
      }
    }
  }, function(err, users) {
    if (err){
      console.log({
        status: 'error',
        message: err
      });
      return;
    }
    // console.log({
    //   status: 'success',
    //   message: `Loading ${users.length} users within ${rad} meters from the specified location`,
    //   data: users
    // });
    // console.log(users);
    // return;
    if(notify_friends){
      users.forEach(function(item, index){
        notify(item._id, msg);
        var message = `${item.name} is caught in a disaster: "${msg}"`;
        notify_friends(item, message);
      })
    } else {
      users.forEach(function(item, index){
        notify(item._id, msg)
      })
    }
  });
}

exports.notify_all_in_radius = notify_all_in_radius;

function notify_friends(user, msg){
  user.friends.forEach(function(item, index){
    notify(item._id, msg);
  })
}

function notify_owners(disaster){
  User.find({
    objects: { _id: disaster.notifier }
  }, function(err, users) {
      if (err){
        console.log({
          status: 'error',
          message: err
        });
        return;
      }
      if( users.n < 1 ){
        console.log(`No owner for object ${disaster.notifier} found`)
        return;
      }
      var msg = `Your object ${disaster.notifier} notified a disaster: ${disaster.title} - ${disaster.description}`;
      users.forEach(function(item, index){
        notify(item._id, msg);
      })
  });
}

exports.notify_owners = notify_owners;
