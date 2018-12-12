Disaster = require('../model/disaster');
var User = require('./user');

exports.index = function(req, res) {
  Disaster.get(function(err, disasters){
    if (err) {
      return res.json({
        status: "error",
        message: err,
      });
    }
    res.json({
      status: "success",
      message: "Disasters retrieved successfully",
      data: disasters
    });
  })
};

// Handle create disaster actions
exports.new = function(req, res) {
  var cnt = req.body;

  if( !cnt.title || cnt.title.length < 3){
    return res.json({
      status: "error",
      message: "Please provide an understandable disaster title"
    });
  }

  var disaster = new Disaster();
  //required attributes
  disaster.title = cnt.title;
  disaster.location = cnt.location;
  disaster.notifier = cnt.notifier;

  //optional attributes
  disaster.description = cnt.description ?
                                    cnt.description : disaster.description;
  disaster.radius = cnt.radius ? cnt.radius : disaster.radius;
  disaster.to_notify = cnt.to_notify ? cnt.to_notify : disaster.to_notify;
  disaster.end_time = cnt.end_time ? cnt.end_time : disaster.end_time;

  // save the disaster and check for errors
  disaster.save(function(err) {
    if (err) {
      return res.json({
        status: "error",
        message: err,
      });
    }
    res.json({
      status: 'success',
      message: 'Disaster created!',
      data: disaster
    });
  });
};

exports.update = function(req, res) {
  var cnt = req.body;
  Disaster.findById(req.params.mongoId, function(err, disaster) {
    if (err){
      res.send(err);
      return;
    }
    if (!disaster){
      res.json({
        status: 'error',
        message: 'Disaster does not exist'
      });
      return;
    }
    //required attributes
    disaster.title = cnt.title;
    disaster.location = cnt.location;
    disaster.notifier = cnt.notifier;

    //optional attributes
    disaster.description = cnt.description ?
                                      cnt.description : disaster.description;
    disaster.radius = cnt.radius ? cnt.radius : disaster.radius;
    disaster.to_notify = cnt.to_notify ? cnt.to_notify : disaster.to_notify;
    disaster.end_time = cnt.end_time ? cnt.end_time : disaster.end_time;

    disaster.save(function(err) {
      if (err) {
        return res.json({
          status: "error",
          message: err,
        });
      }
      res.json({
        status: 'success',
        message: 'Disaster modified!',
        data: disaster
      });
    });
  });
};

exports.view = function(req, res) {
  Disaster.findById(req.params.mongoId, function(err, disaster) {
    if (err){
      res.json({
        status: 'error',
        message: err
      });
      return;
    }
    if (!disaster){
      res.json({
        status: 'error',
        message: 'Disaster does not exist'
      });
      return;
    }
    res.json({
      status: 'success',
      message: 'Disaster details loading..',
      data: disaster
    });
  });
};

exports.delete = function(req, res) {
  Disaster.deleteOne({
    _id: req.params.mongoId
  }, function(err, disaster) {
    if (err || !disaster){
      res.json({
        status: 'error',
        message: err
      });
      return;
    }

    if(disaster.n < 1){
      res.json({
        status: "error",
        message: 'disaster does not exist'
      })
      return;
    }

    res.json({
      status: "success",
      message: 'disaster deleted'
    });
  });
};

exports.in_radius = function(req, res) {
  var lng = req.params.longitude;
  var ltd = req.params.latitude;
  var radius = req.params.radius;

  Disaster.find({
    location: {
      $geoWithin: {
        $centerSphere: [[lng, ltd], radius / 6378.13]
      }
    }
  }, function(err, disasters) {
    if (err){
      res.json({
        status: 'error',
        message: err
      });
      return;
    }
    res.json({
      status: 'success',
      message: `Loading disasters within ${radius} meters from the specified location`,
      data: disasters
    });
  });
}

//*** NON_DB controls ***//

function notify(phone, msg){
  try{
    User.notify('0123456789', { msg: 'Acesta este un test'});
    return ({
      status: "success",
      message: "Am trimis notificarea"
    });
  }catch(ex){
    return ({
      status: "error",
      message: ex.toString()
    })
  }
}
