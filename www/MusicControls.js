module.exports = {
    onStatusUpdate : function(){},

    show: function(data,successCallback, errorCallback) {
        cordova.exec(successCallback, errorCallback, 'MusicControls', 'show', [data]);
    },

    listen : function(onUpdate, successCallback, errorCallback){
            this.onStatusUpdate = onUpdate;
            this.watchEvents();
            successCallback();
    },

    stop : function(successCallback,errorCallback){
        cordova.exec(successCallback, errorCallback, 'MusicControls', 'stop', []);
    },

    watchEvents : function(){
        var self = this;

        var onUpdateSuccess = function(action){
            self.onStatusUpdate(action);
            self.watchEvents();
        };
        cordova.exec(onUpdateSuccess, function(res){
            console.log(res);
        }, 'MusicControls', 'watch', []);
    }
};
