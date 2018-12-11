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
  if( req.body.phone.length != 10){
    return res.json({
      status: "error",
      message: "Incorrect phone number",
    });
  }
  var user = new User();
  user.name = req.body.name ? req.body.name : user.name;
  user.email = req.body.email;
  user._id = req.body.phone;

  // save the user and check for errors
  user.save(function(err) {
    if (err) {
      return res.json({
        status: "error",
        message: err,
      });
    }
    res.json({
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
      message: 'User details loading..',
      data: user
    });
  });
};

// Handle update user info
// exports.update = function (req, res) {
//
//     Contact.findById(req.params.contact_id, function (err, contact) {
//         if (err)
//             res.send(err);
//
//         contact.name = req.body.name ? req.body.name : contact.name;
//         contact.gender = req.body.gender;
//         contact.email = req.body.email;
//         contact.phone = req.body.phone;
//
//         // save the contact and check for errors
//         contact.save(function (err) {
//             if (err)
//                 res.json(err);
//             res.json({
//                 message: 'Contact Info updated',
//                 data: contact
//             });
//         });
//     });
// };


// Handle delete user
exports.delete = function(req, res) {
  User.remove({
    _id: req.params.phone
  }, function(err, contact) {
    if (err)
      res.send(err);

    res.json({
      status: "success",
      message: 'user deleted'
    });
  });
};