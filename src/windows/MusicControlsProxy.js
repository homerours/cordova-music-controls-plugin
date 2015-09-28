var mc = Windows.Media.MediaControl;
var onUpdate = function (event) { };

var onPlay = function () {
    onUpdate('music-controls-play');
},
onPause = function () {
    onUpdate('music-controls-pause');
},
onPlayPause = function () {
    if (mc.isPlaying)
        onPause();
    else
        onPlay();
},
onNext = function () {
    onUpdate('music-controls-next');
},
onPrev = function () {
    onUpdate('music-controls-previous');
};

cordova.commandProxy.add("MusicControls",{
    create: function (successCallback, errorCallback, datas) {
        var data = datas[0];

        //Handle events
        mc.addEventListener("playpausetogglepressed", onPlayPause, false);
		mc.addEventListener("playpressed", onPlay, false);
		mc.addEventListener("pausepressed", onPause, false);
		mc.addEventListener("previoustrackpressed", onPrev, false);
		mc.addEventListener("nexttrackpressed", onNext, false);

		if (!/^(f|ht)tps?:\/\//i.test(data.cover)) {
		    var cover = new Windows.Foundation.Uri("ms-appdata://" + data.cover);
		    mc.albumArt = cover;
		} else {
		    //TODO: Store image locally
		}

        //Set data
		mc.artistName = data.artist;
		mc.isPlaying = data.isPlaying;
		mc.trackName = data.track;
    },
    destroy: function (successCallback, errorCallback, datas) {
        //Remove events
        mc.removeEventListener("playpausetogglepressed", onPlayPause);
        mc.removeEventListener("playpressed", onPlay);
        mc.removeEventListener("pausepressed", onPause);
    },
    watch: function (_onUpdate, errorCallback, datas) {
        //Set callback
	    onUpdate = _onUpdate;
    }
});
