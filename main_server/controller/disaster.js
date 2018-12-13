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
    //notify("1","1");
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
  disaster.level = cnt.level ? cnt.level : disaster.level;


  // save the disaster and check for errors
  disaster.save(function(err) {
    if (err) {
      return res.json({
        status: "error",
        message: err,
      });
    }
    var d = disaster;
    if( d.to_notify === 'public' || d.to_notify === 'public+private' ){
      var message = `${d.notifier} issued "${d.title}" in your proximity`;
      User.notify_all_in_radius(d.location.coordinates[0],
                                d.location.coordinates[1],
                                20000,
                                message,
                                true);
    } else { //disaster comes from an object
      User.notify_owners(disaster);
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
    disaster.level = cnt.level ? cnt.level : disaster.level;

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

exports.disable = function(req, res) {
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
    disaster.active = false;

    disaster.save(function(err) {
      if (err) {
        return res.json({
          status: "error",
          message: err,
        });
      }
      res.json({
        status: 'success',
        message: 'Disaster disabled!',
        data: disaster
      });
    });
  });
}

exports.enable = function(req, res) {
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
    disaster.active = true;

    disaster.save(function(err) {
      if (err) {
        return res.json({
          status: "error",
          message: err,
        });
      }
      res.json({
        status: 'success',
        message: 'Disaster marked as active!',
        data: disaster
      });
    });
  });
}

exports.delete_all = function(req, res) {
  Disaster.deleteMany({}, function(err, disaster) {
    if (err){
      res.send(err);
      return;
    }
    res.json({
      status: 'success',
      message: 'All disasters have been removed!',
      data: disaster
    });
  });
}

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
    User.notify('226103001845682', { msg: 'Acesta este un test'});
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
