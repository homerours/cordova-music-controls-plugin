module.exports = {
    //onStatusUpdate : function(){},

    show: function(data,successCallback, errorCallback) {
        console.log('Show');
        cordova.exec(successCallback, errorCallback, 'MusicControls', 'show', [data]);
    },

    listen : function(onUpdate, successCallback, errorCallback){
        //this.onStatusUpdate = onUpdate;
        //this.watchEvents();
        successCallback("Start listening");
        cordova.exec(onUpdate, function(res){
            console.log('UNE ERREUR: '+res);
        }, 'MusicControls', 'watch', []);
    },

    stop : function(successCallback,errorCallback){
        cordova.exec(successCallback, errorCallback, 'MusicControls', 'stop', []);
    },

    //watchEvents : function(){
    //var self = this;

    //var onUpdateSuccess = function(action){
    //self.onStatusUpdate(action);
    //self.watchEvents();
    //};
    //console.log('Watch');
    //cordova.exec(onUpdateSuccess, function(res){
    //console.log(res);
    //}, 'MusicControls', 'watch', []);
    //}
};
