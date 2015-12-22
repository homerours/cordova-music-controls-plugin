module.exports = {
  updateCallback: function () {},
  
  create: function (data, successCallback, errorCallback) {
    if (data.artist === undefined) {
      data.artist = '';
    }
    if (data.track === undefined) {
      data.track = '';
    }
    if (data.cover === undefined) {
      data.cover = '';
    }
    if (data.ticker === undefined) {
      data.ticker = '';
    }
    if (data.isPlaying === undefined) {
      data.isPlaying = true;
    }
    if (data.hasPrev === undefined) {
      data.hasPrev = true;
    }
    if (data.hasNext === undefined) {
      data.hasNext = true;
    }
    if (data.hasClose === undefined) {
      data.hasClose = false;
    }
    if (data.dismissable === undefined) {
      data.dismissable = false;
    }
    
    cordova.exec(successCallback, errorCallback, 'MusicControls', 'create', [data]);
  },
  
  updateIsPlaying: function (isPlaying, successCallback, errorCallback) {
    cordova.exec(successCallback, errorCallback, 'MusicControls', 'updateIsPlaying', [{isPlaying: isPlaying}]);
  },
  
  destroy: function (successCallback, errorCallback) {
    cordova.exec(successCallback, errorCallback, 'MusicControls', 'destroy', []);
  },
  
  // Register callback
  subscribe: function (onUpdate) {
    module.exports.updateCallback = onUpdate;
  },
  // Start listening for events
  listen: function () {
    cordova.exec(module.exports.receiveCallbackFromNative, function (res) {
    }, 'MusicControls', 'watch', []);
  },
  receiveCallbackFromNative: function (messageFromNative) {
    module.exports.updateCallback(messageFromNative);
    cordova.exec(module.exports.receiveCallbackFromNative, function (res) {
    }, 'MusicControls', 'watch', []);
  }
  
};
