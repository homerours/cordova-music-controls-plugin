# Cordova Music Controls Plugin

Show controls for music playing in the notifications.

## Supported platforms
- Android (>= 4.1)

## Installation
`cordova plugin add https://github.com/homerours/cordova-music-controls-plugin`

## Methods

Show or update notification:
```javascript
var data = {
    artist    : ‘artist’,
    song      : ‘song’,
    image     : ‘imageNativeURL’,
    isPlaying : true
};
MusicControls.show(data, onSuccess, onError);
```

To listen for next action
```javascript
function listenAction(action) {
    if (action===’music-controls-next’){
        // Some code...
    }
    if (action===’music-controls-previous’){
        // Some code...
    }
    if (action===’music-controls-pause’){
        // Some code...
    }
    if (action===’music-controls-play’){
        // Some code...
    }

}

MusicControls.listen(listenAction, onSuccess, onError);
```
## Remarks
This is my first Cordova plugin, and also my first attempt on Android. This plugin might not be perfect and might not be considered for production.
