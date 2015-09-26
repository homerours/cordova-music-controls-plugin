module.exports = {
    create: function(data,successCallback, errorCallback) {
        cordova.exec(successCallback, errorCallback, 'MusicController', 'create', [data]);
    },
    destory: function(successCallback,errorCallback){
        cordova.exec(successCallback, errorCallback, 'MusicController', 'destory', []);
    },

    subscribe: function(onUpdate, successCallback, errorCallback){
        successCallback("Start listening");
        cordova.exec(onUpdate, function(res){ }, 'MusicController', 'watch', []);
    }
};
