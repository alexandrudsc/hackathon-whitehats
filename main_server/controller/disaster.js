Disaster = require('../model/disaster');
var User = require('./user');

exports.index = function(req, res) {
  try{
    User.notify('0123456789', { msg: 'Acesta este un test'});
    res.json({
      status: "success",
      message: "Am trimis notificarea"
    })
  }catch(ex){
    res.json({
      status: "error",
      message: ex.toString()
    })
  }
};

// exports.new = function(req, res) {
//   if( !req.body.phone || req.body.phone.length != 10){
//     return res.json({
//       status: "error",
//       message: "Incorrect phone number",
//     });
//   }
//   var user = new User();
//   user.name = req.body.name ? req.body.name : user.name;
//   user.email = req.body.email;
//   user.token = req.body.token;
//   user._id = req.body.phone;
//   user.friends = req.body.friends ? req.body.friends : user.friends;
//
//   // save the user and check for errors
//   user.save(function(err) {
//     if (err) {
//       return res.json({
//         status: "error",
//         message: err,
//       });
//     }
//     res.json({
//       status: 'success',
//       message: 'New user created!',
//       data: user
//     });
//   });
// };
